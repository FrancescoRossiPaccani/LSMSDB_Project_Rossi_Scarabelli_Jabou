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

echo ""
echo "-----------------------------------"
echo "Adding analytics to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/analytics_collection.json ]; then
    sudo docker cp DataSeederJson/analytics_collection.json mongo_primary:/tmp/analytics_collection.json
fi
sudo docker exec mongo_primary mongoimport --db lsmsdb --collection analytics --file /tmp/analytics_collection.json --jsonArray --mode upsert --quiet

echo "Analytics processed!"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding bookings to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/bookings_collection.json ]; then
    sudo docker cp DataSeederJson/bookings_collection.json mongo_primary:/tmp/bookings_collection.json
fi
sudo docker exec mongo_primary mongoimport --db lsmsdb --collection bookings --file /tmp/bookings_collection.json --jsonArray --mode upsert --quiet

echo "Bookings processed!"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding reviews to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/reviews_collection.json ]; then
    sudo docker cp DataSeederJson/reviews_collection.json mongo_primary:/tmp/reviews_collection.json
fi
sudo docker exec mongo_primary mongoimport --db lsmsdb --collection reviews --file /tmp/reviews_collection.json --jsonArray --mode upsert --quiet

echo "reviews processed!"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding rides to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/rides_collection.json ]; then
    sudo docker cp DataSeederJson/rides_collection.json mongo_primary:/tmp/rides_collection.json
fi
sudo docker exec mongo_primary mongoimport --db lsmsdb --collection rides --file /tmp/rides_collection.json --jsonArray --mode upsert --quiet

echo "Rides processed!"
echo "-----------------------------------"

echo ""
echo "-----------------------------------"
echo "Adding routes to mongodb"
if sudo docker exec mongo_primary [ ! -f /tmp/routes_collection.json ]; then
    sudo docker cp DataSeederJson/routes_collection.json mongo_primary:/tmp/routes_collection.json
fi
sudo docker exec mongo_primary mongoimport --db lsmsdb --collection routes --file /tmp/routes_collection.json --jsonArray --mode upsert --quiet

echo "Routes processed!"
echo "-----------------------------------"




