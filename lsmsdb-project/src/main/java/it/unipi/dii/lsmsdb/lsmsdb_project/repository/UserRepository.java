package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // FONDAMENTALE PER IL LOGIN (che implementeremo tra poco)
    // Cerca dentro l'oggetto annidato personalInfo
    @Query("{ 'personalInfo.email' : ?0 }")
    Optional<User> findByEmail(String email);

    // Utile per la dashboard admin o per mostrare driver disponibili
    @Query("{ 'driverInfo' : { $exists: true }, 'status': 'ACTIVE' }")
    List<User> findActiveDrivers();

    // Cerca utenti per nome (Case insensitive) - Utile per la ricerca admin
    @Query("{ 'personalInfo.name': { $regex: ?0, $options: 'i' } }")
    List<User> findByNameRegex(String name);
}