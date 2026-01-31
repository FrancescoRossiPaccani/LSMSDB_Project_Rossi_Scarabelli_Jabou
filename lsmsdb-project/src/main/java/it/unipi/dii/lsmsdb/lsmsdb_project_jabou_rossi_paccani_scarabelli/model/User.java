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
}