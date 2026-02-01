package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id; // es. "user_001"

    private PersonalInfo personalInfo;
    private List<UserDocument> documents;
    private DriverInfo driverInfo;

    @Field("reviews_driver")
    private ReviewStats reviewsDriver;

    @Field("reviews_passanger") // Nota: nel tuo JSON è scritto "passanger" con la A
    private ReviewStats reviewsPassenger;

    private String status;

    public User() {}
    // Getter e Setter per tutto...

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public List<UserDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<UserDocument> documents) {
        this.documents = documents;
    }

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
    }

    public ReviewStats getReviewsDriver() {
        return reviewsDriver;
    }

    public void setReviewsDriver(ReviewStats reviewsDriver) {
        this.reviewsDriver = reviewsDriver;
    }

    public ReviewStats getReviewsPassenger() {
        return reviewsPassenger;
    }

    public void setReviewsPassenger(ReviewStats reviewsPassenger) {
        this.reviewsPassenger = reviewsPassenger;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // --- CLASSI INTERNE PER MAPPING ---

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

        public PersonalInfo() {
        }

        // Getter/Setter

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

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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
            return identityVerified;
        }

        public void setIdentityVerified(boolean identityVerified) {
            this.identityVerified = identityVerified;
        }
    }

    public static class DriverInfo {
        @Field("avg_acceptance_rate")
        private double avgAcceptanceRate;
        @Field("number_of_acceptance")
        private int numberOfAcceptance;
        private LicenseInfo license;
        private List<CarSummary> cars; // Solo il riassunto, non l'auto intera

        public DriverInfo() {
        }
        // Getter/Setter

        public double getAvgAcceptanceRate() {
            return avgAcceptanceRate;
        }

        public void setAvgAcceptanceRate(double avgAcceptanceRate) {
            this.avgAcceptanceRate = avgAcceptanceRate;
        }

        public int getNumberOfAcceptance() {
            return numberOfAcceptance;
        }

        public void setNumberOfAcceptance(int numberOfAcceptance) {
            this.numberOfAcceptance = numberOfAcceptance;
        }

        public LicenseInfo getLicense() {
            return license;
        }

        public void setLicense(LicenseInfo license) {
            this.license = license;
        }

        public List<CarSummary> getCars() {
            return cars;
        }

        public void setCars(List<CarSummary> cars) {
            this.cars = cars;
        }
    }

    public static class LicenseInfo {
        private String licenseId;
        private boolean isValid;
        // Getter/Setter
    }

    public static class CarSummary {
        private String carId;
        private String model;

        public CarSummary() {
        }

        // Getter/Setter

        public String getCarId() {
            return carId;
        }

        public void setCarId(String carId) {
            this.carId = carId;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

    public static class ReviewStats {
        @Field("average_rating")
        private double averageRating;
        private int count;
        private List<Integer> review; // IDs delle recensioni

        public ReviewStats() {
        }

        // Getter/Setter

        public double getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(double averageRating) {
            this.averageRating = averageRating;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Integer> getReview() {
            return review;
        }

        public void setReview(List<Integer> review) {
            this.review = review;
        }
    }

    public static class UserDocument { // Rinominato da "Document" per evitare conflitti
        private String type;
        private String documentId;
        private String expirationDate; // O LocalDate se converti
        private boolean isValid;

        public UserDocument() {
        }

        // Getter/Setter

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
        }

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean valid) {
            isValid = valid;
        }
    }
}