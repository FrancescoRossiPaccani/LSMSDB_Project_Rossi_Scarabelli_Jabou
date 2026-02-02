package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, String> {
    // Trova tutte le notifiche di un utente (grazie a @Indexed su recipientUserId)
    List<Notification> findByRecipientUserId(String recipientUserId);
}