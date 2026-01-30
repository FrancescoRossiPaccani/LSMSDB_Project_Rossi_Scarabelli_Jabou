package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String bio;
    private String role;
    private boolean isLicenseVerified;
    private String phoneNumber;
    private double averageRatingPassenger;
    private double averageRatingDriver;
    private boolean isIDVerified;
    private String bankAccountId;
    private List<Car> cars = new ArrayList<>();

    public User() {
    }

    public boolean isLicenseVerified() {
        return isLicenseVerified;
    }

    public void setLicenseVerified(boolean licenseVerified) {
        isLicenseVerified = licenseVerified;
    }

    public boolean isIDVerified() {
        return isIDVerified;
    }

    public void setIDVerified(boolean IDVerified) {
        isIDVerified = IDVerified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public double getAverageRatingPassenger() {
        return averageRatingPassenger;
    }

    public void setAverageRatingPassenger(double averageRatingPassenger) {
        this.averageRatingPassenger = averageRatingPassenger;
    }

    public double getAverageRatingDriver() {
        return averageRatingDriver;
    }

    public void setAverageRatingDriver(double averageRatingDriver) {
        this.averageRatingDriver = averageRatingDriver;
    }
}