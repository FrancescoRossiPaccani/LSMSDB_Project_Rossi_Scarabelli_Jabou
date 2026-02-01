package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "bookings")
public class Booking {
    @Id
    private String id; // es. "book_492670bf"
    private String bookingDate;

    @Field("ride_id")
    private String rideId;

    private UserSummary passenger; // {id, name}
    private UserSummary driver;    // {id, name}

    @Field("car_plate")
    private String carPlate;
    private double finalPrice;
    private String paymentStatus;

    private FeedbackSummary feedback;
    private LocationSummary locations;

    public Booking() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public UserSummary getPassenger() {
        return passenger;
    }

    public void setPassenger(UserSummary passenger) {
        this.passenger = passenger;
    }

    public UserSummary getDriver() {
        return driver;
    }

    public void setDriver(UserSummary driver) {
        this.driver = driver;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public FeedbackSummary getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackSummary feedback) {
        this.feedback = feedback;
    }

    public LocationSummary getLocations() {
        return locations;
    }

    public void setLocations(LocationSummary locations) {
        this.locations = locations;
    }

    // --- Inner Classes ---
    public static class UserSummary {
        private String id;
        private String name;

        public UserSummary() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class LocationSummary {
        private String pickup;
        private String dropoff;

        public LocationSummary() {
        }

        public String getPickup() {
            return pickup;
        }

        public void setPickup(String pickup) {
            this.pickup = pickup;
        }

        public String getDropoff() {
            return dropoff;
        }

        public void setDropoff(String dropoff) {
            this.dropoff = dropoff;
        }
    }

    public static class FeedbackSummary {
        @Field("to_driver")
        private String toDriver; // ID recensione o null
        @Field("to_passenger")
        private String toPassenger;

        public FeedbackSummary() {
        }

        public String getToDriver() {
            return toDriver;
        }

        public void setToDriver(String toDriver) {
            this.toDriver = toDriver;
        }

        public String getToPassenger() {
            return toPassenger;
        }

        public void setToPassenger(String toPassenger) {
            this.toPassenger = toPassenger;
        }
    }
}