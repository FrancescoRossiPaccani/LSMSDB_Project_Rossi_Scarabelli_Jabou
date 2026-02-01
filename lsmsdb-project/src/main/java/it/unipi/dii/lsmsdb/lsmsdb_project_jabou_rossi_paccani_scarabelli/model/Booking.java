package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String bookingDate; // Keeping as String for simplicity

    @Field("ride_id")
    private String rideId;

    private PassengerSummary passenger;
    private DriverSummary driver;

    @Field("car_plate")
    private String carPlate;

    private double finalPrice;
    private String paymentStatus;
    private Feedback feedback;
    private Locations locations;

    // --- GETTERS AND SETTERS ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public PassengerSummary getPassenger() { return passenger; }
    public void setPassenger(PassengerSummary passenger) { this.passenger = passenger; }
    public DriverSummary getDriver() { return driver; }
    public void setDriver(DriverSummary driver) { this.driver = driver; }
    public String getCarPlate() { return carPlate; }
    public void setCarPlate(String carPlate) { this.carPlate = carPlate; }
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }
    public Locations getLocations() { return locations; }
    public void setLocations(Locations locations) { this.locations = locations; }

    // --- INNER CLASSES ---

    public static class PassengerSummary {
        @org.springframework.data.mongodb.core.mapping.Field("id") // THIS MATCHES COMPASS
        private String id;

        private String name;

        // Getters/Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class DriverSummary {
        @Field("id") // Maps literally to "id" in Compass
        private String id;

        private String name;

        @Field("car_plate") // Maps to "car_plate" in Compass
        private String carPlate;

        // --- MANUALLY ADD ALL GETTERS AND SETTERS ---
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCarPlate() { return carPlate; }
        public void setCarPlate(String carPlate) { this.carPlate = carPlate; }
    }

    public static class Feedback {
        @Field("to_driver")
        private FeedbackDetail toDriver;

        @Field("to_passenger")
        private FeedbackDetail toPassenger;

        // Getters/Setters
        public FeedbackDetail getToDriver() { return toDriver; }
        public void setToDriver(FeedbackDetail toDriver) { this.toDriver = toDriver; }
        public FeedbackDetail getToPassenger() { return toPassenger; }
        public void setToPassenger(FeedbackDetail toPassenger) { this.toPassenger = toPassenger; }
    }

    public static class FeedbackDetail {
        private int rating;
        private String comment;
        private String date;

        // Getters/Setters
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }

    public static class Locations {
        private String pickup;
        private String dropoff;

        // Getters/Setters
        public String getPickup() { return pickup; }
        public void setPickup(String pickup) { this.pickup = pickup; }
        public String getDropoff() { return dropoff; }
        public void setDropoff(String dropoff) { this.dropoff = dropoff; }
    }
}