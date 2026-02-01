package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    // Trova le prenotazioni effettuate da un passeggero
    @Query("{ 'passenger.id' : ?0 }")
    List<Booking> findByPassengerId(String passengerId);

    // Trova le prenotazioni ricevute da un autista
    @Query("{ 'driver.id' : ?0 }")
    List<Booking> findByDriverId(String driverId);

    // Trova le prenotazioni per una specifica corsa (utile per vedere chi c'è a bordo)
    @Query("{ 'ride_id' : ?0 }")
    List<Booking> findByRideId(String rideId);
}