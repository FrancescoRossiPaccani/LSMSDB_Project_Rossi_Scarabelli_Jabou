$TARGET_CONTAINER = "mongo0"
$MONGO_URI = "mongodb://mongo0:27017,mongo1:27017,mongo2:27017/lsmsdb?replicaSet=rs0"

Write-Host "-----------------------------------"
Write-Host "Waiting 10 seconds for Replica Set election to stabilize..."
Start-Sleep -Seconds 10

function Import-Collection($COLLECTION) {
    $FILE_PATH = "DataSeederJson/$($COLLECTION)_collection.json"
    $TARGET_PATH = "/tmp/$($COLLECTION)_collection.json"

    Write-Host ""
    Write-Host "-----------------------------------"
    Write-Host "Processing collection: $COLLECTION"

    # check if file exists inside container
    docker exec $TARGET_CONTAINER sh -c "test -f $TARGET_PATH"
    if ($LASTEXITCODE -ne 0) {
        docker cp $FILE_PATH "$TARGET_CONTAINER`:$TARGET_PATH"
    }

    docker exec $TARGET_CONTAINER mongoimport `
        --uri "$MONGO_URI" `
        --collection "$COLLECTION" `
        --file $TARGET_PATH `
        --jsonArray `
        --mode upsert

    Write-Host "$COLLECTION processed!"
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

$csvPath = "DataSeederJson/newRoutes/import_graph.csv"

if (Test-Path $csvPath) {
    docker cp $csvPath neo4j_db:/var/lib/neo4j/import/
    Get-Content graph_db_import_data_query.cypher | docker exec -i neo4j_db cypher-shell -u neo4j -p passwordProgetto2026
    Write-Host "Graph db successfully populated"
}
else {
    Write-Host "ERROR: File DataSeederJson/newRoutes/import_graph.csv not found!"
}

