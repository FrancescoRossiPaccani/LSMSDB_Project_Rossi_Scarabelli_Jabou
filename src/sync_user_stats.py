import json

def sync_stats():
    # 1. Load the data
    with open('users_collection.json', 'r') as f:
        users = json.load(f)
    with open('reviews_collection.json', 'r') as f:
        reviews = json.load(f)

    # 2. Calculate new averages
    # We create a dictionary to store [total_rating, count] for each user
    stats = {}
    for rev in reviews:
        t_id = rev['target_id']
        if t_id not in stats:
            stats[t_id] = {'sum': 0, 'count': 0}
        stats[t_id]['sum'] += rev['rating']
        stats[t_id]['count'] += 1

    # 3. Update the users list
    for user in users:
        u_id = user['_id']
        if u_id in stats:
            new_avg = round(stats[u_id]['sum'] / stats[u_id]['count'], 2)
            # Update driver rating if they were reviewed as a driver
            if user.get('driverInfo'):
                user['reviews_driver']['average_rating'] = new_avg
                user['reviews_driver']['count'] = stats[u_id]['count']

    # 4. Save the updated users
    with open('users_collection.json', 'w') as f:
        json.dump(users, f, indent=2)
    
    print("✅ Users synchronized! All ratings now match the reviews.")

if __name__ == "__main__":
    sync_stats()