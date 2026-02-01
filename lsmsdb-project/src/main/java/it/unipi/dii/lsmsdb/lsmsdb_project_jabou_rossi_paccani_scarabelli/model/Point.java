package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node("Point") // Labels aggiuntive (:Location, :Stop) vengono gestite dinamicamente
public class Point {

    @Id
    private String id; // L'ID custom "43.720_10.400"

    private double lat;
    private double lon;

    // Solo se il nodo è anche una Location ha un nome
    private String name;

    @Relationship(type = "NEXT_STOP", direction = Relationship.Direction.OUTGOING)
    private List<NextStopRelationship> nextStops;

    public Point() {
    }
    // Getter e Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NextStopRelationship> getNextStops() {
        return nextStops;
    }

    public void setNextStops(List<NextStopRelationship> nextStops) {
        this.nextStops = nextStops;
    }
}