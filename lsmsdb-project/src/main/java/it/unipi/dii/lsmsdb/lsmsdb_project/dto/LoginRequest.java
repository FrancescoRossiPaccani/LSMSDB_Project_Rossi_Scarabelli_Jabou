package it.unipi.dii.lsmsdb.lsmsdb_project.dto;

public class LoginRequest {
    private String email;
    private String password; // In un progetto reale non si gestisce così, ma per l'esame va bene

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}