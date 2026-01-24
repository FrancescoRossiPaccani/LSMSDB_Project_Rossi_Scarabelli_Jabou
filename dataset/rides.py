# rides.py
import pandas as pd
import random
import uuid
from datetime import datetime, timedelta

# -------------------------
# 1️⃣ Load & clean CSVs
# -------------------------
drivers = pd.read_csv("drivers.csv", sep=";")
drivers.columns = drivers.columns.str.strip()
drivers["available"] = drivers["available"].astype(str).str.lower() == "true"

cars = pd.read_csv("cars_dataset_with_colors_with_plate.csv", sep=";", skiprows=[1])
cars.columns = cars.columns.str.strip()

routes = pd.read_csv("percorsi_pisa_totale.csv", sep=",")
routes.columns = routes.columns.str.strip()

# -------------------------
# 2️⃣ Generate rides
# -------------------------
rides = []
available_drivers = drivers[drivers["available"]]

route_ids = routes["route_id"].unique()

target_rides = 75000  # almeno 10.000 rides
rides_per_driver = max(1, target_rides // len(available_drivers))

for _, driver in available_drivers.iterrows():
    for _ in range(rides_per_driver):
        route_id = random.choice(route_ids)
        route_points = routes[routes["route_id"] == route_id]

        start = route_points.iloc[0]["start"]
        end = route_points.iloc[0]["end"]

        # Departure time random in last 180 days up to next 30 days
        departure_time = (
            datetime.now() + timedelta(days=random.randint(-180, 30), hours=random.randint(0, 23))
        ).isoformat()

        seats = random.randint(1, 4)
        price = round(random.uniform(5, 25), 2)

        car = cars.sample(1).iloc[0]

        rides.append({
            "ride_id": str(uuid.uuid4()),
            "driver_id": driver["driver_id"],
            "route_id": route_id,
            "start_point": start,
            "end_point": end,
            "departure_time": departure_time,
            "price_total": price,
            "available_seats": seats,
            "car_plate": car["Plate"],
            "status": "published"
        })

# Seleziona esattamente 10.000 rides se siamo oltre
if len(rides) > target_rides:
    rides = random.sample(rides, target_rides)

rides_df = pd.DataFrame(rides)
rides_df.to_csv("rides.csv", index=False)

print(f"Generated {len(rides_df)} rides")
