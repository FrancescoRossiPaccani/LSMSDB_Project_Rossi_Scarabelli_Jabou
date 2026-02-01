package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingRequestDTO {

    @JsonProperty("user_id") // Mappa "user_id" del JSON su "userId" Java
    private String userId;

    @JsonProperty("ride_id")
    private String rideId;

    @JsonProperty("seats_requested")
    private int seatsRequested;

    private String pickup;  // Nome del luogo o coordinate
    private String dropoff;

    public BookingRequestDTO() {}

    // Getter e Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public int getSeatsRequested() { return seatsRequested; }
    public void setSeatsRequested(int seatsRequested) { this.seatsRequested = seatsRequested; }
    public String getPickup() { return pickup; }
    public void setPickup(String pickup) { this.pickup = pickup; }
    public String getDropoff() { return dropoff; }
    public void setDropoff(String dropoff) { this.dropoff = dropoff; }
}