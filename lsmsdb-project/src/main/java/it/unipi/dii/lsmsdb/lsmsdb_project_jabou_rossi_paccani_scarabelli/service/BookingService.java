package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.FeedbackRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.RedisBookingRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Booking;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.BookingRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.RideRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.List;

@Service
public class    BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate; // Used for direct path probing

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking finalizeBookingFromRedis(RedisBookingRequest request) {
        // 1. ATOMIC SEAT RESERVATION FIRST
        // This uses findAndModify to prevent overbooking at the DB level
        boolean seatsSecured = reserveSeats(request.getRide_id().trim(), request.getSeats_requested());

        if (!seatsSecured) {
            throw new RuntimeException("Booking failed: Not enough seats available in the ride.");
        }

        // 2. Fetching entities for metadata
        // We use .trim() to ensure no accidental spaces from Postman/Redis cause a 404
        Ride ride = rideRepository.findById(request.getRide_id().trim())
                .orElseThrow(() -> new RuntimeException("Ride details not found: " + request.getRide_id()));

        User passengerUser = userRepository.findById(request.getUser_id().trim())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUser_id()));

        // 3. Create the Booking Document
        Booking booking = new Booking();
        booking.setId("book_" + java.util.UUID.randomUUID().toString().substring(0, 8));
        booking.setRideId(ride.getId());
        booking.setFinalPrice(ride.getBase_price());
        booking.setPaymentStatus("CONFIRMED");
        booking.setBookingDate(java.time.LocalDateTime.now().toString());

        // 4. NESTED: Driver Summary (CRITICAL FIX HERE)
        // We explicitly pull the ID from the ride's driver object
        // Inside Step 4 of finalizeBookingFromRedis
        if (ride.getDriver() != null) {
            System.out.println("DEBUG >>> Driver Object found: " + ride.getDriver());
            System.out.println("DEBUG >>> Extracted ID: " + ride.getDriver().getId());

            Booking.DriverSummary driverSum = new Booking.DriverSummary();
            driverSum.setId(ride.getDriver().getId());
            driverSum.setName(ride.getDriver().getName());
            booking.setDriver(driverSum);
        }else {
            throw new RuntimeException("ERROR: The Ride document is missing driver information.");
        }

        // 5. NESTED: Passenger Summary
        Booking.PassengerSummary passSum = new Booking.PassengerSummary();
        passSum.setId(passengerUser.getId());
        // Safe access to PersonalInfo to avoid NullPointerException
        if (passengerUser.getPersonalInfo() != null) {
            passSum.setName(passengerUser.getPersonalInfo().getName());
        } else {
            passSum.setName("Unknown Passenger");
        }
        booking.setPassenger(passSum);

        // 6. NESTED: Locations
        Booking.Locations locations = new Booking.Locations();
        if (ride.getRoute() != null) {
            locations.setPickup(ride.getRoute().getOrigin());
            locations.setDropoff(ride.getRoute().getDestination());
        }
        booking.setLocations(locations);

        // 7. Car Metadata
        if (ride.getCar() != null) {
            booking.setCarPlate(ride.getCar().getPlate());
        }

        // 8. FINAL STEP: Sync Status
        // If seats reached 0, change ride status to "FULL" or "CLOSED"
        // We'll pass the 'ride' object we already fetched to save a DB call
        syncRideStatus(ride);

        return bookingRepository.save(booking);
    }

    public List<Booking> findBookingsByPassengerId(String passengerId) {
        String cleanId = passengerId.trim();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("DEBUG: Searching for Passenger ID: [" + cleanId + "]");

        // USE THE DIRECT PROBE: Bypassing the Repository naming issues
        Query query = new Query();
        query.addCriteria(Criteria.where("passenger.id").is(cleanId)); // Exact path to image_79c69d.png field

        List<Booking> results = mongoTemplate.find(query, Booking.class);

        System.out.println("DEBUG: Results found: " + results.size());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        return results;
    }

    public Booking getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found."));
    }

    public void syncRideStatus(Ride ride) {
        if (ride.getBookingState().getAvailableSeats() <= 0) {
            ride.setStatus("FULL"); // Match the "FULL" label in your screenshots
        } else {
            ride.setStatus("AVAILABLE");
        }
        rideRepository.save(ride);
    }


    public void updatePassengerRating(String userId, int newRating) {
        User user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Get the ReviewStats object for the passenger (matching your typo 'reviews_passanger')
        User.ReviewStats stats = user.getReviewsPassenger();

        if (stats == null) {
            stats = new User.ReviewStats();
            user.setReviewsPassenger(stats);
        }

        // 2. Calculate the new average using the fields inside ReviewStats
        double currentAvg = stats.getAverageRating();
        int currentCount = stats.getCount();

        double updatedAvg = ((currentAvg * currentCount) + newRating) / (currentCount + 1);

        // 3. Set the values back into the Stats object
        stats.setAverageRating(updatedAvg);
        stats.setCount(currentCount + 1);

        // 4. Save the whole User document back to MongoDB
        userRepository.save(user);
        System.out.println("DEBUG: Updated rating for " + userId + " to " + updatedAvg);
    }

    public void addPassengerFeedback(String bookingId, FeedbackRequest request) {
        Booking booking = getBookingById(bookingId);

        // Create the Detail object
        Booking.FeedbackDetail detail = new Booking.FeedbackDetail();
        detail.setRating(request.getRating());
        detail.setComment(request.getComment());
        detail.setDate(java.time.LocalDateTime.now().toString());

        // Check if feedback object exists, if not create it
        if (booking.getFeedback() == null) {
            booking.setFeedback(new Booking.Feedback());
        }

        // Set the feedback specifically "to_passenger"
        booking.getFeedback().setToPassenger(detail);

        bookingRepository.save(booking);

        // ALSO trigger the rating update we just tested!
        updatePassengerRating(booking.getPassenger().getId(), request.getRating());
    }

    public void addDriverFeedback(String bookingId, FeedbackRequest request) {
        // 1. Validation: Ensure ratings are within 1-5 stars
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }

        Booking booking = getBookingById(bookingId);

        // 2. Safety check for Driver metadata
        if (booking.getDriver() == null || booking.getDriver().getId() == null) {
            throw new RuntimeException("ERROR: Booking document is missing Driver ID. Global ratings cannot be updated.");
        }

        // 3. Map the feedback to the Booking document
        Booking.FeedbackDetail detail = new Booking.FeedbackDetail();
        detail.setRating(request.getRating());
        detail.setComment(request.getComment());
        detail.setDate(java.time.LocalDateTime.now().toString());

        if (booking.getFeedback() == null) {
            booking.setFeedback(new Booking.Feedback());
        }
        booking.getFeedback().setToDriver(detail);

        // 4. Save the specific feedback to the booking
        bookingRepository.save(booking);

        // 5. Trigger the global rating update
        updateDriverRating(booking.getDriver().getId(), request.getRating());
    }

    private void updateDriverRating(String driverId, int newRating) {
        if (driverId == null) return;

        User driver = userRepository.findById(driverId.trim())
                .orElseThrow(() -> new RuntimeException("User not found: " + driverId));

        if (driver.getReviewsDriver() == null) {
            driver.setReviewsDriver(new User.ReviewStats());
        }

        User.ReviewStats stats = driver.getReviewsDriver();
        double currentAvg = stats.getAverageRating();
        int currentCount = stats.getCount();

        // 6. Weighted Average Calculation
        double newTotalScore = (currentAvg * currentCount) + newRating;
        int newCount = currentCount + 1;
        double rawAvg = newTotalScore / newCount;

        // 7. Rounding: Standardize to 1 decimal place (e.g., 4.7)
        double roundedAvg = Math.round(rawAvg * 10.0) / 10.0;

        stats.setAverageRating(roundedAvg);
        stats.setCount(newCount);

        // 8. Save the updated User document
        userRepository.save(driver);
    }


    public List<Document> getTopDestinations() {
        // 1. Group by the 'locations.dropoff' field
        // 2. Count how many times each one appears
        // 3. Sort by the highest count
        Aggregation agg = newAggregation(
                group("locations.dropoff").count().as("totalBookings"),
                sort(org.springframework.data.domain.Sort.Direction.DESC, "totalBookings"),
                limit(5)
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "bookings", Document.class);
        return results.getMappedResults();
    }

    public void updateRideStatus(String rideId, String newStatus) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.setStatus(newStatus);

        rideRepository.save(ride);
    }

    public Document getRevenueStats(String startDate, String endDate) {
        // 1. FILTER: Only look at bookings within this time range
        // 2. GROUP: Sum everything that passed the filter
        Aggregation agg = newAggregation(
                match(Criteria.where("bookingDate").gte(startDate).lte(endDate)),
                group().sum("finalPrice").as("totalRevenue")
                        .count().as("totalConfirmedBookings")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "bookings", Document.class);
        return results.getUniqueMappedResult();
    }
    public List<Booking> getDriverBookings(String driverId) {
        // We use trim() just in case there are extra spaces in the ID
        return bookingRepository.findByDriverId(driverId.trim());
    }

    // findBookingsByPassengerId
    public List<Booking> getPassengerBookings(String passengerId) {
        return bookingRepository.findByPassengerId(passengerId.trim());
    }

    // cancelBooking
    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Ride ride = rideRepository.findById(booking.getRideId()).orElse(null);

        if (ride != null && ride.getBookingState() != null) {
            int currentAvailable = ride.getBookingState().getAvailableSeats();
            int totalCapacity = ride.getBookingState().getTotalSeats();

            // VALIDATION: Only increment if we haven't reached the car's limit
            if (currentAvailable < totalCapacity) {
                ride.getBookingState().setAvailableSeats(currentAvailable + 1);
                rideRepository.save(ride);
                System.out.println("DEBUG: Seat returned. New count: " + (currentAvailable + 1));
            } else {
                // This prevents the 5 > 4 situation
                System.out.println("DEBUG: Warning! Seats already at max capacity. No increment performed.");
            }
        }

        bookingRepository.deleteById(bookingId);
    }

    public Document getAveragePricePerKm() {
        // We assume your 'rides' collection has a 'distance' field in km
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.project("finalPrice", "distance"),
                Aggregation.group().avg(
                        org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Divide.valueOf("finalPrice").divideBy("distance")
                ).as("avgPricePerKm")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "bookings", Document.class);
        return results.getUniqueMappedResult();
    }

    public Document getSmartPriceAnalytics(Double distance) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "_id")).limit(1);
        Document latestStats = mongoTemplate.findOne(query, Document.class, "analytics");

        if (latestStats == null) return new Document("error", "No stats found");

        Document financial = (Document) latestStats.get("financial_metrics");
        Double avgTotalTripPrice = financial.getDouble("avg_payment_per_ride"); // 35.42

        // 1. Define a realistic average distance for a carpool (e.g., 30 km)
        double assumedAvgDistance = 30.0;

        // 2. Calculate the base rate per km (35.42 / 30 = ~1.18€/km)
        double pricePerKmBase = avgTotalTripPrice / assumedAvgDistance;

        Document response = new Document();
        response.append("date_of_stat", latestStats.get("_id"));
        response.append("market_avg_trip_price", avgTotalTripPrice);
        response.append("calculated_rate_per_km", Math.round(pricePerKmBase * 100.0) / 100.0); // Rounding to 1.18

        if (distance != null && distance > 0) {
            // 3. New Fair Price: 5km * 1.18€ = 5.90€ (Much better!)
            double fairPrice = distance * pricePerKmBase;
            response.append("suggested_price_for_distance", Math.round(fairPrice * 100.0) / 100.0);
            response.append("distance_km", distance);
        }

        return response;
    }

    public boolean reserveSeats(String rideId, int seatsToReserve) {
        // Uses 'Query' and 'Criteria' imports
        Query query = new Query(Criteria.where("_id").is(rideId)
                .and("booking_state.available_seats").gte(seatsToReserve));

        // Uses 'Update' import to perform atomic decrement
        Update update = new Update().inc("booking_state.available_seats", -seatsToReserve);

        Ride result = mongoTemplate.findAndModify(
                query,
                update,
                new org.springframework.data.mongodb.core.FindAndModifyOptions().returnNew(true),
                Ride.class
        );

        return result != null;
    }

    public List<Document> getTopDriverLeaderboard() {
        // Stage 2: Weighted trust points (Keep your existing trustPointsStage here)
        AddFieldsOperation trustPointsStage = Aggregation.addFields()
                .addFieldWithValue("trustPoints",
                        ArithmeticOperators.Add.valueOf(
                                ConditionalOperators.when(Criteria.where("personalInfo.is_identity_verified").is(true))
                                        .then(50).otherwise(0)
                        ).add(
                                ArrayOperators.Reduce.arrayOf("documents")
                                        .withInitialValue(0)
                                        .reduce(ArithmeticOperators.Add.valueOf("$$value")
                                                .add(ConditionalOperators.Switch.switchCases(
                                                        ConditionalOperators.Switch.CaseOperator.when(
                                                                BooleanOperators.And.and(
                                                                        ComparisonOperators.Eq.valueOf("$$this.type").equalToValue("Driver License"),
                                                                        ComparisonOperators.Eq.valueOf("$$this.isValid").equalToValue(true)
                                                                )
                                                        ).then(30),
                                                        ConditionalOperators.Switch.CaseOperator.when(
                                                                BooleanOperators.And.and(
                                                                        ComparisonOperators.Eq.valueOf("$$this.type").equalToValue("Passport"),
                                                                        ComparisonOperators.Eq.valueOf("$$this.isValid").equalToValue(true)
                                                                )
                                                        ).then(20)
                                                ).defaultTo(5)))
                        )
                ).build();

        // Stage 3: THE FIX - Using Typed Operators instead of andExpression()
        ProjectionOperation projectStage = Aggregation.project("personalInfo.name")
                .and("reviews_driver.average_rating").as("rating")
                .and("driverInfo.avg_acceptance_rate").as("acceptanceRate")
                .and(
                        ArithmeticOperators.Multiply.valueOf("trustPoints")
                                .multiplyBy(ConditionalOperators.ifNull("reviews_driver.average_rating").then(1))
                                .multiplyBy(ConditionalOperators.ifNull("driverInfo.avg_acceptance_rate").then(0.5))
                ).as("performanceScore");

        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("driverInfo.number_of_acceptance").gt(0)),
                trustPointsStage,
                projectStage,
                sort(Sort.Direction.DESC, "performanceScore"),
                limit(10)
        );

        return mongoTemplate.aggregate(agg, "users", Document.class).getMappedResults();
    }

    public List<Document> getHighValueChurners(int daysThreshold) {
        // 1. STAGE 1: Filter for quality users AND LIMIT the candidates
        // This prevents the "forever" hang by only processing 100 users
        MatchOperation filterRated = match(Criteria.where("reviews_passanger.average_rating").gte(4.0));
        LimitOperation candidateLimit = limit(100);

        // 2. STAGE 2: Optimized Join (Uses your passenger.id_1 index)
        LookupOperation lookupBookings = LookupOperation.newLookup()
                .from("bookings")
                .localField("_id")
                .foreignField("passenger.id")
                .as("ub");

        // 3. STAGE 3: Project the fields we need
        ProjectionOperation projectFields = project("personalInfo.name")
                .and("reviews_passanger.average_rating").as("rating")
                .and(AccumulatorOperators.Sum.sumOf("ub.finalPrice")).as("totalSpent")
                .and(ArrayOperators.ArrayElemAt.arrayOf("ub.bookingDate").elementAt(0)).as("lastRide");

        // 4. STAGE 4: Assemble and Sort
        Aggregation agg = newAggregation(
                filterRated,
                candidateLimit, // The secret to stopping the hang
                lookupBookings,
                projectFields,
                sort(Sort.Direction.DESC, "totalSpent"),
                limit(10) // Only return the top 10 to Postman
        );

        return mongoTemplate.aggregate(agg, "users", Document.class).getMappedResults();
    }

}