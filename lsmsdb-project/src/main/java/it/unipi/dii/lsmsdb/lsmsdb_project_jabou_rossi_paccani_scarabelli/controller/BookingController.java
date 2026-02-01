package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.controller;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.FeedbackRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Booking;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service.BookingService;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.RedisBookingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.bson.Document;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // This endpoint receives the "Accept" signal from your Key-Value DB logic
    @PostMapping("/finalize")
    public Booking finalizeBooking(@RequestBody RedisBookingRequest request) {
        return bookingService.finalizeBookingFromRedis(request);
    }

    // URL: GET http://localhost:8080/api/bookings/book_0f30a661
    @GetMapping("/{id}")
    public Booking getBooking(@PathVariable String id) {
        return bookingService.getBookingById(id);
    }

    // URL: GET http://localhost:8080/api/bookings/user/user_006
    @GetMapping("/user/{passengerId}")
    public List<Booking> getUserHistory(@PathVariable String passengerId) {
        return bookingService.findBookingsByPassengerId(passengerId);
    }

    // URL: DELETE http://localhost:8080/api/bookings/book_0f30a661
    @DeleteMapping("/{id}")
    public String cancel(@PathVariable String id) {
        bookingService.cancelBooking(id);
        return "Booking " + id + " cancelled successfully!";
    }

    // URL: PUT http://localhost:8080/api/bookings/user/user_4378/rate?stars=5
    @PutMapping("/user/{userId}/rate")
    public String updateRating(@PathVariable String userId, @RequestParam int stars) {
        bookingService.updatePassengerRating(userId, stars);
        return "Rating updated for user " + userId;
    }

    // URL: POST http://localhost:8080/api/bookings/book_9a595cda/feedback
    @PostMapping("/{bookingId}/feedback")
    public String addFeedback(@PathVariable String bookingId, @RequestBody FeedbackRequest request) {
        bookingService.addPassengerFeedback(bookingId, request);
        return "Feedback added and User rating updated!";
    }

    // 1. URL: POST http://localhost:8080/api/bookings/book_9a595cda/feedback/driver
    @PostMapping("/{bookingId}/feedback/driver")
    public String addDriverFeedback(@PathVariable String bookingId, @RequestBody FeedbackRequest request) {
        bookingService.addDriverFeedback(bookingId, request);
        return "Driver feedback saved and Driver rating updated!";
    }

    // URL: GET http://localhost:8080/api/bookings/analytics/revenue?start=2026-01-01T00:00:00&end=2026-01-31T23:59:59
    @GetMapping("/analytics/revenue")
    public Document getRevenue(@RequestParam String start, @RequestParam String end) {
        return bookingService.getRevenueStats(start, end);
    }

    // URL: GET http://localhost:8080/api/bookings/driver/user_001
    @GetMapping("/driver/{driverId}")
    public List<Booking> getDriverBookings(@PathVariable String driverId) {
        return bookingService.getDriverBookings(driverId);
    }

    // URL: GET http://localhost:8080/api/bookings/passenger/user_4378
    @GetMapping("/passenger/{passengerId}")
    public List<Booking> getPassengerBookings(@PathVariable String passengerId) {
        return bookingService.getPassengerBookings(passengerId);
    }


    // URL: GET http://localhost:8080/api/bookings/analytics/price-stats?dist=10.0
    @GetMapping("/analytics/price-stats")
    public Document getPriceStats(@RequestParam(required = false) Double dist) {
        return bookingService.getSmartPriceAnalytics(dist);
    }

    @GetMapping("/leaderboard/top-drivers")
    public ResponseEntity<List<Document>> getTopDrivers() {
        // Calling the "Hard" aggregation you just tested in CMD
        List<Document> drivers = bookingService.getTopDriverLeaderboard();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/analytics/high-value-churners")
    public ResponseEntity<List<Document>> getChurners(@RequestParam(defaultValue = "0") int days) {
        // Calling the method you just put in the Service
        List<Document> churners = bookingService.getHighValueChurners(days);
        return ResponseEntity.ok(churners);
    }
}

