package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    // Trova le corse "OPEN" che hanno ancora posti disponibili
    @Query("{ 'status': 'OPEN', 'booking_state.available_seats': { $gt: 0 } }")
    List<Ride> findAvailableRides();

    // Trova corse dirette tra due città (salvate come stringhe in Mongo)
    @Query("{ 'route.origin': ?0, 'route.destination': ?1, 'status': 'OPEN' }")
    List<Ride> findByOriginAndDestination(String origin, String destination);

    // Trova corse create da un autista specifico
    @Query("{ 'driver.id': ?0 }")
    List<Ride> findByDriverId(String driverId);
}