import pandas as pd
import json
import io

def transform_rides():
    # 1. Your raw CSV data (I'm using the snippet you provided)
    csv_data = """ride_id,driver_id,route_id,start_point,end_point,departure_time,price_total,available_seats,car_plate,status
79085914-58d6-4c47-858c-a9d9d2fa86a3,1,route_1049,Via Cisanello 170,MD Discount Riglione,2025-10-17T00:02:22.178986,7.16,4,IY368GR,published
50c711c1-9741-4bae-b22a-60460f0d1a06,1,route_426,Stazione Leopolda,Universita di Pisa,2026-01-15T08:02:22.180797,22.12,2,IY368GR,published
5c2b0650-d65e-4feb-8d84-6aae2e01e6d1,1,route_1205,Via del Borghetto 102,Eurospin Ospedaletto,2026-02-14T13:02:22.182542,16.19,4,KZ007EP,published
6bc486a9-359f-42b9-8444-cd2206b88a96,1,route_3,Torre,Via Angelo Battelli 5,2025-08-02T08:02:22.184248,19.3,2,DI910UE,published"""

    # Load the CSV
    df = pd.read_csv(io.StringIO(csv_data))
    
    rides_final = []
    
    for _, row in df.iterrows():
        # Standardizing IDs to match our other collections
        ride_doc = {
            "_id": f"ride_{str(row['ride_id'])[:8]}", # Using first part of UUID for brevity
            "driver_id": f"user_{str(int(row['driver_id'])).zfill(3)}",
            "car_id": f"car_{row['car_plate']}", # Links to our Cars entity
            "route_id": row['route_id'], # Links to our Route entity
            "details": {
                "start": row['start_point'],
                "end": row['end_point'],
                "departure": row['departure_time'],
                "status": row['status'].upper()
            },
            "pricing": {
                "total_price": float(row['price_total']),
                "currency": "EUR"
            },
            "capacity": {
                "available_seats": int(row['available_seats'])
            }
        }
        rides_final.append(ride_doc)

    # Save to JSON
    with open('rides_collection.json', 'w') as f:
        json.dump(rides_final, f, indent=2)

    print(f"Success! {len(rides_final)} rides transformed into linked JSON format.")

if __name__ == "__main__":
    transform_rides()