package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    // We use a regular expression ($regex) to force a match even if there
    // are hidden formatting differences between the Java string and Mongo data
    @Query("{ 'passenger.id' : { $regex: ?0, $options: 'i' } }")
    List<Booking> findByPassengerId(String passengerId);

    @Query("{ 'driver.id' : ?0 }")
    List<Booking> findByDriverId(String driverId);

}