package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("sessions") // Questo dice a Redis di creare una "tabella" di hash
public class UserSession {

    @Id
    private String id;
    private String lastLogin;

    public UserSession() {}

    public UserSession(String id, String lastLogin) {
        this.id = id;
        this.lastLogin = lastLogin;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
}