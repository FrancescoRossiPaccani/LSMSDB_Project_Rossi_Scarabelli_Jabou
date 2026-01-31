import pandas as pd
import json

def transform_bookings():
    # 1. Load your Source of Truth collections
    try:
        with open('users_collection.json', 'r') as f:
            users_map = {u['_id']: u for u in json.load(f)}
            
        with open('reviews_collection.json', 'r') as f:
            # Group reviews by booking_id for quick matching
            reviews_list = json.load(f)
            reviews_map = {}
            for rev in reviews_list:
                bid = rev['booking_id']
                if bid not in reviews_map:
                    reviews_map[bid] = []
                reviews_map[bid].append(rev['_id'])
                
    except FileNotFoundError as e:
        print(f"Error: Required JSON files missing. {e}")
        return

    # 2. Load the raw bookings.csv
    df_bookings = pd.read_csv("bookings.csv")
    bookings_collection = []

    for _, row in df_bookings.iterrows():
        # Standardizing IDs
        booking_raw_id = str(row['booking_id'])[:8]
        booking_full_id = f"book_{booking_raw_id}"
        passenger_id = f"user_{str(row['passenger_id']).zfill(3)}"
        
        # Match reviews belonging to this booking
        # For your project, we assume the first two reviews found are P->D and D->P
        booking_reviews = reviews_map.get(booking_full_id, [])
        p_to_d = booking_reviews[0] if len(booking_reviews) > 0 else None
        d_to_p = booking_reviews[1] if len(booking_reviews) > 1 else None

        booking_doc = {
            "_id": booking_full_id,
            "bookingDate": row['created_at'],
            "pickupCode": f"PICK-{booking_raw_id.upper()}", # Generated for realism
            "ride_id": f"ride_{str(row['ride_id'])[:8]}",
            "finalPrice": float(row['price_paid']),
            "paymentStatus": "RELEASED" if row['booking_status'] == 'accepted' else "REFUNDED",
            "fundsReleasedAt": row['created_at'], # Simplification for simulation
            "passengerId": passenger_id,
            
            "review_refs": {
                "passenger_to_driver": p_to_d,
                "driver_to_passenger": d_to_p,
                "locations": {
                    # City removed as requested
                    "pickup": { "location": row['pickup_point'], "id": "loc_001" },
                    "dropoff": { "location": row['dropoff_point'], "id": "loc_002" }
                }
            }
        }
        bookings_collection.append(booking_doc)

    # 3. Save the final colorful bookings_collection.json
    with open('bookings_collection.json', 'w') as f:
        json.dump(bookings_collection, f, indent=2)
    
    print(f"✅ Success! {len(bookings_collection)} bookings linked with reviews created.")

if __name__ == "__main__":
    transform_bookings()