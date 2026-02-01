package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Location")
public class Location {
    @Id
    private String name;
    private Double lat;
    private Double lon;
    private String type;

    // Standard Constructor
    public Location() {}

    // Manual Getters and Setters (Breaking the Lombok dependency)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}