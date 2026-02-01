package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Point;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends Neo4jRepository<Point, String> {

    /**
     * Trova quali 'route_id' collegano due località (es. "Pisa" -> "Firenze").
     * Usa la label :Location per trovare i nodi per nome.
     */
    @Query("MATCH (start:Location {name: $from}), (end:Location {name: $to}) " +
            "MATCH path = (start)-[:NEXT_STOP*]->(end) " +
            "UNWIND relationships(path) AS rel " +
            "WITH DISTINCT rel.route_id AS rideId " +
            "RETURN rideId")
    List<String> findRideIdsBetweenLocations(@Param("from") String from, @Param("to") String to);

    /**
     * Restituisce l'intero percorso (lista di punti) tra due località.
     * Utile per disegnare la mappa nel frontend.
     */
    @Query("MATCH p=(start:Location {name: $from})-[:NEXT_STOP*]->(end:Location {name: $to}) " +
            "RETURN p LIMIT 1")
    Iterable<Point> findPathPoints(@Param("from") String from, @Param("to") String to);
}