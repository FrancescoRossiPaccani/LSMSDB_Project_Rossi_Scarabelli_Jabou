# notifications.py
import pandas as pd
import uuid
import random
from datetime import datetime, timedelta

# -------------------------
# 1️⃣ Load datasets
# -------------------------
rides = pd.read_csv("rides.csv", sep=",")
rides.columns = rides.columns.str.strip()

bookings = pd.read_csv("bookings.csv", sep=",")
bookings.columns = bookings.columns.str.strip()

# -------------------------
# 2️⃣ Generate notifications
# -------------------------
notifications = []

for _, booking in bookings.iterrows():
    ride_id = booking["ride_id"]
    passenger_id = booking["passenger_id"]

    # Estrai driver_id in modo sicuro
    driver_id = int(rides.loc[rides["ride_id"] == ride_id, "driver_id"].iloc[0])

    # 1️⃣ Notification: passenger requests to join ride
    notifications.append({
        "notification_id": str(uuid.uuid4()),
        "user_id": driver_id,
        "type": "request_join",
        "related_id": booking["booking_id"],
        "created_at": (datetime.now() - timedelta(days=random.randint(0, 30))).isoformat(),
        "read": False
    })

    # 2️⃣ Notification: driver accepts or rejects
    if booking["booking_status"] == "accepted":
        # Passenger notified of acceptance
        notifications.append({
            "notification_id": str(uuid.uuid4()),
            "user_id": passenger_id,
            "type": "request_accepted",
            "related_id": booking["booking_id"],
            "created_at": (datetime.now() - timedelta(days=random.randint(0, 30))).isoformat(),
            "read": False
        })
        # Driver notified of payment received
        notifications.append({
            "notification_id": str(uuid.uuid4()),
            "user_id": driver_id,
            "type": "payment_received",
            "related_id": booking["booking_id"],
            "created_at": (datetime.now() - timedelta(days=random.randint(0, 30))).isoformat(),
            "read": False
        })
    elif booking["booking_status"] == "rejected":
        # Passenger notified of rejection
        notifications.append({
            "notification_id": str(uuid.uuid4()),
            "user_id": passenger_id,
            "type": "request_rejected",
            "related_id": booking["booking_id"],
            "created_at": (datetime.now() - timedelta(days=random.randint(0, 30))).isoformat(),
            "read": False
        })

    # 3️⃣ Notification: passenger changes ride details (10% chance)
    if random.random() < 0.1:
        notifications.append({
            "notification_id": str(uuid.uuid4()),
            "user_id": driver_id,
            "type": "request_change",
            "related_id": booking["booking_id"],
            "created_at": (datetime.now() - timedelta(days=random.randint(0, 30))).isoformat(),
            "read": False
        })

# -------------------------
# 3️⃣ Save to CSV
# -------------------------
notifications_df = pd.DataFrame(notifications)
notifications_df.to_csv("notifications.csv", index=False)

print(f"Generated {len(notifications_df)} notifications")
