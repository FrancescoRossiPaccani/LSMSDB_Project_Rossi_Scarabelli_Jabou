import json
import random

def refit_reviews():
    # 1. Load your real data
    try:
        with open('bookings_collection.json', 'r') as f:
            bookings = json.load(f)
        with open('rides_collection.json', 'r') as f:
            rides = {r['_id']: r for r in json.load(f)}
    except Exception as e:
        print(f"Error: Make sure bookings and rides are generated first. {e}")
        return

    new_reviews = []
    
    # 2. Generate exactly 2 reviews for every successful Booking
    for book in bookings:
        # FIX: Changed 'status' to 'paymentStatus' to match your JSON
        # We only review if the money was RELEASED (trip happened)
        if book.get('paymentStatus') != "RELEASED":
            continue
            
        ride = rides.get(book.get('ride_id'))
        if not ride: 
            continue

        driver_id = ride.get('driver', {}).get('id')
        passenger_id = book.get('passengerId')

        if not driver_id or not passenger_id:
            continue

        # Review A: Passenger -> Driver
        new_reviews.append({
            "_id": f"rev_{str(len(new_reviews) + 1).zfill(4)}",
            "booking_id": book['_id'],
            "author_id": passenger_id,
            "target_id": driver_id,
            "role": "PASSENGER_TO_DRIVER",
            "rating": random.randint(3, 5),
            "comment": "Very reliable service in Pisa.",
            "created_at": book.get('bookingDate')
        })

        # Review B: Driver -> Passenger
        new_reviews.append({
            "_id": f"rev_{str(len(new_reviews) + 1).zfill(4)}",
            "booking_id": book['_id'],
            "author_id": driver_id,
            "target_id": passenger_id,
            "role": "DRIVER_TO_PASSENGER",
            "rating": random.randint(4, 5),
            "comment": "Polite and followed the pickup instructions.",
            "created_at": book.get('bookingDate')
        })

    # 3. Save the clean, connected reviews
    with open('reviews_collection.json', 'w') as f:
        json.dump(new_reviews, f, indent=2)
    
    print(f"✅ Success! Generated {len(new_reviews)} connected reviews.")

if __name__ == "__main__":
    refit_reviews()