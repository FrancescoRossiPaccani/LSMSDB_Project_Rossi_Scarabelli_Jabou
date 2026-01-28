package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "sessions", timeToLive = 86400)
public class Session {

    @Id
    private String id;
    private String userId;
    private String username;

    public Session() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}