# payments.py
import pandas as pd
import uuid
import random
from datetime import datetime

# -------------------------
# 1️⃣ Load bookings
# -------------------------
bookings = pd.read_csv("bookings.csv", sep=",")
bookings.columns = bookings.columns.str.strip()

# -------------------------
# 2️⃣ Generate payments
# -------------------------
payments = []

for _, booking in bookings.iterrows():
    booking_status = booking["booking_status"]
    
    # Determina lo stato del pagamento
    if booking_status == "accepted":
        status = random.choice(["blocked", "released"])  # soldi bloccati o rilasciati
    elif booking_status == "pending":
        status = "pending"
    else:
        status = "refunded"

    # Tip casuale (30% dei casi per prenotazioni accettate)
    tip = round(random.uniform(0, 5), 2) if (booking_status == "accepted" and random.random() < 0.3) else 0

    payments.append({
        "payment_id": str(uuid.uuid4()),
        "booking_id": booking["booking_id"],
        "amount": booking["price_paid"],
        "tip": tip,
        "status": status,
        "payment_date": datetime.now().isoformat()
    })

# -------------------------
# 3️⃣ Save to CSV
# -------------------------
payments_df = pd.DataFrame(payments)
payments_df.to_csv("payments.csv", index=False)

print(f"Generated {len(payments_df)} payments")
