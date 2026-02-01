package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    @org.springframework.data.mongodb.repository.Query("{ 'route.origin': ?0, 'route.destination': ?1, 'bookingState.availableSeats': { $gt: 0 }, 'status': 'OPEN' }")
    java.util.List<it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride> findAvailableRides(String origin, String destination);
    List<Ride> findByRouteOriginIgnoreCaseAndRouteDestinationIgnoreCase(String origin, String destination);
}