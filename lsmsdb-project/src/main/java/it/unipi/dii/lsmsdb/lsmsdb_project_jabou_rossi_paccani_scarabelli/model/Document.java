package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import java.time.LocalDate;

public class Document {
    private String id;
    private String type;
    private LocalDate expirationDate;
    private boolean isTrue;

    public Document() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }
}
