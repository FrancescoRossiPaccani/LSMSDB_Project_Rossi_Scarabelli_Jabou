package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.dto.UserSummaryDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public UserSummaryDTO getPublicProfile(String userId) {
        // 1. Fetch the full User document from MongoDB
        User user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 2. Map the heavy document to our slim, safe DTO
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setName(user.getPersonalInfo().getName());

        // 3. Safety check: Only add ratings if they exist
        if (user.getReviewsDriver() != null) {
            dto.setAverageRating(user.getReviewsDriver().getAverageRating());
            dto.setTotalReviews(user.getReviewsDriver().getCount());
        }

        // 4. Use 'location' as the city for the summary
        dto.setCity(user.getPersonalInfo().getLocation());

        return dto;
    }
}