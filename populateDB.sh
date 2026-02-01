#!/bin/bash
TARGET_CONTAINER="mongo0"
MONGO_URI="mongodb://mongo0:27017,mongo1:27017,mongo2:27017/lsmsdb?replicaSet=rs0"

echo "-----------------------------------"
echo "Waiting 10 seconds for Replica Set election to stabilize..."
sleep 10

import_collection() {
    local COLLECTION=$1
    local FILE_PATH="DataSeederJson/${COLLECTION}_collection.json"
    local TARGET_PATH="/tmp/${COLLECTION}_collection.json"

    echo ""
    echo "-----------------------------------"
    echo "Processing collection: $COLLECTION"

    if sudo docker exec $TARGET_CONTAINER [ ! -f $TARGET_PATH ]; then
        sudo docker cp "$FILE_PATH" $TARGET_CONTAINER:$TARGET_PATH
    fi

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

if [ -f "DataSeederJson/newRoutes/import_graph.csv" ]; then
    sudo docker cp DataSeederJson/newRoutes/import_graph.csv neo4j_db:/var/lib/neo4j/import/
    
    cat graph_db_import_data_query.cypher | sudo docker exec -i neo4j_db cypher-shell -u neo4j -p passwordProgetto2026
    echo "Graph db succesfully populated"
else
    echo "ERROR: File DataSeederJson/newRoutes/import_graph.csv not found!"
fi
