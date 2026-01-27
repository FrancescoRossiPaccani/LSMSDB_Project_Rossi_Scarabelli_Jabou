# reviews.py
import pandas as pd
import uuid
import random
from datetime import datetime, timedelta

# -------------------------
# 1️⃣ Load bookings
# -------------------------
bookings = pd.read_csv("bookings.csv", sep=",")
bookings.columns = bookings.columns.str.strip()

# -------------------------
# 2️⃣ Generate reviews
# -------------------------

# Lista di commenti più lunga e varia
comments_list = [
    "Great ride!", "Very smooth experience", "Driver was friendly",
    "Comfortable and clean", "On time", "Excellent service",
    "Would ride again", "Nice car", "Pleasant journey", "Good communication",
    "Amazing experience", "Fast and safe", "Highly recommended",
    "Superb driving", "Car was spotless", "Friendly conversation",
    "Quick and easy", "Perfect route", "Enjoyed the trip", "Reliable driver",
    ""  # vuoto per simulare chi non lascia commento
]

reviews = []

# Genera recensioni solo per prenotazioni accettate
for _, booking in bookings.iterrows():
    if booking["booking_status"] == "accepted":
        # Probabilità bassa che venga scritto un commento (30% dei casi)
        leave_comment = random.random() < 0.3
        comment = random.choice(comments_list) if leave_comment else ""

        reviews.append({
            "review_id": str(uuid.uuid4()),
            "ride_id": booking["ride_id"],
            "user_id": booking["passenger_id"],
            "rating_value": random.randint(3, 5),
            "comments": comment,
            "rating_date": (datetime.now() - timedelta(days=random.randint(0, 90))).isoformat()
        })

# -------------------------
# 3️⃣ Save to CSV
# -------------------------
reviews_df = pd.DataFrame(reviews)
reviews_df.to_csv("reviews.csv", index=False)

print(f"Generated {len(reviews_df)} reviews")
