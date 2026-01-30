import pandas as pd
import json

def transform_rides():
    # 1. Load your Source of Truth collections
    try:
        with open('users_collection.json', 'r') as f:
            users_map = {u['_id']: u for u in json.load(f)}
            
        with open('cars_collection.json', 'r') as f:
            cars_map = {c['_id'].replace('car_', ''): c for c in json.load(f)}
            
        with open('routes_collection.json', 'r') as f:
            routes_map = {r['_id']: r for r in json.load(f)}
            
    except FileNotFoundError as e:
        print(f"Error: Required JSON files missing. {e}")
        return

    # 2. Load the raw rides.csv
    df_rides = pd.read_csv("rides.csv")
    rides_collection = []

    for _, row in df_rides.iterrows():
        user_id = f"user_{str(int(row['driver_id'])).zfill(3)}"
        plate = str(row['car_plate']).strip()
        route_id = str(row['route_id']).strip()

        # Retrieve snapshots
        user_data = users_map.get(user_id, {})
        car_data = cars_map.get(plate, {})
        
        route_exists = route_id in routes_map

        ride_doc = {
            "_id": f"ride_{str(row['ride_id'])[:8]}",
            "status": "OPEN",
            
            "driver": {
                "id": user_id,
                "name": f"{user_data.get('personalInfo', {}).get('name', 'N/A')} {user_data.get('personalInfo', {}).get('surname', '')}".strip(),
                "phone": user_data.get('personalInfo', {}).get('phone', 'N/A'),
                "avg_acceptance_rate": user_data.get('driverInfo', {}).get('avg_acceptance_rate', 0.82)
            },

            "car": {
                "model": f"{car_data.get('details', {}).get('brand', 'Unknown')} {car_data.get('details', {}).get('model', '')}".strip(),
                "plate": plate,
                "comfort": car_data.get('metadata', {}).get('comfort_level', 'BASIC')
            },

            "route": {
                "origin": row['start_point'], 
                "destination": row['end_point'],
                "route_id": route_id,
                "is_route_verified": route_exists 
            },

            "booking_state": {
                "total_seats": int(row['available_seats']),
                "available_seats": int(row['available_seats']),
                "has_waiting_list": False
            },

            "base_price": round(float(row['price_total']), 2),

            "metadata": {
                "created_at": row['departure_time']
                # route_point_count removed as requested
            }
        }
        rides_collection.append(ride_doc)

    # 3. Save the final professional rides_collection.json
    with open('rides_collection.json', 'w') as f:
        json.dump(rides_collection, f, indent=2)
    
    print(f"✅ Success! Generated {len(rides_collection)} rides without route point metadata.")

if __name__ == "__main__":
    transform_rides()