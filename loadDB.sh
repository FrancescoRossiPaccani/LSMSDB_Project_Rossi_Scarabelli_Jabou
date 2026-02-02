#!/bin/bash
# 1. NOME CONTAINER CORRETTO (da mongo0 a mongo1)
TARGET_CONTAINER="mongo1"

# 2. URI CORRETTA
# Dato che sei in network_mode: host sulla VM1, puoi usare localhost o l'IP della VM1 (10.1.1.39).
# Non usare nomi host come 'mongo0' se non sono nel /etc/hosts.
MONGO_URI="mongodb://127.0.0.1:27017/lsmsdb?replicaSet=rs0"

echo "-----------------------------------"
echo "Waiting 10 seconds for Replica Set election to stabilize..."
sleep 10

import_collection() {
    local COLLECTION=$1
    local FILE_PATH="DataSet/${COLLECTION}_collection.json"
    local TARGET_PATH="/tmp/${COLLECTION}_collection.json"

    echo ""
    echo "-----------------------------------"
    echo "Processing collection: $COLLECTION"

    # Controllo esistenza file locale
    if [ ! -f "$FILE_PATH" ]; then
        echo "ERROR: File $FILE_PATH does not exist locally!"
        return
    fi

    # Copia nel container corretto
    sudo docker cp "$FILE_PATH" $TARGET_CONTAINER:$TARGET_PATH

    # Esecuzione mongoimport nel container corretto
    sudo docker exec $TARGET_CONTAINER mongoimport --uri "$MONGO_URI" --collection "$COLLECTION" --file $TARGET_PATH --jsonArray --mode upsert 
    
    echo "$COLLECTION processed!"
}

# --- EXECUTION IMPORT ---

import_collection "users"
import_collection "cars"
import_collection "analytics"
import_collection "bookings"
import_collection "rides"

echo "-----------------------------------"
echo "Mongo setup completed."

# --- NEO4J ---
echo ""
echo "-----------------------------------"
echo "Populating neo4j db"

# 3. NOME CONTAINER NEO4J CORRETTO (da neo4j_db a neo4j)
NEO4J_CONTAINER="neo4j"

if [ -f "DataSet/routes.csv" ]; then
    # Copia nel container corretto
    sudo docker cp DataSet/routes.csv $NEO4J_CONTAINER:/var/lib/neo4j/import/
    
    # Importazione Cypher
    # Nota: Rimosso user/pass perché nel docker-compose hai messo NEO4J_AUTH=none
    cat DataSet/graph_db_import_data_query.cypher | sudo docker exec -i $NEO4J_CONTAINER cypher-shell
    
    echo "Graph db succesfully populated"
else
    echo "ERROR: File DataSet/routes.csv not found!"
fi
