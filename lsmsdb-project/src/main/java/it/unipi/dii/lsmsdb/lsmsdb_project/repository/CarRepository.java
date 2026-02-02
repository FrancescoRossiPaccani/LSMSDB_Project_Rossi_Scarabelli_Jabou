package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarRepository extends MongoRepository<Car, String> {
    // Trova le auto di un proprietario specifico
    List<Car> findByOwnerId(String ownerId);
}