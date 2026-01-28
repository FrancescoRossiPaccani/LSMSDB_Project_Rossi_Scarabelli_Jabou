package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.GraphTest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.DocumentTest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.KVTest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.GraphRepositoryTest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.KVRepositoryTest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.DocumentRepositoryTest;
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
	CommandLineRunner testAllDatabases(DocumentRepositoryTest mongoRepo,
									   KVRepositoryTest redisRepo,
									   GraphRepositoryTest neo4jRepo) {
		return args -> {
			System.out.println("\n\n========================================");
			System.out.println("🚀  STARTING MULTI-DATABASE TEST  🚀");
			System.out.println("========================================\n");

			try {
				// --- 1. MONGODB TEST ---
				System.out.println("🍃 Test MongoDB...");
				DocumentTest mongoUser = new DocumentTest(null, "MarioMongo", "mario@mongo.com", 28);
				mongoRepo.save(mongoUser);
				System.out.println("   ✅ Salvato su Mongo: " + mongoUser.getUsername());

				// --- 2. REDIS TEST ---
				System.out.println("🔴 Test Redis...");
				// Redis vuole un ID esplicito di solito
				KVTest session = new KVTest("session_12345", "2026-01-27 16:00");
				redisRepo.save(session);

				// Verifichiamo leggendo
				KVTest retrievedSession = redisRepo.findById("session_12345").orElse(null);
				if (retrievedSession != null) {
					System.out.println("   ✅ Salvato e Letto da Redis: " + retrievedSession.getLastLogin());
				} else {
					System.out.println("   ❌ Errore lettura Redis");
				}

				// --- 3. NEO4J TEST ---
				System.out.println("🕸️ Test Neo4j...");
				GraphTest graphPerson = new GraphTest("LuigiGrafo");
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