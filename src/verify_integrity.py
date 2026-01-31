import json
import os

def verify_system_integrity():
    files = {
        'users': 'users_collection.json',
        'cars': 'cars_collection.json',
        'routes': 'routes_collection.json',
        'rides': 'rides_collection.json',
        'bookings': 'bookings_collection.json',
        'reviews': 'reviews_collection.json'
    }

    # 1. Load all data into memory
    data = {}
    for key, filename in files.items():
        if not os.path.exists(filename):
            print(f"❌ Critical Error: {filename} is missing!")
            return
        with open(filename, 'r') as f:
            data[key] = json.load(f)

    # 2. Create ID Maps for O(1) lightning-fast lookups
    user_ids = {u['_id'] for u in data['users']}
    car_ids = {c['_id'] for c in data['cars']}
    route_ids = {r['_id'] for r in data['routes']}
    ride_ids = {r['_id'] for r in data['rides']}
    booking_ids = {b['_id'] for b in data['bookings']}

    errors = []
    print(f"📊 Starting Integrity Check on {len(data['bookings'])} Bookings and {len(data['rides'])} Rides...")

    # --- CHECK RIDES ---
    for ride in data['rides']:
        # Check Driver
        if ride['driver']['id'] not in user_ids:
            errors.append(f"Ride {ride['_id']}: Driver {ride['driver']['id']} not found in users.")
        # Check Car
        car_key = f"car_{ride['car']['plate']}"
        if car_key not in car_ids:
            errors.append(f"Ride {ride['_id']}: Car {car_key} not found in cars.")
        # Check Route
        if ride['route']['route_id'] not in route_ids:
            errors.append(f"Ride {ride['_id']}: Route {ride['route']['route_id']} not found in routes.")

    # --- CHECK BOOKINGS ---
    for book in data['bookings']:
        # Check Ride
        if book['ride_id'] not in ride_ids:
            errors.append(f"Booking {book['_id']}: Points to non-existent Ride {book['ride_id']}.")
        # Check Passenger
        if book['passengerId'] not in user_ids:
            errors.append(f"Booking {book['_id']}: Passenger {book['passengerId']} not found in users.")

    # --- CHECK REVIEWS ---
    for rev in data['reviews']:
        # Check Booking
        if rev['booking_id'] not in booking_ids:
            errors.append(f"Review {rev['_id']}: Points to non-existent Booking {rev['booking_id']}.")
        # Check Author & Target
        if rev['author_id'] not in user_ids:
            errors.append(f"Review {rev['_id']}: Author {rev['author_id']} not found.")
        if rev['target_id'] not in user_ids:
            errors.append(f"Review {rev['_id']}: Target {rev['target_id']} not found.")

    # --- FINAL REPORT ---
    print("\n" + "="*30)
    print("📈 INTEGRITY REPORT")
    print("="*30)
    print(f"✅ Users: {len(user_ids)}")
    print(f"✅ Cars: {len(car_ids)}")
    print(f"✅ Rides: {len(ride_ids)}")
    print(f"✅ Bookings: {len(booking_ids)}")
    print(f"✅ Reviews: {len(data['reviews'])}")
    
    if not errors:
        print("\n✨ Perfect Integrity! All references are valid across the 'Big Data' set.")
    else:
        print(f"\n⚠️ Found {len(errors)} errors:")
        for err in errors[:10]: # Show first 10 errors
            print(f"  - {err}")
        if len(errors) > 10:
            print(f"  ... and {len(errors)-10} more.")

if __name__ == "__main__":
    verify_system_integrity()