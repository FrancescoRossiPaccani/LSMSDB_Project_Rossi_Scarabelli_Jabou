package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Tell Spring exactly where to find your MongoDB repositories
@org.springframework.data.mongodb.repository.config.EnableMongoRepositories(
        basePackages = "it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository"
)
public class LsmsdbProjectJabouRossiPaccaniScarabelliApplication {
    public static void main(String[] args) {
        var context = SpringApplication.run(LsmsdbProjectJabouRossiPaccaniScarabelliApplication.class, args);

        // THE PROBE
        org.springframework.data.mongodb.core.MongoTemplate template = context.getBean(org.springframework.data.mongodb.core.MongoTemplate.class);
        String dbName = template.getDb().getName();
        long rideCount = template.getCollection("rides").countDocuments();

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("CONNECTED TO DATABASE: " + dbName);
        System.out.println("RIDES FOUND IN COLLECTION: " + rideCount);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}