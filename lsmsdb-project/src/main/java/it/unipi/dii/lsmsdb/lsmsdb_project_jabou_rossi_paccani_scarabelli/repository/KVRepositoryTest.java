package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.KVTest;
import org.springframework.data.repository.CrudRepository;

// Nota: Per Redis si usa spesso CrudRepository invece di MongoRepository
public interface KVRepositoryTest extends CrudRepository<KVTest, String> {
}