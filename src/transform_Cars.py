import pandas as pd
import json
import os

def clean_seats(seat_value):
    s = str(seat_value).strip()
    if '+' in s:
        return sum(int(part) for part in s.split('+'))
    try:
        return int(float(s))
    except:
        return 0

def transform_to_cars_json():
    try:
        # 1. Load data
        df_detailed = pd.read_csv("cars_dataset_with_colors_with_plate.csv", sep=';', skiprows=[1])
        df_map = pd.read_csv("driver_vehicles.csv", sep=',')
        
        with open('users_collection.json', 'r') as f:
            users_data = json.load(f)

        # Normalize plate mapping
        df_map.columns = df_map.columns.str.strip()
        df_map['car_plate_clean'] = df_map['car_plate'].astype(str).str.strip().str.lower()
        
    except Exception as e:
        print(f"Error loading files: {e}")
        return

    # 2. Identify Users without cars to maintain the 1-to-1 assumption
    all_user_ids = [u['_id'] for u in users_data]
    
    # Map of plates already assigned in your CSV
    plate_to_owner = {}
    users_with_cars = set()
    for _, row in df_map.iterrows():
        plate = row['car_plate_clean']
        owner = f"user_{str(int(row['driver_id'])).zfill(3)}"
        plate_to_owner[plate] = owner
        users_with_cars.add(owner)

    # List of users available for new assignments
    available_users = [uid for uid in all_user_ids if uid not in users_with_cars]
    
    cars_final = []
    user_idx = 0

    for _, row in df_detailed.iterrows():
        raw_plate = str(row['Plate']).strip()
        clean_plate = raw_plate.lower()
        
        # 3. Logic: Match or Assign
        if clean_plate in plate_to_owner:
            owner_id = plate_to_owner[clean_plate]
        elif user_idx < len(available_users): # FIXED: Changed from available_user_ids
            owner_id = available_users[user_idx]
            user_idx += 1
        else:
            owner_id = "user_SYSTEM_INVENTORY"

        # Metadata for UML coherency
        brand = str(row['Brand']).upper()
        comfort = "LUXURY" if any(lb in brand for lb in ['FERRARI', 'ROLLS ROYCE', 'MERCEDES']) else "BASIC"
        
        car_doc = {
            "_id": f"car_{raw_plate}",
            "owner_id": owner_id,
            "details": {
                "brand": row['Brand'],
                "model": row['Model'],
                "engine": row['Engine'],
                "seats": clean_seats(row['Seats']),
                "color": row['Color']
            },
            "metadata": { "comfort_level": comfort }
        }
        cars_final.append(car_doc)

    with open('cars_collection.json', 'w') as f:
        json.dump(cars_final, f, indent=2)
    
    print(f"Success! {len(cars_final)} cars assigned. {user_idx} new users got a car.")

if __name__ == "__main__":
    transform_to_cars_json()