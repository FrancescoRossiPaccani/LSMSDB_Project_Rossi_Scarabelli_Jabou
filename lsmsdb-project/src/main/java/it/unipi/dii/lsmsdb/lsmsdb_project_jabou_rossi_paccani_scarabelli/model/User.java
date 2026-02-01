package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Field("personalInfo")
    private PersonalInfo personalInfo;

    @Field("documents")
    private List<UserDocument> documents;

    @Field("driverInfo")
    private DriverInfo driverInfo;

    @Field("reviews_driver")
    private ReviewStats reviewsDriver;

    // Note: The JSON has a typo "passanger", we must match it exactly!
    @Field("reviews_passanger")
    private ReviewStats reviewsPassenger;

    private String status;

    // --- GETTERS AND SETTERS ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public PersonalInfo getPersonalInfo() { return personalInfo; }
    public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }
    public List<UserDocument> getDocuments() { return documents; }
    public void setDocuments(List<UserDocument> documents) { this.documents = documents; }
    public DriverInfo getDriverInfo() { return driverInfo; }
    public void setDriverInfo(DriverInfo driverInfo) { this.driverInfo = driverInfo; }
    public ReviewStats getReviewsDriver() { return reviewsDriver; }
    public void setReviewsDriver(ReviewStats reviewsDriver) { this.reviewsDriver = reviewsDriver; }
    public ReviewStats getReviewsPassenger() { return reviewsPassenger; }
    public void setReviewsPassenger(ReviewStats reviewsPassenger) { this.reviewsPassenger = reviewsPassenger; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // --- INNER CLASSES (To keep it simple in one file) ---

    public static class PersonalInfo {
        private String name;
        private String surname;
        private String email;
        private String phone;
        private int age;
        private String gender;
        private String location;
        @Field("is_identity_verified")
        private boolean identityVerified;

        // Getters/Setters for PersonalInfo
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public boolean isIdentityVerified() { return identityVerified; }
        public void setIdentityVerified(boolean identityVerified) { this.identityVerified = identityVerified; }
    }

    public static class UserDocument {
        private String type;
        private String documentId;
        private String expirationDate;
        private boolean isValid;

        // Getters/Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        public String getExpirationDate() { return expirationDate; }
        public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }
    }

    public static class DriverInfo {
        @Field("avg_acceptance_rate")
        private double avgAcceptanceRate;
        @Field("number_of_acceptance")
        private int numberOfAcceptance;
        private License license;
        private List<CarRef> cars;

        // Getters/Setters
        public double getAvgAcceptanceRate() { return avgAcceptanceRate; }
        public void setAvgAcceptanceRate(double avgAcceptanceRate) { this.avgAcceptanceRate = avgAcceptanceRate; }
        public int getNumberOfAcceptance() { return numberOfAcceptance; }
        public void setNumberOfAcceptance(int numberOfAcceptance) { this.numberOfAcceptance = numberOfAcceptance; }
        public License getLicense() { return license; }
        public void setLicense(License license) { this.license = license; }
        public List<CarRef> getCars() { return cars; }
        public void setCars(List<CarRef> cars) { this.cars = cars; }
    }

    public static class License {
        private String licenseId;
        private boolean isValid;

        // Getters/Setters
        public String getLicenseId() { return licenseId; }
        public void setLicenseId(String licenseId) { this.licenseId = licenseId; }
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }
    }

    public static class CarRef {
        private String carId;
        private String model;

        // Getters/Setters
        public String getCarId() { return carId; }
        public void setCarId(String carId) { this.carId = carId; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    public static class ReviewStats {
        @Field("average_rating")
        private double averageRating;
        private int count;
        private List<Integer> review; // Stores IDs of reviews

        // Getters/Setters
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public List<Integer> getReview() { return review; }
        public void setReview(List<Integer> review) { this.review = review; }
    }
}