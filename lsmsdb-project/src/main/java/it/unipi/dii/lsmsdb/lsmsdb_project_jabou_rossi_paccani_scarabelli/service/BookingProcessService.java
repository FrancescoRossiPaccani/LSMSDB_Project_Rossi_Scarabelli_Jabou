package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.BookingRequestDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.BookingResponseDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.LocationDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.UserSummaryDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Booking;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.BookingRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.BookingRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.BookingRequestRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.RideRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingProcessService {

    private final BookingRequestRepository redisRepo;
    private final BookingRepository bookingRepo;
    private final RideRepository rideRepo;
    private final UserRepository userRepo;

    public BookingProcessService(BookingRequestRepository redisRepo, BookingRepository bookingRepo, RideRepository rideRepo, UserRepository userRepo) {
        this.redisRepo = redisRepo;
        this.bookingRepo = bookingRepo;
        this.rideRepo = rideRepo;
        this.userRepo = userRepo;
    }

    // FASE 1: Redis
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

    // FASE 2: Mongo Finalize
    @Transactional
    public BookingResponseDTO finalizeBooking(BookingRequestDTO requestData) {

        // 1. Recupera la Ride e check posti
        Ride ride = rideRepo.findById(requestData.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getBookingState().getAvailableSeats() < requestData.getSeatsRequested()) {
            throw new RuntimeException("Not enough seats available!");
        }

        // 2. Decrementa i posti
        ride.getBookingState().setAvailableSeats(
                ride.getBookingState().getAvailableSeats() - requestData.getSeatsRequested()
        );
        rideRepo.save(ride);

        // 3. Recupera Utenti
        User passenger = userRepo.findById(requestData.getUserId()).orElseThrow();
        // Attenzione: ride.getDriver() qui è un DriverSummary, prendiamo l'ID per cercare l'User completo
        User driver = userRepo.findById(ride.getDriver().getId()).orElseThrow();

        // 4. Crea l'oggetto Booking Persistente
        Booking booking = new Booking();
        booking.setId("book_" + UUID.randomUUID().toString().substring(0, 8));
        booking.setBookingDate(LocalDateTime.now().toString());
        booking.setRideId(ride.getId());

        // Accesso annidato per la targa
        if (ride.getCar() != null) {
            booking.setCarPlate(ride.getCar().getPlate());
        }

        booking.setFinalPrice(ride.getBasePrice() * requestData.getSeatsRequested());
        booking.setPaymentStatus("CONFIRMED");

        // Setta i sommari (Inner Classes di Booking)
        Booking.UserSummary passSum = new Booking.UserSummary();
        passSum.setId(passenger.getId());
        if (passenger.getPersonalInfo() != null) {
            passSum.setName(passenger.getPersonalInfo().getName());
        }
        booking.setPassenger(passSum);

        Booking.UserSummary driverSum = new Booking.UserSummary();
        driverSum.setId(driver.getId());
        if (driver.getPersonalInfo() != null) {
            driverSum.setName(driver.getPersonalInfo().getName());
        }
        booking.setDriver(driverSum);

        Booking.LocationSummary locSum = new Booking.LocationSummary();
        locSum.setPickup(requestData.getPickup());
        locSum.setDropoff(requestData.getDropoff());
        booking.setLocations(locSum);

        Booking savedBooking = bookingRepo.save(booking);

        return convertToDTO(savedBooking, passenger, driver);
    }

    private BookingResponseDTO convertToDTO(Booking b, User p, User d) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(b.getId());
        dto.setBookingDate(LocalDateTime.parse(b.getBookingDate()));
        dto.setFinalPrice(b.getFinalPrice());
        dto.setPaymentStatus(b.getPaymentStatus());
        dto.setRideId(b.getRideId());
        dto.setCarPlate(b.getCarPlate());

        // Driver DTO Mapping
        UserSummaryDTO driverDto = new UserSummaryDTO();
        driverDto.setId(d.getId());
        if (d.getPersonalInfo() != null) {
            driverDto.setName(d.getPersonalInfo().getName() + " " + d.getPersonalInfo().getSurname());
        }
        // Null check per i rating
        if (d.getReviewsDriver() != null) {
            driverDto.setRating(d.getReviewsDriver().getAverageRating());
        } else {
            driverDto.setRating(0.0);
        }
        driverDto.setProfilePicture("url_placeholder");
        dto.setDriver(driverDto);

        // Passenger DTO Mapping
        UserSummaryDTO passDto = new UserSummaryDTO();
        passDto.setId(p.getId());
        if (p.getPersonalInfo() != null) {
            passDto.setName(p.getPersonalInfo().getName());
        }
        if (p.getReviewsPassenger() != null) {
            passDto.setRating(p.getReviewsPassenger().getAverageRating());
        } else {
            passDto.setRating(0.0);
        }
        dto.setPassenger(passDto);

        // Locations
        if (b.getLocations() != null) {
            LocationDTO locDto = new LocationDTO();
            locDto.setPickup(b.getLocations().getPickup());
            locDto.setDropoff(b.getLocations().getDropoff());
            dto.setLocations(locDto);
        }

        return dto;
    }
}