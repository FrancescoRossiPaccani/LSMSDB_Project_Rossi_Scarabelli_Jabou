package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto;

public class LocationDTO {
    private String pickup;
    private String dropoff;

    public LocationDTO() {}
    public LocationDTO(String pickup, String dropoff) {
        this.pickup = pickup;
        this.dropoff = dropoff;
    }

    public String getPickup() { return pickup; }
    public void setPickup(String pickup) { this.pickup = pickup; }
    public String getDropoff() { return dropoff; }
    public void setDropoff(String dropoff) { this.dropoff = dropoff; }
}