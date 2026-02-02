package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteConcern;

import it.unipi.dii.lsmsdb.lsmsdb_project.dto.BookingRequestDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.Booking;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.BookingRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.Notification;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.BookingRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.BookingRequestRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.NotificationRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.RideRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.UserRepository;

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

        redisRepo.save(request);

        try {
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                return connection.execute("WAIT", "1".getBytes(), "1000".getBytes());
            });
        } catch (Exception e) {
            System.err.println("CAP WARNING: Redis WAIT sync failed or timed out: " + e.getMessage());
        }

        return tempId;
    }

    // Mongo finalizzatiion
    @Transactional
    public Booking finalizeBooking(String redisRequestId) {
        //get datat from redis
        BookingRequest req = redisRepo.findById(redisRequestId)
                .orElseThrow(() -> new RuntimeException("Request expired or not found"));

        //atomic decrement for evitate overbooking
        boolean seatsSecured = reserveSeatsAtomic(req.getRideId(), req.getSeatsRequested());
        if (!seatsSecured) throw new RuntimeException("No seats available for this ride");

        Ride ride = rideRepo.findById(req.getRideId()).orElseThrow();
        User passenger = userRepo.findById(req.getPassengerId()).orElseThrow();

        Booking booking = new Booking();
        booking.setId("book_" + UUID.randomUUID().toString().substring(0, 8));
        booking.setRideId(ride.getId());
        booking.setBookingDate(LocalDateTime.now().toString());
        booking.setFinalPrice(ride.getBasePrice() * req.getSeatsRequested());
        booking.setPaymentStatus("CONFIRMED");

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

        Booking saved = bookingRepo.save(booking);
        redisRepo.deleteById(redisRequestId);

        //notify
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

    // Helper for atomic upadate
    private boolean reserveSeatsAtomic(String rideId, int seats) {
        // Ensures the update is acknowledged by the majority of the 3-VM
        mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);

        Query query = new Query(Criteria.where("_id").is(rideId)
                .and("booking_state.available_seats").gte(seats));
        Update update = new Update().inc("booking_state.available_seats", -seats);

        Ride result = mongoTemplate.findAndModify(query, update, Ride.class);
        return result != null;
    }
}