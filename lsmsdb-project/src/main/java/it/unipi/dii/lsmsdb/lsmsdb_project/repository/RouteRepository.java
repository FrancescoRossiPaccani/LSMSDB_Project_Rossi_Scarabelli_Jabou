package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Location;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends Neo4jRepository<Location, String> {

    // --- LOGICA DEL COLLEGA (Geospatial) ---
    @Query("MATCH (l:Location) " +
            "WHERE l.name IS NOT NULL " +
            "WITH l, point.distance(point({latitude: l.lat, longitude: l.lon}), " +
            "point({latitude: $userLat, longitude: $userLon})) AS dist " +
            "WHERE dist < $radiusInMeters " +
            "RETURN l.name AS name, dist AS distance " +
            "ORDER BY dist ASC")
    List<NearbyLocationProjection> findNearbyNodes(@Param("userLat") Double userLat,
                                                   @Param("userLon") Double userLon,
                                                   @Param("radiusInMeters") Double radiusInMeters);

    interface NearbyLocationProjection {
        String getName();
        Double getDistance();
    }

    // --- LOGICA DINAMICA PERFETTA (Crea Nodi con Coordinate) ---
    @Query("MERGE (a:Location {name: $from}) " +
            "ON CREATE SET a.lat = $fromLat, a.lon = $fromLon " +
            "MERGE (b:Location {name: $to}) " +
            "ON CREATE SET b.lat = $toLat, b.lon = $toLon " +
            "MERGE (a)-[r:HAS_RIDE]->(b) " +
            "SET r.rideId = $rideId, r.price = $price")
    void createRideRelationship(@Param("from") String from,
                                @Param("fromLat") Double fromLat,
                                @Param("fromLon") Double fromLon,
                                @Param("to") String to,
                                @Param("toLat") Double toLat,
                                @Param("toLon") Double toLon,
                                @Param("rideId") String rideId,
                                @Param("price") Double price);
}