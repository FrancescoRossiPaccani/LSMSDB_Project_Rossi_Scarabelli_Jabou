package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class RouteSegment {

    @Id @GeneratedValue
    private Long id;
    private String rideMongoId;
    private int sequenceOrder;
    @TargetNode
    private City nextCity;

    public RouteSegment() {
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }

    public String getRideMongoId() { return rideMongoId; }
    public void setRideMongoId(String rideMongoId) { this.rideMongoId = rideMongoId; }

    public int getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(int sequenceOrder) { this.sequenceOrder = sequenceOrder; }

    public City getNextCity() { return nextCity; }
    public void setNextCity(City nextCity) { this.nextCity = nextCity; }
}