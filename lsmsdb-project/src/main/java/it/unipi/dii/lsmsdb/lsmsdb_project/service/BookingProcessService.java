package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import com.mongodb.WriteConcern;
import it.unipi.dii.lsmsdb.lsmsdb_project.dto.BookingRequestDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.*;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingProcessService {

    @Autowired private MongoTemplate mongoTemplate;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private RideRepository rideRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private BookingRequestRepository redisRepo;
    @Autowired private NotificationRepository notificationRepo;
    @Autowired private RedisTemplate<String, Object> redisTemplate; // Required for CAP WAIT command

    public String createTemporaryReservation(BookingRequestDTO dto) {
        BookingRequest request = new BookingRequest();
        String tempId = "req_" + UUID.randomUUID().toString().substring(0, 8);
        request.setId(tempId);
        request.setRideId(dto.getRideId());
        request.setPassengerId(dto.getUserId());
        request.setSeatsRequested(dto.getSeatsRequested());

        // 1. Save normally to Redis
        redisRepo.save(request);

        // 2. CP STRATEGY: Use a more robust execution method
        try {
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                // Use 'execute' with the name of the command and the specific arguments
                // We use 'null' for the output type to let Spring handle the conversion
                return connection.execute("WAIT", "1".getBytes(), "1000".getBytes());
            });
        } catch (Exception e) {
            // Log the warning but don't crash the demo if replication is just slow
            System.err.println("CAP WARNING: Redis WAIT sync failed or timed out: " + e.getMessage());
        }

        return tempId;
    }

    // FASE 2: MONGO FINALIZATION (CAP Enhancements for Atomic Consistency)
    @Transactional
    public Booking finalizeBooking(String redisRequestId) {
        // 1. Recupera da Redis
        BookingRequest req = redisRepo.findById(redisRequestId)
                .orElseThrow(() -> new RuntimeException("Request expired or not found"));

        // 2. ATOMIC DECREMENT (Previene overbooking con WriteConcern.MAJORITY)
        boolean seatsSecured = reserveSeatsAtomic(req.getRideId(), req.getSeatsRequested());
        if (!seatsSecured) throw new RuntimeException("No seats available for this ride");

        // 3. Recupera Entità
        Ride ride = rideRepo.findById(req.getRideId()).orElseThrow();
        User passenger = userRepo.findById(req.getPassengerId()).orElseThrow();

        // 4. Crea Booking Document
        Booking booking = new Booking();
        booking.setId("book_" + UUID.randomUUID().toString().substring(0, 8));
        booking.setRideId(ride.getId());
        booking.setBookingDate(LocalDateTime.now().toString());
        booking.setFinalPrice(ride.getBasePrice() * req.getSeatsRequested());
        booking.setPaymentStatus("CONFIRMED");

        // Popola Nested Objects
        Booking.PassengerSummary pSum = new Booking.PassengerSummary();
        pSum.setId(passenger.getId());
        pSum.setName(passenger.getPersonalInfo().getName());
        booking.setPassenger(pSum);

        if (ride.getDriver() != null) {
            Booking.DriverSummary dSum = new Booking.DriverSummary();
            dSum.setId(ride.getDriver().getId());
            dSum.setName(ride.getDriver().getName());
            booking.setDriver(dSum);
        }

        // 5. Salva Booking
        Booking saved = bookingRepo.save(booking);

        // 6. Pulisce Redis
        redisRepo.deleteById(redisRequestId);

        // 7. Genera NOTIFICA su Redis
        if (ride.getDriver() != null) {
            Notification notif = new Notification(
                    ride.getDriver().getId(),
                    "NEW_BOOKING",
                    "Nuova prenotazione da " + passenger.getPersonalInfo().getName()
            );
            notificationRepo.save(notif);
        }

        return saved;
    }

    // Helper per aggiornamento atomico con CAP Consistency
    private boolean reserveSeatsAtomic(String rideId, int seats) {
        // CP STRATEGY: Enforce Majority Write Concern
        // Ensures the update is acknowledged by the majority of the 3-VM cluster
        mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);

        Query query = new Query(Criteria.where("_id").is(rideId)
                .and("booking_state.available_seats").gte(seats));
        Update update = new Update().inc("booking_state.available_seats", -seats);

        Ride result = mongoTemplate.findAndModify(query, update, Ride.class);
        return result != null;
    }
}