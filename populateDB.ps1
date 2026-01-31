# --- CONFIGURAZIONE ---
# Definiamo la stringa di connessione che include tutti i membri del Replica Set
$MONGO_URI = "mongodb://mongo_primary:27017,mongo_secondary1:27018,mongo_secondary2:27019/lsmsdb?replicaSet=rs0"

Write-Host "-----------------------------------"
Write-Host "Waiting a few seconds for Replica Set election to stabilize..."
Start-Sleep -Seconds 5

# Funzione per importare una collection
function Import-Collection($collectionName, $jsonFile) {
    Write-Host "-----------------------------------"
    Write-Host "Adding $collectionName to MongoDB"

    $containerFile = "/tmp/$jsonFile"
    $localFile = Join-Path $PSScriptRoot "DataSeederJson\$jsonFile"

    # Copiamo il file dentro il container se non esiste già
    $fileExists = docker exec mongo_primary powershell -Command "Test-Path $containerFile"
    if (-not $fileExists) {
        docker cp $localFile mongo_primary:$containerFile
    }

    # Eseguiamo l'import
    docker exec mongo_primary mongoimport --uri "$MONGO_URI" --collection $collectionName --file $containerFile --jsonArray --mode upsert
    Write-Host "$collectionName processed!"
    Write-Host "-----------------------------------"
}

# Lista delle collection da importare
$collections = @{
    "users" = "users_collection.json"
    "cars" = "cars_collection.json"
    "analytics" = "analytics_collection.json"
    "bookings" = "bookings_collection.json"
    "rides" = "rides_collection.json"
    "routes" = "routes_collection.json"
}

foreach ($name in $collections.Keys) {
    Import-Collection $name $collections[$name]
}

# Mostriamo l'IP del container (Primary Node)
Write-Host "Mongo container IP (Primary Node could be different):"
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mongo_primary
