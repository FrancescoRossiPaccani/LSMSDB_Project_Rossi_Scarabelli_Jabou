$TARGET_CONTAINER = "mongo0"
$MONGO_URI = "mongodb://mongo0:27017,mongo1:27017,mongo2:27017/lsmsdb?replicaSet=rs0"

Write-Host "-----------------------------------"
Write-Host "Waiting 10 seconds for Replica Set election to stabilize..."
Start-Sleep -Seconds 10

function Import-Collection {
    param (
        [string]$Collection
    )

    $FILE_PATH = "DataSet\${Collection}_collection.json"
    $TARGET_PATH = "/tmp/${Collection}_collection.json"

    Write-Host ""
    Write-Host "-----------------------------------"
    Write-Host "Processing collection: $Collection"

    # Verifica se il file esiste nel container (assumendo container Linux)
    # test -f restituisce exit code 0 se esiste, 1 se non esiste.
    docker exec $TARGET_CONTAINER test -f $TARGET_PATH 2>$null

    # Se l'exit code non è 0 (file non trovato), copiamo il file
    if ($LASTEXITCODE -ne 0) {
        docker cp "$FILE_PATH" "${TARGET_CONTAINER}:${TARGET_PATH}"
    }

    docker exec $TARGET_CONTAINER mongoimport --uri "$MONGO_URI" --collection "$Collection" --file $TARGET_PATH --jsonArray --mode upsert 
    
    Write-Host "$Collection processed!"
}

# --- EXECUTION IMPORT ---

Import-Collection "users"
Import-Collection "cars"
Import-Collection "analytics"
Import-Collection "bookings"
Import-Collection "rides"

Write-Host "-----------------------------------"
Write-Host "Mongo setup completed."

# --- NEO4J ---
Write-Host ""
Write-Host "-----------------------------------"
Write-Host "Populating neo4j db"

if (Test-Path "DataSet\routes.csv") {
    docker cp "DataSet\routes.csv" "neo4j_db:/var/lib/neo4j/import/"
    
    # Legge il file cypher e lo passa in pipe a docker exec
    Get-Content "graph_db_import_data_query.cypher" -Raw | docker exec -i neo4j_db cypher-shell -u neo4j -p passwordProgetto2026
    
    Write-Host "Graph db succesfully populated"
} else {
    Write-Host "ERROR: File DataSet\routes.csv not found!"
}
