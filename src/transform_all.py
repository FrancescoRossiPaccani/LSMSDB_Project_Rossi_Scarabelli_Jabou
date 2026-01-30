import pandas as pd
import json

def run_pipeline():
    # --- 1. LOAD SOURCES OF TRUTH (JSON) ---
    try:
        with open('users_collection.json', 'r') as f:
            users_map = {u['_id']: u for u in json.load(f)}
        with open('cars_collection.json', 'r') as f:
            cars_map = {c['_id'].replace('car_', ''): c for c in json.load(f)}
        with open('routes_collection.json', 'r') as f:
            routes_map = {r['_id']: r for r in json.load(f)}
    except Exception as e:
        print(f"❌ Error loading JSON files: {e}")
        return

    # --- 2. LOAD LARGE CSV FILES ---
    try:
        # This now reads your ACTUAL files from your folder
        df_rides = pd.read_csv("rides.csv")
        df_books = pd.read_csv("bookings.csv")
    except Exception as e:
        print(f"❌ Error loading CSV files: {e}. Make sure 'rides.csv' and 'bookings.csv' are in the folder.")
        return

    # --- 3. TRANSFORM RIDES (Scale-Ready) ---
    rides_final = []
    rides_lookup = {} 

    for _, row in df_rides.iterrows():
        u_id = f"user_{str(int(row['driver_id'])).zfill(3)}"
        plate = str(row['car_plate']).strip()
        user_data = users_map.get(u_id, {})
        car_data = cars_map.get(plate, {})
        
        ride_doc = {
            "_id": f"ride_{str(row['ride_id'])[:8]}",
            "status": "OPEN",
            "driver": {
                "id": u_id,
                "name": f"{user_data.get('personalInfo', {}).get('name', 'N/A')} {user_data.get('personalInfo', {}).get('surname', '')}".strip(),
                "phone": user_data.get('personalInfo', {}).get('phone', 'N/A'),
                "avg_acceptance_rate": user_data.get('driverInfo', {}).get('avg_acceptance_rate', 0.82)
            },
            "car": {
                "model": f"{car_data.get('details', {}).get('brand', 'Unknown')} {car_data.get('details', {}).get('model', '')}".strip(),
                "plate": plate,
                "comfort": car_data.get('metadata', {}).get('comfort_level', 'BASIC')
            },
            "route": { "origin": row['start_point'], "destination": row['end_point'], "route_id": row['route_id'] },
            "booking_state": { "total_seats": int(row['available_seats']), "available_seats": int(row['available_seats']), "has_waiting_list": False },
            "base_price": float(row['price_total']),
            "metadata": { "created_at": row['departure_time'] }
        }
        rides_final.append(ride_doc)
        rides_lookup[ride_doc['_id']] = ride_doc

    # --- 4. TRANSFORM BOOKINGS & REVIEWS (Scale-Ready) ---
    bookings_final = []
    reviews_final = []

    for _, row in df_books.iterrows():
        b_raw_id = str(row['booking_id'])[:8]
        b_id = f"book_{b_raw_id}"
        r_id = f"ride_{str(row['ride_id'])[:8]}"
        p_id = f"user_{str(int(row['passenger_id'])).zfill(3)}"
        
        ride_ref = rides_lookup.get(r_id, {})
        d_id = ride_ref.get('driver', {}).get('id', 'unknown_driver')

        rev_p_id = f"rev_p_{b_raw_id}"
        rev_d_id = f"rev_d_{b_raw_id}"

        bookings_final.append({
            "_id": b_id,
            "bookingDate": row['created_at'],
            "ride_id": r_id,
            "passengerId": p_id,
            "finalPrice": float(row['price_paid']),
            "paymentStatus": "RELEASED",
            "review_refs": { "passenger_to_driver": rev_p_id, "driver_to_passenger": rev_d_id, 
                             "locations": { "pickup": row['pickup_point'], "dropoff": row['dropoff_point'] } }
        })

        # Only generate reviews if the booking was accepted/successful
        if "accepted" in str(row['booking_status']).lower():
            reviews_final.append({
                "_id": rev_p_id, "booking_id": b_id, "author_id": p_id, "target_id": d_id,
                "role": "PASSENGER_TO_DRIVER", "rating": 5, "comment": "Great trip!", "created_at": row['created_at']
            })
            reviews_final.append({
                "_id": rev_d_id, "booking_id": b_id, "author_id": d_id, "target_id": p_id,
                "role": "DRIVER_TO_PASSENGER", "rating": 5, "comment": "Good passenger.", "created_at": row['created_at']
            })

    # --- 5. SAVE EVERYTHING ---
    with open('rides_collection.json', 'w') as f: json.dump(rides_final, f, indent=2)
    with open('bookings_collection.json', 'w') as f: json.dump(bookings_final, f, indent=2)
    with open('reviews_collection.json', 'w') as f: json.dump(reviews_final, f, indent=2)
    
    print(f"🚀 SUCCESS! Processed {len(rides_final)} rides and {len(bookings_final)} bookings.")

if __name__ == "__main__":
    run_pipeline()