package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {
}