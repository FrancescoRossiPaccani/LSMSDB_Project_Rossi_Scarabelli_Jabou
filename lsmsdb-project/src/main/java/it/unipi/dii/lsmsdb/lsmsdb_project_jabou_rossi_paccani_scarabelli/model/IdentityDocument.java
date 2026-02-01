package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import java.time.LocalDate;

public class IdentityDocument { // Rinominato
    private String number; // ID del documento reale
    private String type;   // "PATENTE", "CARTA_IDENTITA"
    private LocalDate expirationDate;
    private boolean isValid;

    public IdentityDocument() {
    }

    // Getter e Setter

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}