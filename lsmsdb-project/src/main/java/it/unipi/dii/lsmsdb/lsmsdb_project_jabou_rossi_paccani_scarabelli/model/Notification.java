package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import java.time.LocalDateTime;

// TTL di 7 giorni (604800 secondi) per non intasare Redis
@RedisHash(value = "notifications", timeToLive = 604800)
public class Notification {
    @Id
    private String id;

    @Indexed // Fondamentale per fare findByRecipientId
    private String recipientUserId;

    private String type; // es. "BOOKING_CONFIRMED", "RIDE_CANCELLED"
    private String message;
    private LocalDateTime timestamp;
    private boolean read;

    public Notification() {
    }
    // Getter e Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(String recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}