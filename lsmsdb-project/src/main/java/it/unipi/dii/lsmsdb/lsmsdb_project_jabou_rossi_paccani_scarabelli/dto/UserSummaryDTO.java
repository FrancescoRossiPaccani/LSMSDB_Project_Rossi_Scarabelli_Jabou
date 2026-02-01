package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto;

public class UserSummaryDTO {
    private String id;
    private String name;           // Nome + Cognome o solo Nome
    private String profilePicture; // URL foto
    private double rating;         // Media voti

    public UserSummaryDTO() {}

    public UserSummaryDTO(String id, String name, String profilePicture, double rating) {
        this.id = id;
        this.name = name;
        this.profilePicture = profilePicture;
        this.rating = rating;
    }

    // Getter e Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}