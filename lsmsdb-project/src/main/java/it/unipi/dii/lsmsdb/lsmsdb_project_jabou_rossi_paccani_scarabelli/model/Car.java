package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "cars")
public class Car {

    @Id
    private String id;

    @Field("owner_id")
    private String ownerId;

    private CarDetails details;
    private CarMetadata metadata;

    // --- GETTERS AND SETTERS ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public CarDetails getDetails() { return details; }
    public void setDetails(CarDetails details) { this.details = details; }
    public CarMetadata getMetadata() { return metadata; }
    public void setMetadata(CarMetadata metadata) { this.metadata = metadata; }

    // --- INNER CLASSES ---

    public static class CarDetails {
        private String brand;
        private String model;
        private String engine;
        private int seats;
        private String color;

        // Getters/Setters
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getEngine() { return engine; }
        public void setEngine(String engine) { this.engine = engine; }
        public int getSeats() { return seats; }
        public void setSeats(int seats) { this.seats = seats; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    public static class CarMetadata {
        @Field("comfort_level")
        private String comfortLevel;

        // Getters/Setters
        public String getComfortLevel() { return comfortLevel; }
        public void setComfortLevel(String comfortLevel) { this.comfortLevel = comfortLevel; }
    }
}