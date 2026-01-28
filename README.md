# 📂 Large Scale & Multi-Structured Databases Project

This repository contains the implementation of a multi-model database architecture using Spring Boot, Docker, and three NoSQL technologies:
* 🍃 MongoDB (configured as a Replica Set)
* 🔴 Redis (Key-Value Store)
* 🕸️ Neo4j (Graph Database)

The goal is to demonstrate a distributed system where data is stored across different paradigms depending on the use case.

---

## 🏗️ Project Structure

* /lsmsdb-project: ☕ The Spring Boot application source code (Java).
* docker-compose.yml: 🐳 The infrastructure definition. Run this to start all databases.
* /old_files: 🗄️ Archive containing diagrams, datasets, and Python scripts for data generation.
* /data: 🚫 (Git Ignored) Local database storage created by Docker.

---

## ⚙️ Prerequisites

Before starting, ensure you have the following installed on your machine:
1. Docker Desktop (or Docker Engine + Compose plugin).
2. Java JDK 17 (or higher).
3. IntelliJ IDEA (Recommended IDE).
4. Git.

---

## 🚀 Setup Guide (Mandatory Steps)

To make the distributed architecture work on your local machine, you must follow these steps strictly.

### 1. Clone the Repository
git clone https://github.com/YOUR_USERNAME_HERE/LSMSDB_Project.git
cd LSMSDB_Project

### 2. ⚠️ CRITICAL: Update your Hosts File
Why? Our MongoDB Replica Set nodes are named mongo_primary, mongo_secondary1, etc. inside Docker. The Java driver receives these names and tries to connect to them. You must map these names to 127.0.0.1 so your computer knows where to find them.

Add this exact line to your hosts file:
127.0.0.1 mongo_primary mongo_secondary1 mongo_secondary2 redis_master redis_replica1 redis_replica2 neo4j_db

* 🪟 Windows: Open Notepad as Administrator -> Edit C:\Windows\System32\drivers\etc\hosts.
* 🐧 Linux / 🍏 Mac: Open terminal -> sudo nano /etc/hosts.

### 3. Start the Infrastructure
Open a terminal in the project root (where docker-compose.yml is located) and run:
docker compose up -d
(Wait ~30 seconds for all containers to initialize).

### 4. Initialize MongoDB Replica Set (First Time Only)
By default, the 3 Mongo nodes are independent. You must link them into a Replica Set (rs0) for the application to work. Run this command in your terminal:

docker exec -it mongo_primary mongosh --port 27017 --eval 'rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "mongo_primary:27017" },
    { _id: 1, host: "mongo_secondary1:27018" },
    { _id: 2, host: "mongo_secondary2:27019" }
  ]
})'

---

## ▶️ How to Run the Application

1. Open IntelliJ IDEA.
2. Select File > Open and choose the lsmsdb-project folder.
3. Let Maven download all dependencies.
4. If prompted "Project JDK is not defined", select JDK 17 (or higher).
5. Locate the main class:
   src/main/java/it/unipi/dii/lsmsdb/lsmsdb_project_jabou_rossi_paccani_scarabelli/LsmsdbProjectJabouRossiPaccaniScarabelliApplication.java
6. Click the Run (Green Play Button).

### ✅ Verification 
If the setup is correct, you will see green logs in the console:
🚀  STARTING MULTI-DATABASE TEST  🚀
🍃 Test MongoDB... ✅ Salvato su Mongo
🔴 Test Redis...   ✅ Salvato e Letto da Redis
🕸️ Test Neo4j...   ✅ Salvato Nodo Neo4j

---

## 🛠️ Troubleshooting

| Error | Cause | Solution |
| :--- | :--- | :--- |
| NotWritablePrimary or MongoTimeout | The Java driver cannot resolve container names. | Go back to Step 2. Edit your hosts file! |
| Connection Refused | Databases are offline. | Run docker compose up -d. |
| Project JDK not defined | IntelliJ doesn't know where Java is. | File -> Project Structure -> SDK -> Add JDK 17+. |
| Target folder / Permission denied | Old build files are locked. | Run mvn clean via the IntelliJ Maven panel. |
