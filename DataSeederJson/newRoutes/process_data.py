import json
import math

# Configurazione
INPUT_FILE = 'routes.json'
OUTPUT_FILE = 'import_graph.csv'
DECIMALS = 3  # 3 cifre = ~110m di tolleranza per raggruppare i nodi

def haversine(lat1, lon1, lat2, lon2):
    """Calcola la distanza in metri tra due punti geografici"""
    R = 6371000  # Raggio terra in metri
    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlambda = math.radians(lon2 - lon1)
    a = math.sin(dphi/2)**2 + math.cos(phi1)*math.cos(phi2) * math.sin(dlambda/2)**2
    return 2 * R * math.atan2(math.sqrt(a), math.sqrt(1 - a))

def main():
    try:
        with open(INPUT_FILE, 'r', encoding='utf-8') as f:
            routes = json.load(f)
    except FileNotFoundError:
        print(f"ERRORE: Non trovo il file '{INPUT_FILE}'. Assicurati che sia nella stessa cartella.")
        return

    csv_rows = []
    # Intestazione del CSV: includiamo i dati del nodo corrente E del nodo precedente
    header = "route_id,sequence,type,name,lat,lon,prev_lat,prev_lon,dist_from_prev"
    csv_rows.append(header)

    print(f"Elaborazione di {len(routes)} rotte...")

    for route in routes:
        route_id = route['_id']
        coords = route['coordinates'] # Il tuo formato è [LON, LAT]
        
        # 1. Calcolo lunghezza totale reale del percorso ad alta definizione
        total_dist_real = 0
        distances_accumulated = [0]
        
        for i in range(1, len(coords)):
            # coords[i] è [lon, lat], quindi lat è indice 1, lon è indice 0
            d = haversine(coords[i-1][1], coords[i-1][0], coords[i][1], coords[i][0])
            total_dist_real += d
            distances_accumulated.append(total_dist_real)

        # 2. Campionamento: Vogliamo Start + 9 intermedi + End (11 punti totali)
        step_dist = total_dist_real / 10
        sampled_points = []
        
        # -- Punto INIZIALE --
        sampled_points.append({
            "lat": round(coords[0][1], DECIMALS),
            "lon": round(coords[0][0], DECIMALS),
            "type": "Location",
            "name": route['start_point']
        })

        # -- Punti INTERMEDI (10%, 20%... 90%) --
        for i in range(1, 10):
            target = i * step_dist
            
            # Trova l'indice nel path originale che è appena prima o dopo la distanza target
            idx = 0
            while idx < len(distances_accumulated) and distances_accumulated[idx] < target:
                idx += 1
            
            # Prendiamo il punto raw corrispondente
            # (Se idx va fuori range prendiamo l'ultimo, sicurezza)
            if idx >= len(coords): idx = len(coords) - 1
            
            p_curr = coords[idx]
            sampled_points.append({
                "lat": round(p_curr[1], DECIMALS),
                "lon": round(p_curr[0], DECIMALS),
                "type": "Stop", # È solo una tappa di passaggio
                "name": ""
            })

        # -- Punto FINALE --
        sampled_points.append({
            "lat": round(coords[-1][1], DECIMALS),
            "lon": round(coords[-1][0], DECIMALS),
            "type": "Location",
            "name": route['end_point']
        })

        # 3. Scrittura righe CSV (con logica linked-list)
        for seq, p in enumerate(sampled_points):
            # Pulizia nomi per evitare rotture del CSV
            safe_name = p['name'].replace(',', ' ').replace('"', '') if p['name'] else ""
            
            if seq == 0:
                # Il primo punto non ha distanze o precedenti
                row = f"{route_id},{seq},{p['type']},{safe_name},{p['lat']},{p['lon']},,,"
            else:
                prev = sampled_points[seq-1]
                # Calcoliamo la distanza tra i punti CAMPIONATI
                dist = haversine(prev['lat'], prev['lon'], p['lat'], p['lon'])
                
                row = (f"{route_id},{seq},{p['type']},{safe_name},{p['lat']},{p['lon']},"
                       f"{prev['lat']},{prev['lon']},{dist:.2f}")
            
            csv_rows.append(row)

    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write("\n".join(csv_rows))
    
    print(f"Fatto! File '{OUTPUT_FILE}' generato correttamente.")

if __name__ == "__main__":
    main()
