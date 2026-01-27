import pandas as pd
import random
import uuid
from datetime import datetime

# -------------------------
# 1️⃣ Load datasets
# -------------------------
rides = pd.read_csv("rides.csv", sep=",")
rides.columns = rides.columns.str.strip()

users = pd.read_csv("users.csv", sep=";")  # <-- FORZATO IL SEPARATORE
users.columns = users.columns.str.strip()

# -------------------------
# 2️⃣ Generate bookings
# -------------------------
bookings = []

for _, ride in rides.iterrows():
    max_passengers = int(ride["available_seats"])
    num_passengers = random.randint(1, max_passengers)

    # Seleziona casualmente i passeggeri
    passengers = users.sample(num_passengers)

    for _, user in passengers.iterrows():
        # Stato della prenotazione
        status = random.choices(
            ["accepted", "pending", "rejected"],
            weights=[0.6, 0.3, 0.1]
        )[0]

        bookings.append({
            "booking_id": str(uuid.uuid4()),
            "ride_id": ride["ride_id"],
            "passenger_id": user["user_id"],  # ora funziona
            "pickup_point": ride["start_point"],
            "dropoff_point": ride["end_point"],
            "price_paid": round(ride["price_total"] * random.uniform(0.6, 1.0), 2),
            "booking_status": status,
            "created_at": datetime.now().isoformat()
        })

# -------------------------
# 3️⃣ Save to CSV
# -------------------------
bookings_df = pd.DataFrame(bookings)
bookings_df.to_csv("bookings.csv", index=False)

print(f"Generated {len(bookings_df)} bookings")
