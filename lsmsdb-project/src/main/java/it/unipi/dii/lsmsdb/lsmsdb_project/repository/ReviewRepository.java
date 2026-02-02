package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    // Tutte le recensioni ricevute da un utente (es. Driver)
    List<Review> findByTargetUserId(String targetUserId);

    // Tutte le recensioni scritte da un utente
    List<Review> findByAuthorId(String authorId);
}