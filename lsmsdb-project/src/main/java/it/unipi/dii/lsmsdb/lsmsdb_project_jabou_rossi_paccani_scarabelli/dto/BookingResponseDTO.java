package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto;

import java.time.LocalDateTime;

public class BookingResponseDTO {
    private String id;
    private String rideId;
    private LocalDateTime bookingDate;
    private String paymentStatus;
    private double finalPrice;
    private String carPlate;

    // Oggetti annidati (come nel JSON del tuo amico)
    private UserSummaryDTO driver;
    private UserSummaryDTO passenger;
    private LocationDTO locations;
    private FeedbackDTO feedback; // Può essere null se non c'è ancora feedback

    public BookingResponseDTO() {}

    // Getter e Setter standard...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
    public String getCarPlate() { return carPlate; }
    public void setCarPlate(String carPlate) { this.carPlate = carPlate; }
    public UserSummaryDTO getDriver() { return driver; }
    public void setDriver(UserSummaryDTO driver) { this.driver = driver; }
    public UserSummaryDTO getPassenger() { return passenger; }
    public void setPassenger(UserSummaryDTO passenger) { this.passenger = passenger; }
    public LocationDTO getLocations() { return locations; }
    public void setLocations(LocationDTO locations) { this.locations = locations; }
    public FeedbackDTO getFeedback() { return feedback; }
    public void setFeedback(FeedbackDTO feedback) { this.feedback = feedback; }
}