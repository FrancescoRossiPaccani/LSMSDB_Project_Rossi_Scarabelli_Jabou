import pandas as pd
import random
import string

# === CONFIGURAZIONE ===
input_file = "cars_dataset_with_colors.csv"
output_file = "cars_dataset_with_colors_with_plate.csv"
separator = ";"   # separatore presente nel csv originale


# === FUNZIONE PER GENERARE UNA TARGA ITALIANA ===
def genera_targa():
    letters = string.ascii_uppercase
    numbers = string.digits
    return (
        random.choice(letters) +
        random.choice(letters) +
        random.choice(numbers) +
        random.choice(numbers) +
        random.choice(numbers) +
        random.choice(letters) +
        random.choice(letters)
    )


# === CARICO CSV ===
df = pd.read_csv(input_file, sep=separator, header=None)

# Se vuoi aggiungere header manualmente:
df.columns = ["Brand", "Model", "Engine", "Power", "Fuel", "Seats", "Color"]

# === GENERO TARGHE UNICHE ===
plates_set = set()
plates = []

for _ in range(len(df)):
    plate = genera_targa()
    while plate in plates_set:        # evita duplicati
        plate = genera_targa()
    plates_set.add(plate)
    plates.append(plate)

df["Plate"] = plates

# === SALVO IL FILE ===
df.to_csv(output_file, sep=separator, index=False)

print("File creato:", output_file)
