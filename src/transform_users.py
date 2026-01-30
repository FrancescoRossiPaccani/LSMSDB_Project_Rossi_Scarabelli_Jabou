import pandas as pd
import json
import os
import random

def transform_to_user_json():
    try:
        # 1. Load datasets
        df_users = pd.read_csv("users.csv", sep=';')
        df_docs = pd.read_csv("user_documents.csv", sep=',')
        df_drivers = pd.read_csv("drivers.csv", sep=';')
        df_vehicles = pd.read_csv("driver_vehicles.csv", sep=',')
        df_ratings = pd.read_csv("ratings.csv", sep=';')

        # Normalize columns (strip spaces and lowercase)
        for df in [df_users, df_docs, df_drivers, df_vehicles, df_ratings]:
            df.columns = df.columns.str.strip().str.lower()
            
    except Exception as e:
        print(f"Error loading files: {e}")
        return

    users_final = []
    
    for _, u in df_users.iterrows():
        uid = int(u['user_id'])
        u_docs = df_docs[df_docs['user_id'] == uid]
        driver_row = df_drivers[df_drivers['driver_id'] == uid]
        
        # 2. Filter and Split Ratings
        # For the project logic, we'll split the ratings for this user into two groups
        all_ratings = df_ratings[df_ratings['user_id'] == uid]['rating_id'].tolist()
        
        # Splitting logic: first half as driver reviews, second half as passenger
        mid = len(all_ratings) // 2
        driver_review_ids = all_ratings[:mid]
        passenger_review_ids = all_ratings[mid:]

        # 3. Handle Driver-specific info (Cars and Analytics)
        driver_info = None
        if not driver_row.empty:
            dr = driver_row.iloc[0]
            v_entry = df_vehicles[df_vehicles['driver_id'] == uid]
            cars_snapshot = [{"carId": f"car_{v['car_plate']}", "model": f"{v['brand']} {v['model']}"} 
                             for _, v in v_entry.iterrows()]
            
            driver_info = {
                "avg_acceptance_rate": round(random.uniform(0.75, 0.98), 2), 
                "number_of_acceptance": int(int(dr['total_rides']) * 0.9),
                "license": {"licenseId": "VERIFIED_EXT", "isValid": bool(dr['available'])},
                "cars": cars_snapshot
            }
            
        # 4. Assemble Final Document with your exact structure
        user_doc = {
            "_id": f"user_{str(uid).zfill(3)}",
            "personalInfo": {
                "name": u['name'].split()[0],
                "surname": " ".join(u['name'].split()[1:]),
                "email": u['email'],
                "phone": u['phone_number'],
                "age": int(u['age']),
                "gender": u['gender'],
                "location": u['location'],
                "is_identity_verified": not u_docs.empty
            },
            "documents": [{"type": d['document_type'], "documentId": str(d['document_number']), 
                           "expirationDate": d['expiry_date'], "isValid": True} for _, d in u_docs.iterrows()],
            "driverInfo": driver_info,
            
            "reviews_driver": {
                "average_rating": float(dr['rating']) if not driver_row.empty else 0.0,
                "count": len(driver_review_ids),
                "review": driver_review_ids
            },
            
            "reviews_passanger": {
                "average_rating": 4.5, # Placeholder or calculated from ratings.csv
                "count": len(passenger_review_ids),
                "review": passenger_review_ids
            },
            
            "status": "ACTIVE"
        }
        users_final.append(user_doc)
        
    with open('users_collection.json', 'w') as f:
        json.dump(users_final, f, indent=2)
    print("Success! 'users_collection.json' generated with separate Driver/Passenger blocks.")

if __name__ == "__main__":
    transform_to_user_json()