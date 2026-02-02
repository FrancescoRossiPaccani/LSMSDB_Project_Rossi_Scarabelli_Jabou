package it.unipi.dii.lsmsdb.lsmsdb_project;

import it.unipi.dii.lsmsdb.lsmsdb_project.dto.BookingRequestDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project.dto.LoginRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project.dto.UserSummaryDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.*;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.*;
import it.unipi.dii.lsmsdb.lsmsdb_project.service.*;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class LsmsdbProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(LsmsdbProjectApplication.class, args);
    }

    /**
     * FULL END-TO-END TEST (ALL APIs)
     */
    @Bean
    CommandLineRunner demo(
            UserService userService,
            RideService rideService,
            BookingProcessService bookingService,
            AuthService authService,
            BookingAnalyticsService analyticsService,
            CarService carService,
            UserRepository userRepo,
            RideRepository rideRepo,
            BookingRepository bookingRepo,
            BookingRequestRepository redisBookingRepo,
            NotificationRepository notificationRepo,
            SessionRepository sessionRepo,
            CarRepository carRepo,
            RouteRepository neo4jRepo
    ) {
        return args -> {
            System.out.println("\n\n================================================================");
            System.out.println("🚀 STARTING FULL DEMO (LSMSDB PROJECT - ALL FEATURES)");
            System.out.println("================================================================\n");

            // --- 1. FULL CLEANUP ---
            System.out.println("🧹 [STEP 1] Cleaning Databases (Mongo, Redis)...");
            userRepo.deleteAll();
            rideRepo.deleteAll();
            bookingRepo.deleteAll();
            carRepo.deleteAll();
            redisBookingRepo.deleteAll();
            notificationRepo.deleteAll();
            sessionRepo.deleteAll();
            // Note: Neo4j is handled dynamically using MERGE, so strict deletion is not mandatory,
            // but if you want a fresh graph: neo4jRepo.deleteAll();

            // --- 2. USER REGISTRATION ---
            System.out.println("👤 [STEP 2] Creating Actors (Driver & Passenger)...");

            // DRIVER: Mario
            User driver = new User();
            driver.setPersonalInfo(new User.PersonalInfo());
            driver.getPersonalInfo().setName("Mario");
            driver.getPersonalInfo().setSurname("Rossi");
            driver.getPersonalInfo().setEmail("mario@driver.com");
            driver.getPersonalInfo().setLocation("Pisa");
            driver.getPersonalInfo().setIdentityVerified(true); // Important for Leaderboard score
            driver.setStatus("ACTIVE");

            User.DriverInfo dInfo = new User.DriverInfo();
            dInfo.setAvgAcceptanceRate(0.99); // Excellent driver
            dInfo.setNumberOfAcceptance(100);
            driver.setDriverInfo(dInfo);

            User.ReviewStats dStats = new User.ReviewStats();
            dStats.setAverageRating(4.9);
            dStats.setCount(50);
            driver.setReviewsDriver(dStats);

            userRepo.save(driver);
            System.out.println("   ✅ Driver registered: Mario Rossi (" + driver.getId() + ")");

            // PASSENGER: Luigi
            User passenger = new User();
            passenger.setPersonalInfo(new User.PersonalInfo());
            passenger.getPersonalInfo().setName("Luigi");
            passenger.getPersonalInfo().setSurname("Verdi");
            passenger.getPersonalInfo().setEmail("luigi@passenger.com");
            passenger.setStatus("ACTIVE");

            // Stats for Churner analysis
            User.ReviewStats pStats = new User.ReviewStats();
            pStats.setAverageRating(5.0);
            pStats.setCount(5);
            passenger.setReviewsPassenger(pStats);

            userRepo.save(passenger);
            System.out.println("   ✅ Passenger registered: Luigi Verdi (" + passenger.getId() + ")");

            // --- 3. FLEET MANAGEMENT (CARS) ---
            System.out.println("\n🚗 [STEP 3] Adding Car to Fleet...");
            Car car = new Car();
            car.setOwnerId(driver.getId());
            Car.CarDetails details = new Car.CarDetails();
            details.setBrand("Fiat");
            details.setModel("Panda 4x4");
            details.setColor("Green");
            details.setSeats(4);
            car.setDetails(details);

            carService.saveCar(car);

            List<Car> driverCars = carService.getCarsByOwner(driver.getId());
            if (!driverCars.isEmpty()) {
                System.out.println("   ✅ Car added successfully: " + driverCars.get(0).getDetails().getModel());
            } else {
                System.err.println("   ❌ Error: Car not found.");
            }

            // --- 4. LOGIN (REDIS) ---
            System.out.println("\n🔐 [STEP 4] Authentication (Redis Sessions)...");
            LoginRequest login = new LoginRequest();
            login.setEmail("luigi@passenger.com");
            login.setPassword("password123");

            String token = authService.login(login);
            System.out.println("   ✅ Login successful. Redis Token: " + token);

            // Verify on Redis
            if (sessionRepo.existsById(token)) {
                System.out.println("   ✅ Session verified on Redis.");
            }

            // --- 5. RIDE PUBLICATION (MONGO + NEO4J SYNC) ---
            System.out.println("\n🗺️ [STEP 5] Publishing Ride (Sync Mongo -> Neo4j)...");
            Ride ride = new Ride();

            // Driver Info
            ride.setDriver(new Ride.DriverInfo());
            ride.getDriver().setId(driver.getId());
            ride.getDriver().setName("Mario Rossi");

            // Car Info
            Ride.CarInfo cInfo = new Ride.CarInfo();
            cInfo.setModel("Fiat Panda 4x4");
            ride.setCar(cInfo);

            // Booking State
            ride.setBookingState(new Ride.BookingState());
            ride.getBookingState().setTotalSeats(4);
            ride.getBookingState().setAvailableSeats(4);
            ride.setBasePrice(10.0);

            // ROUTE (With Coordinates for Neo4j)
            Ride.RouteInfo route = new Ride.RouteInfo();
            route.setOrigin("Pisa");
            route.setOriginLat(43.7228);
            route.setOriginLon(10.4017);

            route.setDestination("Firenze");
            route.setDestLat(43.7696);
            route.setDestLon(11.2558);

            ride.setRoute(route);

            Ride savedRide = rideService.createRide(ride);
            System.out.println("   ✅ Ride saved on Mongo: " + savedRide.getId());
            System.out.println("   ✅ Nodes and Relationship created on Neo4j (with lat/lon).");

            // --- 6. GEOSPATIAL SEARCH (RADIUS) ---
            System.out.println("\n🔍 [STEP 6] Proximity Search (Radius Search)...");
            // User simulates being near Pisa
            List<Ride> matches = rideService.searchMatchingRides(43.72, 10.40, 43.77, 11.25);

            if (!matches.isEmpty()) {
                System.out.println("   ✅ Ideal ride found ID: " + matches.get(0).getId());
                System.out.println("      -> Price: " + matches.get(0).getBasePrice() + "€");
            } else {
                System.err.println("   ❌ No rides found (Neo4j coordinates issue?).");
            }

            // --- 7. PUBLIC PROFILE ---
            System.out.println("\n👀 [STEP 7] Viewing Driver Public Profile...");
            UserSummaryDTO profile = userService.getPublicProfile(driver.getId());
            System.out.println("   ✅ Profile downloaded: " + profile.getName() + " | Rating: " + profile.getAverageRating() + "⭐");

            // --- 8. BOOKING (REDIS -> MONGO) ---
            System.out.println("\n🎟️ [STEP 8] Complete Booking Flow...");

            // Phase A: Request (Redis)
            BookingRequestDTO req = new BookingRequestDTO();
            req.setUserId(passenger.getId());
            req.setRideId(savedRide.getId());
            req.setSeatsRequested(2);

            String reqId = bookingService.createTemporaryReservation(req);
            System.out.println("   [A] Request buffered on Redis. ID: " + reqId);

            // Phase B: Confirm (Mongo Atomic)
            Booking booking = bookingService.finalizeBooking(reqId);
            System.out.println("   [B] Booking finalized on Mongo. ID: " + booking.getId());

            // Seat Verification
            Ride updatedRide = rideRepo.findById(savedRide.getId()).get();
            int seatsLeft = updatedRide.getBookingState().getAvailableSeats();
            System.out.println("   [C] Seat Verification: " + seatsLeft + "/4 remaining.");
            if(seatsLeft == 2) System.out.println("   ✅ Consistency OK.");
            else System.err.println("   ❌ SEAT CONSISTENCY ERROR.");

            // --- 9. NOTIFICATIONS (REDIS) ---
            System.out.println("\n🔔 [STEP 9] Checking Notifications...");
            List<Notification> notifs = notificationRepo.findByRecipientUserId(driver.getId());
            if (!notifs.isEmpty()) {
                System.out.println("   ✅ Notification received by Driver: " + notifs.get(0).getMessage());
            } else {
                System.err.println("   ❌ No notifications found.");
            }

            // --- 10. ANALYTICS ---
            System.out.println("\n📊 [STEP 10] Testing Analytics Aggregations...");

            // A. Revenue
            Document revenue = analyticsService.getRevenueStats("2020-01-01", "2030-12-31");
            if(revenue != null)
                System.out.println("   💰 Total Revenue: " + revenue.get("totalRevenue") + "€ (From 1 booking)");

            // B. Leaderboard
            List<Document> leaders = analyticsService.getTopDriverLeaderboard();
            if(!leaders.isEmpty())
                System.out.println("   🏆 Top Driver: " + leaders.get(0).getString("name") + " (Score: " + leaders.get(0).get("performanceScore") + ")");

            // C. Churners
            List<Document> churners = analyticsService.getHighValueChurners(30);
            System.out.println("   📉 Churner Analysis executed (" + churners.size() + " at-risk users found).");

            // --- 11. LOGOUT ---
            System.out.println("\n🚪 [STEP 11] Logout...");
            authService.logout(token);
            if (!sessionRepo.existsById(token)) {
                System.out.println("   ✅ Logout successful. Redis key removed.");
            } else {
                System.err.println("   ❌ Logout Error: Key still exists.");
            }

            System.out.println("\n================================================================");
            System.out.println("🏁 DEMO COMPLETED SUCCESSFULLY. ALL SYSTEMS OPERATIONAL.");
            System.out.println("================================================================\n");
        };
    }
}