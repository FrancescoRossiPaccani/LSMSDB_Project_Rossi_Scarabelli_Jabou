package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    // 1. Fixed name to match your Service calls
    @Query("{ 'route.origin': ?0, 'route.destination': ?1, 'bookingState.availableSeats': { $gt: 0 }, 'status': 'OPEN' }")
    List<Ride> findAvailableRides(String origin, String destination);

    // 2. Standard case-insensitive search
    List<Ride> findByRouteOriginIgnoreCaseAndRouteDestinationIgnoreCase(String origin, String destination);

    // 3. The "Walking/Radius" search for lists of nearby names
    List<Ride> findByRouteOriginInAndRouteDestinationIn(List<String> origins, List<String> destinations);
}