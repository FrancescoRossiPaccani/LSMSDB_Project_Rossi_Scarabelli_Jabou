from routingpy import Graphhopper
import pandas as pd
import time
import os

# --- CONFIGURAZIONE API ---
API_KEY = "a693a304-0f82-4fb9-a9f8-790584039a26"
client = Graphhopper(api_key=API_KEY)

# --- POI di Pisa con coordinate [lon, lat] ---
pois = {
    "Torre": [10.3966, 43.7230],
    "Stazione Pisa Centrale": [10.3973, 43.7085],
    "Universita di Pisa": [10.4080, 43.7190],
    "Via Aretino Spinello 11": [10.4000, 43.7150],
    "Via Angelo Battelli 5": [10.4020, 43.7160],
    "Via Gabriele Vettori 18": [10.4040, 43.7170],
    "Facolta di Ingegneria Polo A": [10.4060, 43.7180],
    "Polo Fibonacci": [10.4070, 43.7190],
    "Stazione Leopolda": [10.3990, 43.7120],
    "Stazione San Rossore": [10.3900, 43.7110],
    "Parco San Rossore": [10.3880, 43.7100],
    "Lidl Pisa": [10.3920, 43.7130],
    "McDonald's Pisa Aurelia": [10.3895, 43.7025],
    "Parcheggio Via Paparelli": [10.4150, 43.7275],
    "Decathlon Pisa": [10.4112, 43.7068],
    "MediaWorld Pisa": [10.4120, 43.7075],
    "Centro Commerciale Pisanova": [10.4135, 43.7095],
    "Ristorante Galileo": [10.4160, 43.7108],
    "Ipercoop Pisa": [10.4142, 43.7045],
    "Via Cisanello 170": [10.4178, 43.7130],
    "Farmacia Cisanello": [10.4210, 43.7135],
    "Ristorante Il Mio Capriccio": [10.4082, 43.7059],
    "Via del Borghetto 102": [10.4058, 43.7202],
    "Parcheggio di Cisanello": [10.4188, 43.7142],
    "Carrefour Express Pisanova": [10.4108, 43.7105],
    "OBI Pisa": [10.3835, 43.7008],
    "Ristorante La Greppia": [10.3992, 43.7042],
    "Ristorante Pizzeria Il Pellicano": [10.4028, 43.7055],
    "Via San Ranieri 9": [10.4175, 43.7158],
    "Supermercato Coop San Giusto": [10.3922, 43.7058],
    "Via Montanelli 66": [10.4062, 43.7132],
    "Ristorante Tuscany 2": [10.4201, 43.7098],
    "Euronics Pisa Cisanello": [10.4149, 43.7070],
    "Hotel San Ranieri": [10.4208, 43.7145],
    "Esselunga Cisanello": [10.4202, 43.7108],
    "Piscina Comunale Camalich": [10.3845, 43.6862],
    "Conad Pian di Marco": [10.3878, 43.7028],
    "Hotel Galilei Pisa": [10.3898, 43.6925],
    "Pizzeria Pisanova": [10.4132, 43.7102],
    "Ristorante Il Rustichello - San Piero": [10.3618, 43.7002],
    "Eurospin Ospedaletto": [10.3735, 43.6850],
    "McDonald's Barbaricina": [10.3610, 43.7245],
    "Conad La Vettola": [10.3545, 43.7125],
    "MD Discount Riglione": [10.4280, 43.6902],
    "Parcheggio Porto di Marina": [10.2958, 43.6651],
    "Ristorante Da Stefano (Tirrenia)": [10.2941, 43.6433],
    "Centro Commerciale Darsena (Tirrenia)": [10.2840, 43.6358],
    "Parcheggio San Giuliano Terme": [10.4305, 43.7588],
    "Hotel Continental Tirrenia": [10.2928, 43.6502],
    "Parcheggio Migliarino": [10.3625, 43.7840],
    "Conad Metato": [10.4275, 43.7603],
    "Ristorante Il Lago (Nodica)": [10.4144, 43.7488],
    "Pizzeria Colignola": [10.4295, 43.7090],
    "Supermercato Coop Ghezzano": [10.4251, 43.7415]
}


# --- CONFIGURAZIONE BATCH ---
batch_size = 30   # quante coppie calcolare per batch
batch_counter = 1

poi_items = list(pois.items())
total_pois = len(poi_items)

# Lista globale per salvare i dati di tutti i batch se vuoi unirli alla fine
all_output_rows = []

global_id = 0
route_counter = 0

# --- Genera percorsi in batch ---
for i, (start_name, start_coord) in enumerate(poi_items):
    for j, (end_name, end_coord) in enumerate(poi_items):
        if start_name == end_name:
            continue

        # Salva solo batch_size percorsi alla volta
        if route_counter % batch_size == 0 and route_counter != 0:
            # Salva CSV del batch corrente
            df = pd.DataFrame(all_output_rows, columns=["id","start","end","route_id","lat","lon","idx"])
            filename = f"percorsi_pisa_batch_{batch_counter}.csv"
            df.to_csv(filename, index=False)
            print(f"Batch {batch_counter} salvato in {filename}")
            batch_counter += 1
            all_output_rows = []
            time.sleep(30)  # pausa lunga tra batch per evitare 429

        route_id = f"route_{route_counter}"
        route_counter += 1

        try:
            res = client.directions(
                locations=[start_coord, end_coord],
                profile="car"
            )
        except Exception as e:
            print("Errore per", start_name, "->", end_name, e)
            continue

        coords = res.geometry
        for idx, (lon, lat) in enumerate(coords):
            all_output_rows.append({
                "id": global_id,
                "start": start_name,
                "end": end_name,
                "route_id": route_id,
                "lat": lat,
                "lon": lon,
                "idx": idx
            })
            global_id += 1

        # pausa tra le singole richieste per non superare il limite API
        time.sleep(3)

# --- Salva l'ultimo batch rimasto ---
if all_output_rows:
    df = pd.DataFrame(all_output_rows, columns=["id","start","end","route_id","lat","lon","idx"])
    filename = f"percorsi_pisa_batch_{batch_counter}.csv"
    df.to_csv(filename, index=False)
    print(f"Ultimo batch salvato in {filename}")



# ------------------------------
# PARTE FINALE: MERGIARE I CSV
# ------------------------------

print("\n--- Unione di tutti i batch in un unico file...")

dfs = []
for batch_file in csv_files:
    dfs.append(pd.read_csv(batch_file))

df_finale = pd.concat(dfs, ignore_index=True)
df_finale.to_csv("percorsi_pisa_finale.csv", index=False)

print("File finale salvato in percorsi_pisa_finale.csv")


# ------------------------------
# CANCELLARE I CSV TEMPORANEI
# ------------------------------

print("\n--- Eliminazione file temporanei...")

for batch_file in csv_files:
    try:
        os.remove(batch_file)
        print(f"Eliminato: {batch_file}")
    except Exception as e:
        print(f"Errore eliminando {batch_file}: {e}")

print("\n Script terminato \n")