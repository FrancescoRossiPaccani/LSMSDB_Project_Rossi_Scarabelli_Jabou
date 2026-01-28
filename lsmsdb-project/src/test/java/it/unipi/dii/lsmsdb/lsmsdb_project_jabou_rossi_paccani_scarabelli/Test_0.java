package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.*;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class Test_0 {

    // --- Iniezione dei Repository ---
    @Autowired private UserRepository userRepository;               // MongoDB
    @Autowired private CityRepository cityRepository;               // Neo4j
    @Autowired private BookingRequestRepository requestRepository;  // Redis

    // --- Variabili per memorizzare gli ID dei dati di test ---
    private String testUserId;
    private Long testCityId;
    private String testRequestId;

    @Test
    void testPolyglotPersistence() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("AVVIO TEST_0: Verifica Connessione MongoDB, Neo4j e Redis");
        System.out.println("----------------------------------------------------------------");

        // ---------------------------------------------------------
        // 1. TEST MONGODB (Creazione User)
        // ---------------------------------------------------------
        System.out.println("[MONGODB] Tento il salvataggio di un utente...");
        User user = new User();
        user.setUsername("TestUser_01");
        user.setEmail("test01@example.com");
        user.setPassword("passwordSegreta");
        user.setRole("PASSENGER");
        user.setPhoneNumber("333998877"); // Uso int come da tuo file attuale

        User savedUser = userRepository.save(user);
        testUserId = savedUser.getId(); // Salvo l'ID per cancellarlo dopo

        Assertions.assertNotNull(testUserId, "L'ID dell'utente MongoDB non dovrebbe essere null");
        System.out.println("[MONGODB] SUCCESSO: Utente salvato con ID: " + testUserId);


        // ---------------------------------------------------------
        // 2. TEST NEO4J (Creazione City)
        // ---------------------------------------------------------
        System.out.println("[NEO4J] Tento il salvataggio di una città...");
        City city = new City();
        city.setName("Città_Test_0");
        city.setAddress("Via dei Test, 0");
        city.setLatitude(45.00);
        city.setLongitude(11.00);

        City savedCity = cityRepository.save(city);
        testCityId = savedCity.getId(); // Salvo l'ID per cancellarlo dopo

        Assertions.assertNotNull(testCityId, "L'ID della città Neo4j non dovrebbe essere null");
        System.out.println("[NEO4J] SUCCESSO: Città salvata con ID: " + testCityId);


        // ---------------------------------------------------------
        // 3. TEST REDIS (Creazione BookingRequest)
        // ---------------------------------------------------------
        System.out.println("[REDIS] Tento il salvataggio di una richiesta...");
        BookingRequest request = new BookingRequest();
        // Redis spesso richiede di settare l'ID manualmente se non è autogenerato
        String customRedisId = "req_test_" + System.currentTimeMillis();
        request.setId(customRedisId);
        request.setPassengerId(testUserId); // Collego (logicamente) all'utente appena creato
        request.setSeatsRequested(1);
        request.setProposedPrice(15.50);

        BookingRequest savedRequest = requestRepository.save(request);
        testRequestId = savedRequest.getId();

        // Verifica immediata di lettura
        boolean existsRedis = requestRepository.existsById(testRequestId);
        Assertions.assertTrue(existsRedis, "La richiesta dovrebbe esistere in Redis");
        System.out.println("[REDIS] SUCCESSO: Richiesta salvata con ID: " + testRequestId);

        System.out.println("----------------------------------------------------------------");
        System.out.println("TUTTI I TEST DI SCRITTURA COMPLETATI CON SUCCESSO");
        System.out.println("----------------------------------------------------------------");
    }

    // ---------------------------------------------------------
    // CLEANUP (Ripristino dello stato iniziale)
    // Questo metodo viene eseguito SEMPRE alla fine, anche se il test fallisce.
    // ---------------------------------------------------------
    @AfterEach
    void tearDown() {
        System.out.println("--- INIZIO PULIZIA DATI DI TEST ---");

        // 1. Pulizia Redis
        if (testRequestId != null && requestRepository.existsById(testRequestId)) {
            requestRepository.deleteById(testRequestId);
            System.out.println("[REDIS] Cancellata richiesta di test: " + testRequestId);
        }

        // 2. Pulizia Neo4j
        if (testCityId != null && cityRepository.existsById(testCityId)) {
            cityRepository.deleteById(testCityId);
            System.out.println("[NEO4J] Cancellata città di test: " + testCityId);
        }

        // 3. Pulizia MongoDB
        if (testUserId != null && userRepository.existsById(testUserId)) {
            userRepository.deleteById(testUserId);
            System.out.println("[MONGODB] Cancellato utente di test: " + testUserId);
        }

        System.out.println("--- PULIZIA COMPLETATA: DATABASE RIPRISTINATO ---");
    }
}