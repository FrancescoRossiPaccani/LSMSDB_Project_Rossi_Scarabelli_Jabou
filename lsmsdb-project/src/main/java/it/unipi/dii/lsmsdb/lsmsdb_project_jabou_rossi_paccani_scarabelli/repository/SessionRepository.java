package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.UserSession;
import org.springframework.data.repository.CrudRepository;

// Nota: Per Redis si usa spesso CrudRepository invece di MongoRepository
public interface SessionRepository extends CrudRepository<UserSession, String> {
}