import pandas as pd
import json

def create_simple_routes():
    # 1. Load your actual CSV
    try:
        df = pd.read_csv("pisa_routes.csv")
        df.columns = df.columns.str.strip().str.lower()
    except Exception as e:
        print(f"Error loading CSV: {e}")
        return

    # 2. Group by route_id and sort by index
    routes_list = []
    grouped = df.sort_values(['route_id', 'idx']).groupby('route_id')

    for route_id, group in grouped:
        # We retrieve the points as a simple list of [lon, lat]
        points = group[['lon', 'lat']].values.tolist()
        
        # This is the simplified structure you asked for
        route_doc = {
            "_id": route_id,
            "start_point": group['start'].iloc[0],
            "end_point": group['end'].iloc[0],
            "point_count": len(group),
            "coordinates": points  # Just the list of points, no 'geometry' or 'Feature'
        }
        routes_list.append(route_doc)

    # 3. Save as a standard JSON list
    with open('routes_collection.json', 'w') as f:
        json.dump(routes_list, f, indent=2)
    
    print(f"Success! {len(routes_list)} routes created in a clean list format.")

if __name__ == "__main__":
    create_simple_routes()