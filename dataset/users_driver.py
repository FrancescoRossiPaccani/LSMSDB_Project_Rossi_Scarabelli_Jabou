import pandas as pd
import random
from datetime import datetime, timedelta
import uuid

# Carica utenti, driver e auto
users = pd.read_csv("users.csv", sep=';')
drivers = pd.read_csv("drivers.csv", sep=';')
cars = pd.read_csv("cars_dataset_with_colors_with_plate.csv", sep=';')

documents = []
driver_vehicles = []

# --- Utenti normali ---
for _, user in users.iterrows():
    # Documento generico (non patente)
    doc_id = str(uuid.uuid4())
    doc_type = random.choice(["ID Card", "Passport", "Health Card"])
    expiry = datetime.now() + timedelta(days=random.randint(365, 365*10))
    
    documents.append({
        "document_id": doc_id,
        "user_id": user["user_id"],
        "document_type": doc_type,
        "document_number": str(random.randint(1000000, 9999999)),
        "expiry_date": expiry.strftime("%Y-%m-%d"),
        "mandatory": True
    })

# --- Driver ---
available_cars = cars.copy()
for _, driver in drivers.iterrows():
    user_id = driver["driver_id"]  # assumiamo driver_id == user_id
    # Patente obbligatoria
    doc_id = str(uuid.uuid4())
    expiry = datetime.now() + timedelta(days=random.randint(365, 365*10))
    documents.append({
        "document_id": doc_id,
        "user_id": user_id,
        "document_type": "Driver License",
        "document_number": str(random.randint(1000000, 9999999)),
        "expiry_date": expiry.strftime("%Y-%m-%d"),
        "mandatory": True
    })
    
    # Documento extra casuale
    doc_id = str(uuid.uuid4())
    doc_type = random.choice(["ID Card", "Passport", "Health Card"])
    expiry = datetime.now() + timedelta(days=random.randint(365, 365*10))
    documents.append({
        "document_id": doc_id,
        "user_id": user_id,
        "document_type": doc_type,
        "document_number": str(random.randint(1000000, 9999999)),
        "expiry_date": expiry.strftime("%Y-%m-%d"),
        "mandatory": False
    })
    
    # Assegna una macchina unica
    car = available_cars.sample(1)
    available_cars = available_cars.drop(car.index)
    driver_vehicles.append({
        "driver_id": user_id,
        "car_plate": car.iloc[0]["Plate"],
        "brand": car.iloc[0]["Brand"],
        "model": car.iloc[0]["Model"],
        "color": car.iloc[0]["Color"]
    })

# Salva CSV
docs_df = pd.DataFrame(documents)
docs_df.to_csv("user_documents.csv", index=False)

vehicles_df = pd.DataFrame(driver_vehicles)
vehicles_df.to_csv("driver_vehicles.csv", index=False)

print("Generati documenti e veicoli per i driver!")
