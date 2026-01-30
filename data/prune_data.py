import json
import os

def minify_existing_data():
    data_folder = 'data'
    for filename in os.listdir(data_folder):
        if filename.endswith('.json'):
            path = os.path.join(data_folder, filename)
            with open(path, 'r') as f:
                data = json.load(f)
            
            # Save without ANY spaces or indents
            with open(path, 'w') as f:
                json.dump(data, f, separators=(',', ':'))
            print(f"✅ Minified {filename}")

if __name__ == "__main__":
    minify_existing_data()