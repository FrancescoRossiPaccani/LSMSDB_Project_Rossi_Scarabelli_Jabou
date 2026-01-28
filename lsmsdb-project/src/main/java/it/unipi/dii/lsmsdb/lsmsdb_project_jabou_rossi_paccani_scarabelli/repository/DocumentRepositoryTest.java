package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.DocumentTest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepositoryTest extends MongoRepository<DocumentTest, String> {
}