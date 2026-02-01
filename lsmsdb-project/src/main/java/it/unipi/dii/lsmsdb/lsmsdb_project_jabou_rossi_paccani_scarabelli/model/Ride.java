package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;

@Document(collection = "rides")
public class Ride {
    @Id
    private String id; // es. "ride_79085914"
    private String status;

    private DriverSummary driver;
    private CarSummary car;
    private RouteSummary route;

    @Field("booking_state")
    private BookingState bookingState;

    @Field("base_price")
    private double basePrice;

    private Metadata metadata;

    public Ride() {
    }

    // --- GETTERS E SETTERS PRINCIPALI ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public DriverSummary getDriver() { return driver; }
    public void setDriver(DriverSummary driver) { this.driver = driver; }

    public CarSummary getCar() { return car; }
    public void setCar(CarSummary car) { this.car = car; }

    public RouteSummary getRoute() { return route; }
    public void setRoute(RouteSummary route) { this.route = route; }

    public BookingState getBookingState() { return bookingState; }
    public void setBookingState(BookingState bookingState) { this.bookingState = bookingState; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }


    // --- INNER CLASSES (Definizioni basate sul tuo JSON) ---

    // Mappa: "driver": { "id":..., "name":..., "phone":..., "avg_acceptance_rate":... }
    public static class DriverSummary {
        private String id;
        private String name;
        private String phone;
        @Field("avg_acceptance_rate")
        private double avgAcceptanceRate;

        public DriverSummary() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public double getAvgAcceptanceRate() { return avgAcceptanceRate; }
        public void setAvgAcceptanceRate(double avgAcceptanceRate) { this.avgAcceptanceRate = avgAcceptanceRate; }
    }

    // Mappa: "car": { "model": "...", "plate": "...", "comfort": "..." }
    public static class CarSummary {
        private String model;
        private String plate;
        private String comfort;

        public CarSummary() {}

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getPlate() { return plate; }
        public void setPlate(String plate) { this.plate = plate; }
        public String getComfort() { return comfort; }
        public void setComfort(String comfort) { this.comfort = comfort; }
    }

    // Mappa: "route": { "origin": "...", "destination": "...", "route_id": "..." }
    public static class RouteSummary {
        private String origin;
        private String destination;
        @Field("route_id")
        private String routeId;

        public RouteSummary() {}

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
    }

    // Mappa: "booking_state": { "total_seats":..., "available_seats":..., "has_waiting_list":... }
    public static class BookingState {
        @Field("total_seats")
        private int totalSeats;
        @Field("available_seats")
        private int availableSeats;
        @Field("has_waiting_list")
        private boolean hasWaitingList;

        public BookingState() {}

        public int getTotalSeats() { return totalSeats; }
        public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
        public boolean isHasWaitingList() { return hasWaitingList; }
        public void setHasWaitingList(boolean hasWaitingList) { this.hasWaitingList = hasWaitingList; }
    }

    // Mappa: "metadata": { "created_at": "..." }
    public static class Metadata {
        @Field("created_at")
        private LocalDateTime createdAt;
        // Nota: Se MongoDB salva come stringa pura, usa String createdAt.
        // Se Spring è configurato per convertire le date ISO, usa LocalDateTime.

        public Metadata() {}

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}