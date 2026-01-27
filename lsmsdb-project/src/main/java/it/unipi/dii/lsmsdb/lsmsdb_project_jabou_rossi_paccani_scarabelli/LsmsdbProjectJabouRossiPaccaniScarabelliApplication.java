package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Person;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.UserSession;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.PersonRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.SessionRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LsmsdbProjectJabouRossiPaccaniScarabelliApplication {

	public static void main(String[] args) {
		SpringApplication.run(LsmsdbProjectJabouRossiPaccaniScarabelliApplication.class, args);
	}

	@Bean
	CommandLineRunner testAllDatabases(UserRepository mongoRepo,
									   SessionRepository redisRepo,
									   PersonRepository neo4jRepo) {
		return args -> {
			System.out.println("\n\n========================================");
			System.out.println("🚀  STARTING MULTI-DATABASE TEST  🚀");
			System.out.println("========================================\n");

			try {
				// --- 1. MONGODB TEST ---
				System.out.println("🍃 Test MongoDB...");
				User mongoUser = new User(null, "MarioMongo", "mario@mongo.com", 28);
				mongoRepo.save(mongoUser);
				System.out.println("   ✅ Salvato su Mongo: " + mongoUser.getUsername());

				// --- 2. REDIS TEST ---
				System.out.println("🔴 Test Redis...");
				// Redis vuole un ID esplicito di solito
				UserSession session = new UserSession("session_12345", "2026-01-27 16:00");
				redisRepo.save(session);

				// Verifichiamo leggendo
				UserSession retrievedSession = redisRepo.findById("session_12345").orElse(null);
				if (retrievedSession != null) {
					System.out.println("   ✅ Salvato e Letto da Redis: " + retrievedSession.getLastLogin());
				} else {
					System.out.println("   ❌ Errore lettura Redis");
				}

				// --- 3. NEO4J TEST ---
				System.out.println("🕸️ Test Neo4j...");
				Person graphPerson = new Person("LuigiGrafo");
				neo4jRepo.save(graphPerson);
				System.out.println("   ✅ Salvato Nodo Neo4j: " + graphPerson.getName() + " (ID: " + graphPerson.getId() + ")");

			} catch (Exception e) {
				System.out.println("❌ ERRORE CRITICO: " + e.getMessage());
				e.printStackTrace();
			}

			System.out.println("\n========================================");
			System.out.println("🏁  ALL TESTS COMPLETED  🏁");
			System.out.println("========================================\n\n");
		};
	}
}