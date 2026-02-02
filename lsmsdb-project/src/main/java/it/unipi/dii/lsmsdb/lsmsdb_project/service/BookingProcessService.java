package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import it.unipi.dii.lsmsdb.lsmsdb_project.dto.BookingRequestDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.*;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    // FASE 1: REDIS (Il tuo codice - Fast Write)
    public String createTemporaryReservation(BookingRequestDTO dto) {
        BookingRequest request = new BookingRequest();
        String tempId = "req_" + UUID.randomUUID().toString().substring(0, 8);
        request.setId(tempId);
        request.setRideId(dto.getRideId());
        request.setPassengerId(dto.getUserId());
        request.setSeatsRequested(dto.getSeatsRequested());
        redisRepo.save(request);
        return tempId;
    }

    // FASE 2: MONGO FINALIZATION (Codice del collega - Atomic Update)
    @Transactional
    public Booking finalizeBooking(String redisRequestId) {
        // 1. Recupera da Redis
        BookingRequest req = redisRepo.findById(redisRequestId)
                .orElseThrow(() -> new RuntimeException("Request expired or not found"));

        // 2. ATOMIC DECREMENT (Previene overbooking)
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

        // 7. Genera NOTIFICA su Redis (Il pezzo mancante)
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

    // Helper per aggiornamento atomico
    private boolean reserveSeatsAtomic(String rideId, int seats) {
        Query query = new Query(Criteria.where("_id").is(rideId)
                .and("booking_state.available_seats").gte(seats));
        Update update = new Update().inc("booking_state.available_seats", -seats);
        Ride result = mongoTemplate.findAndModify(query, update, Ride.class);
        return result != null;
    }
}