package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.*;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Test_0 {

    // --- Repositories Injection ---
    @Autowired private UserRepository userRepository;               // MongoDB
    @Autowired private CityRepository cityRepository;               // Neo4j
    @Autowired private BookingRequestRepository requestRepository;  // Redis

    // --- Test Data IDs ---
    private String testUserId;
    private Long testCityId;
    private String testRequestId;

    @Test
    void testPolyglotPersistence() {
        // HEADER
        System.out.println("==================================================================");
        System.out.println("  TEST REPORT: POLYGLOT PERSISTENCE INTEGRATION CHECK");
        System.out.println("==================================================================");

        // ---------------------------------------------------------
        // 1. MONGODB SECTION
        // ---------------------------------------------------------
        System.out.println("\n### 1. MongoDB Integration Test");
        System.out.println("---------------------------------");
        System.out.println("> Status: Initializing User object...");

        User user = new User();
        user.setUsername("TestUser_01");
        user.setEmail("test01@example.com");
        user.setPassword("secretPassword");
        user.setRole("PASSENGER");
        user.setPhoneNumber("333998877");

        System.out.println("> Status: Saving to MongoDB...");
        User savedUser = userRepository.save(user);
        testUserId = savedUser.getId();

        Assertions.assertNotNull(testUserId, "MongoDB User ID should not be null");
        System.out.println("> [SUCCESS] User saved successfully.");
        System.out.println("> [INFO] Generated ID: " + testUserId);


        // ---------------------------------------------------------
        // 2. NEO4J SECTION
        // ---------------------------------------------------------
        System.out.println("\n### 2. Neo4j Integration Test");
        System.out.println("---------------------------------");
        System.out.println("> Status: Initializing City node...");

        City city = new City();
        city.setName("TestCity_0");
        city.setAddress("Test Street, 0");
        city.setLatitude(45.00);
        city.setLongitude(11.00);

        System.out.println("> Status: Saving to Neo4j...");
        City savedCity = cityRepository.save(city);
        testCityId = savedCity.getId();

        Assertions.assertNotNull(testCityId, "Neo4j City ID should not be null");
        System.out.println("> [SUCCESS] City node saved successfully.");
        System.out.println("> [INFO] Generated ID: " + testCityId);


        // ---------------------------------------------------------
        // 3. REDIS SECTION
        // ---------------------------------------------------------
        System.out.println("\n### 3. Redis Integration Test");
        System.out.println("---------------------------------");
        System.out.println("> Status: Initializing BookingRequest hash...");

        BookingRequest request = new BookingRequest();
        String customRedisId = "req_test_" + System.currentTimeMillis();
        request.setId(customRedisId);
        request.setPassengerId(testUserId);
        request.setSeatsRequested(1);
        request.setProposedPrice(15.50);

        System.out.println("> Status: Saving to Redis...");
        BookingRequest savedRequest = requestRepository.save(request);
        testRequestId = savedRequest.getId();

        boolean existsRedis = requestRepository.existsById(testRequestId);
        Assertions.assertTrue(existsRedis, "Request key should exist in Redis");

        System.out.println("> [SUCCESS] Request stored successfully.");
        System.out.println("> [INFO] Key Used: " + testRequestId);

        // FOOTER
        System.out.println("\n==================================================================");
        System.out.println("  TEST RESULT: PASSED (All databases operational)");
        System.out.println("==================================================================");
    }

    // ---------------------------------------------------------
    // CLEANUP PHASE
    // ---------------------------------------------------------
    @AfterEach
    void tearDown() {
        System.out.println("\n\n--- TEARDOWN & CLEANUP REPORT ---");

        // 1. Redis Cleanup
        if (testRequestId != null && requestRepository.existsById(testRequestId)) {
            requestRepository.deleteById(testRequestId);
            System.out.println("[CLEANUP] Redis: Removed key " + testRequestId);
        } else {
            System.out.println("[CLEANUP] Redis: Nothing to remove.");
        }

        // 2. Neo4j Cleanup
        if (testCityId != null && cityRepository.existsById(testCityId)) {
            cityRepository.deleteById(testCityId);
            System.out.println("[CLEANUP] Neo4j: Removed node " + testCityId);
        } else {
            System.out.println("[CLEANUP] Neo4j: Nothing to remove.");
        }

        // 3. MongoDB Cleanup
        if (testUserId != null && userRepository.existsById(testUserId)) {
            userRepository.deleteById(testUserId);
            System.out.println("[CLEANUP] MongoDB: Removed document " + testUserId);
        } else {
            System.out.println("[CLEANUP] MongoDB: Nothing to remove.");
        }

        System.out.println("--- END OF REPORT ---");
    }
}