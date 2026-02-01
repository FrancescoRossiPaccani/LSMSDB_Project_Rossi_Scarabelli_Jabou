package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Cerca un utente tramite email (che è dentro l'oggetto personalInfo)
    @Query("{ 'personalInfo.email' : ?0 }")
    Optional<User> findByEmail(String email);

    // Trova tutti gli autisti attivi (chi ha driverInfo e status ACTIVE)
    @Query("{ 'driverInfo' : { $exists: true }, 'status': 'ACTIVE' }")
    List<User> findActiveDrivers();
}