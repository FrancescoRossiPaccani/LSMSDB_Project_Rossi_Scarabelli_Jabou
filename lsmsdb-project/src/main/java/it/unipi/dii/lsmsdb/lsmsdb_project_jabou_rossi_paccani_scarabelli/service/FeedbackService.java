package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.FeedbackDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Booking;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Review;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.BookingRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.ReviewRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public FeedbackService(ReviewRepository reviewRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public void submitDriverFeedback(String bookingId, FeedbackDTO feedbackDTO) {
        // 1. Trova prenotazione
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Nota: i campi in booking.getDriver() sono UserSummary, quindi usiamo getId()
        String driverId = booking.getDriver().getId();
        String passengerId = booking.getPassenger().getId();

        // 2. Salva Review
        Review review = new Review();
        review.setRideId(booking.getRideId());
        review.setAuthorId(passengerId);
        review.setTargetUserId(driverId);
        review.setRating(feedbackDTO.getRating());
        review.setComment(feedbackDTO.getComment());
        review.setDate(LocalDateTime.now());
        review.setRole("DRIVER_REVIEW");
        reviewRepository.save(review);

        // 3. Aggiorna media voti Driver
        updateUserRating(driverId, true);

        // 4. Aggiorna lo stato nel booking
        if (booking.getFeedback() == null) {
            booking.setFeedback(new Booking.FeedbackSummary());
        }
        booking.getFeedback().setToDriver(review.getId());
        bookingRepository.save(booking);
    }

    private void updateUserRating(String userId, boolean isDriver) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Review> reviews = reviewRepository.findByTargetUserId(userId);

        // Calcola nuova media
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        int count = reviews.size();

        if (isDriver) {
            if (user.getReviewsDriver() == null) {
                user.setReviewsDriver(new User.ReviewStats());
            }
            user.getReviewsDriver().setAverageRating(avg);
            user.getReviewsDriver().setCount(count);
        } else {
            if (user.getReviewsPassenger() == null) {
                user.setReviewsPassenger(new User.ReviewStats());
            }
            user.getReviewsPassenger().setAverageRating(avg);
            user.getReviewsPassenger().setCount(count);
        }
        userRepository.save(user);
    }
}