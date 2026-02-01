package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Location;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LocationRepository extends Neo4jRepository<Location, String> {

    @Query("MATCH (l:Location) " +
            "WHERE l.name IS NOT NULL " +
            "WITH l, point.distance(point({latitude: l.lat, longitude: l.lon}), point({latitude: $userLat, longitude: $userLon})) AS walkDist " +
            "WHERE walkDist < $radiusInMeters " +
            "RETURN l.name AS name, walkDist AS distance " +
            "ORDER BY walkDist ASC")
    List<NearbyLocationProjection> findNearbyNodes(@Param("userLat") Double userLat,
                                                   @Param("userLon") Double userLon,
                                                   @Param("radiusInMeters") Double radiusInMeters);

    interface NearbyLocationProjection {
        String getName();
        Double getDistance();
    }

    @Query("MATCH (l:Location) " +
            "WITH l, point.distance(point({latitude: l.lat, longitude: l.lon}), point({latitude: $userLat, longitude: $userLon})) AS dist " +
            "WHERE dist < 2000 " +
            "RETURN l.name ORDER BY dist ASC LIMIT 1")
    String findNearestNode(@Param("userLat") Double userLat, @Param("userLon") Double userLon);

    @Query("MATCH (a:Location {name: $startNode}), (b:Location {name: $endNode}) " +
            "MATCH p=shortestPath((a)-[:NEXT_STOP*]->(b)) " +
            "RETURN nodes(p)")
    List<Location> findShortestPath(@Param("startNode") String startNode, @Param("endNode") String endNode);
}