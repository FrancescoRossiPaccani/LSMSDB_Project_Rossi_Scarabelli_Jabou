#!/bin/bash
echo "-----------------------------------"
echo "Adding users to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/users_collection.json ]; then
    sudo docker cp DataSeederJson/users_collection.json mongo_primary:/tmp/users_collection.json
fi
sudo docker exec mongo_primary mongoimport --db lsmsdb --collection users --file /tmp/users_collection.json --jsonArray --mode upsert --quiet
echo "Users processed"
echo "-----------------------------------"
echo ""
echo "-----------------------------------"
echo "Adding cars to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/cars_collection.json ]; then
    sudo docker cp DataSeederJson/cars_collection.json mongo_primary:/tmp/cars_collection.json
fi
sudo docker exec mongo_primary mongoimport --db lsmsdb --collection cars --file /tmp/cars_collection.json --jsonArray --mode upsert --quiet

echo "Cars processed!"
echo "-----------------------------------"
