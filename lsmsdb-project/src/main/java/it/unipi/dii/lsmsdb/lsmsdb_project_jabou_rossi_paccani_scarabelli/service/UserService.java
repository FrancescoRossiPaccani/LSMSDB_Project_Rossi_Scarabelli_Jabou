package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Car;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Transactional
    public User registerUser(User user) {
        if (user.getStatus() == null) user.setStatus("ACTIVE");
        return userRepository.save(user);
    }

    /**
     * Aggiunge il sommario di un'auto al profilo del driver.
     * Nota: Assumiamo che l'oggetto Car sia già stato salvato nella sua collezione "cars"
     * e qui stiamo aggiornando il documento "users".
     */
    @Transactional
    public void addCarToDriver(String userId, Car car) {
        User user = getUserById(userId);

        // Inizializza DriverInfo se non esiste
        if (user.getDriverInfo() == null) {
            user.setDriverInfo(new User.DriverInfo());
            user.getDriverInfo().setCars(new ArrayList<>());
        }

        // Creiamo il sommario dell'auto (User.CarSummary) da salvare dentro l'utente
        User.CarSummary summary = new User.CarSummary();
        summary.setCarId(car.getId());

        // Accesso sicuro ai dettagli dell'auto
        if (car.getDetails() != null) {
            summary.setModel(car.getDetails().getModel());
        }

        user.getDriverInfo().getCars().add(summary);
        userRepository.save(user);
    }
}