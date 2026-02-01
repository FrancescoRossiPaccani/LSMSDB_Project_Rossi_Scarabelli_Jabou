#!/bin/bash

# --- CONFIGURAZIONE ---
# Definiamo la stringa di connessione che include tutti i membri del Replica Set.
# Questo permette a mongoimport di trovare automaticamente il nodo PRIMARY attuale.
MONGO_URI="mongodb://mongo_primary:27017,mongo_secondary1:27018,mongo_secondary2:27019/lsmsdb?replicaSet=rs0"

echo "-----------------------------------"
echo "Waiting a few seconds for Replica Set election to stabilize..."
sleep 5

echo "-----------------------------------"
echo "Adding users to mongodb"
# Copiamo il file JSON dentro il container (necessario perché mongoimport gira lì dentro)
if sudo docker exec mongo_primary [ ! -f /tmp/users_collection.json ]; then
    sudo docker cp DataSeederJson/users_collection.json mongo_primary:/tmp/users_collection.json
fi
# Eseguiamo l'import usando la URI completa
sudo docker exec mongo_primary mongoimport --uri "$MONGO_URI" --collection users --file /tmp/users_collection.json --jsonArray --mode upsert 
echo "Users processed"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding cars to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/cars_collection.json ]; then
    sudo docker cp DataSeederJson/cars_collection.json mongo_primary:/tmp/cars_collection.json
fi
sudo docker exec mongo_primary mongoimport --uri "$MONGO_URI" --collection cars --file /tmp/cars_collection.json --jsonArray --mode upsert 
echo "Cars processed!"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding analytics to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/analytics_collection.json ]; then
    sudo docker cp DataSeederJson/analytics_collection.json mongo_primary:/tmp/analytics_collection.json
fi
sudo docker exec mongo_primary mongoimport --uri "$MONGO_URI" --collection analytics --file /tmp/analytics_collection.json --jsonArray --mode upsert 
echo "Analytics processed!"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding bookings to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/bookings_collection.json ]; then
    sudo docker cp DataSeederJson/bookings_collection.json mongo_primary:/tmp/bookings_collection.json
fi
sudo docker exec mongo_primary mongoimport --uri "$MONGO_URI" --collection bookings --file /tmp/bookings_collection.json --jsonArray --mode upsert 
echo "Bookings processed!"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding rides to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/rides_collection.json ]; then
    sudo docker cp DataSeederJson/rides_collection.json mongo_primary:/tmp/rides_collection.json
fi
sudo docker exec mongo_primary mongoimport --uri "$MONGO_URI" --collection rides --file /tmp/rides_collection.json --jsonArray --mode upsert 
echo "Rides processed!"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding routes to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/routes_collection.json ]; then
    sudo docker cp DataSeederJson/routes_collection.json mongo_primary:/tmp/routes_collection.json
fi
sudo docker exec mongo_primary mongoimport --uri "$MONGO_URI" --collection routes --file /tmp/routes_collection.json --jsonArray --mode upsert 
echo "Routes processed!"
echo "-----------------------------------"

echo "Mongo container IP (Primary Node could be different):"
sudo docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mongo_primary