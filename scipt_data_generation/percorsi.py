from routingpy import Graphhopper
import pandas as pd
import time

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
    "Via Gabriele Vettori 25": [10.4040, 43.7170],
    "Facolta di Ingegneria Polo A": [10.4060, 43.7180],
    "Polo Fibonacci": [10.4070, 43.7190],
    "Stazione Leopolda": [10.3990, 43.7120],
    "Stazione San Rossore": [10.3900, 43.7110],
    "Parco San Rossore": [10.3880, 43.7100],
    "Lidl Pisa": [10.3920, 43.7130]
}

# --- CONFIGURAZIONE BATCH ---
batch_size = 3   # quante coppie calcolare per batch
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
            time.sleep(60)  # pausa lunga tra batch per evitare 429

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
        time.sleep(5)

# --- Salva l'ultimo batch rimasto ---
if all_output_rows:
    df = pd.DataFrame(all_output_rows, columns=["id","start","end","route_id","lat","lon","idx"])
    filename = f"percorsi_pisa_batch_{batch_counter}.csv"
    df.to_csv(filename, index=False)
    print(f"Ultimo batch salvato in {filename}")
