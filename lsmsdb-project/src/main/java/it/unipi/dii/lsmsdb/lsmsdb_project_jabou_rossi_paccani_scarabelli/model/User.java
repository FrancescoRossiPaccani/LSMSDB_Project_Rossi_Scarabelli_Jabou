package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private int age;
    private String gender;
    private String location;
    private boolean isIdentityVerified;

    private int role;
    private boolean isLicenseVerified;
    private double averageRatingPassenger;
    private double averageRatingDriver;
    private boolean isIDVerified;
    private String bankAccountId;
    private List<Car> cars = new ArrayList<>();

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isIdentityVerified() {
        return isIdentityVerified;
    }

    public void setIdentityVerified(boolean identityVerified) {
        isIdentityVerified = identityVerified;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean isLicenseVerified() {
        return isLicenseVerified;
    }

    public void setLicenseVerified(boolean licenseVerified) {
        isLicenseVerified = licenseVerified;
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

    public boolean isIDVerified() {
        return isIDVerified;
    }

    public void setIDVerified(boolean IDVerified) {
        isIDVerified = IDVerified;
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
}