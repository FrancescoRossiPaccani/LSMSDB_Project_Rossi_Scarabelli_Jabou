# analytics.py
import pandas as pd

# -------------------------
# 1️⃣ Load datasets
# -------------------------
rides = pd.read_csv("rides.csv", sep=",")
rides.columns = rides.columns.str.strip()

bookings = pd.read_csv("bookings.csv", sep=",")
bookings.columns = bookings.columns.str.strip()

payments = pd.read_csv("payments.csv", sep=",")
payments.columns = payments.columns.str.strip()

# -------------------------
# 2️⃣ Analytics per driver
# -------------------------
rides_per_driver = rides.groupby("driver_id").agg(
    total_rides=pd.NamedAgg(column="ride_id", aggfunc="count"),
    avg_price=pd.NamedAgg(column="price_total", aggfunc="mean")
).reset_index()

# -------------------------
# 3️⃣ Analytics per ride
# -------------------------
# Passeggeri per ride
passengers_per_ride = bookings.groupby("ride_id").agg(
    total_passengers=pd.NamedAgg(column="passenger_id", aggfunc="count"),
    accepted_passengers=pd.NamedAgg(column="booking_status", aggfunc=lambda x: (x=="accepted").sum())
).reset_index()

# Merge con rides
rides_with_passengers = rides.merge(passengers_per_ride, on="ride_id", how="left")
rides_with_passengers["accepted_passengers"] = rides_with_passengers["accepted_passengers"].fillna(0)
rides_with_passengers["occupancy_rate"] = rides_with_passengers["accepted_passengers"] / rides_with_passengers["available_seats"]

# -------------------------
# 4️⃣ Earnings e tips
# -------------------------
payments["total_payment"] = payments["amount"] + payments["tip"]
payments_merged = payments.merge(bookings[["booking_id", "ride_id"]], on="booking_id")
payments_merged = payments_merged.merge(rides[["ride_id", "driver_id"]], on="ride_id")

earnings_per_driver = payments_merged.groupby("driver_id").agg(
    total_earnings=pd.NamedAgg(column="total_payment", aggfunc="sum"),
    avg_tip=pd.NamedAgg(column="tip", aggfunc="mean")
).reset_index()

# -------------------------
# 5️⃣ Percorso più frequentato
# -------------------------
most_traveled_route = rides.groupby("route_id").agg(
    total_rides=pd.NamedAgg(column="ride_id", aggfunc="count"),
    avg_price=pd.NamedAgg(column="price_total", aggfunc="mean"),
    avg_occupancy=pd.NamedAgg(column="available_seats", aggfunc="mean")
).reset_index().sort_values(by="total_rides", ascending=False)

# -------------------------
# 6️⃣ Merge driver analytics con guadagni
# -------------------------
analytics_df = rides_per_driver.merge(earnings_per_driver, on="driver_id", how="left")
analytics_df = analytics_df.fillna(0)

# -------------------------
# 7️⃣ Save CSV
# -------------------------
analytics_df.to_csv("analytics_drivers.csv", index=False)
most_traveled_route.to_csv("analytics_routes.csv", index=False)
rides_with_passengers.to_csv("analytics_rides.csv", index=False)

print("Driver analytics:", len(analytics_df))
print("Top routes analytics:", len(most_traveled_route))
print("Rides analytics:", len(rides_with_passengers))
