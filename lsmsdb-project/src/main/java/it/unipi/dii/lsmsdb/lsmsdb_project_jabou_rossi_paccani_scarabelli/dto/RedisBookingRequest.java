package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RedisBookingRequest {
    @JsonProperty("user_id")
    private String user_id;

    @JsonProperty("ride_id")
    private String ride_id;

    @JsonProperty("seats_requested")
    private Integer seats_requested;

    private String pickup;
    private String dropoff;

    // Explicit Getters/Setters if not using Lombok
    public String getRide_id() { return ride_id; }
    public String getUser_id() { return user_id; }
    public Integer getSeats_requested() { return seats_requested; }
    public String getPickup() { return pickup; }
    public String getDropoff() { return dropoff; }
}