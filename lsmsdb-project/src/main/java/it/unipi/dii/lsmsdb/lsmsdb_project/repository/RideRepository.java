package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    // Query del COLLEGA (Case insensitive search)
    List<Ride> findByRouteOriginIgnoreCaseAndRouteDestinationIgnoreCase(String origin, String destination);

    // Query del COLLEGA (Per la ricerca geospaziale "Radius")
    List<Ride> findByRouteOriginInAndRouteDestinationIn(List<String> origins, List<String> destinations);

    // Query TUA (Utile per trovare corse attive in generale)
    @Query("{ 'status': 'OPEN', 'booking_state.available_seats': { $gt: 0 } }")
    List<Ride> findAvailableRides();
}