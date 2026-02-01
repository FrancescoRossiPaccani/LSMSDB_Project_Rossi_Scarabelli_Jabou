package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    // Trova tutte le recensioni dirette a un utente specifico
    List<Review> findByTargetUserId(String targetUserId);

    // Trova recensioni scritte da un utente
    List<Review> findByAuthorId(String authorId);
}