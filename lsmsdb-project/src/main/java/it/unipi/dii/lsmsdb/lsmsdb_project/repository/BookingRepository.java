package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    // Regex case-insensitive (Codice del collega)
    @Query("{ 'passenger.id' : { $regex: ?0, $options: 'i' } }")
    List<Booking> findByPassengerId(String passengerId);

    @Query("{ 'driver.id' : ?0 }")
    List<Booking> findByDriverId(String driverId);
}