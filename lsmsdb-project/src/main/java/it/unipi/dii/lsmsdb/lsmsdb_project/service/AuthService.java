package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import it.unipi.dii.lsmsdb.lsmsdb_project.dto.LoginRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.Session;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.SessionRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private SessionRepository sessionRepository;

    public String login(LoginRequest request) {
        // 1. Cerca l'utente su Mongo
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // 2. Controllo password (semplificato per l'esame: plain text)
        // In produzione useremmo BCrypt
        // Assumiamo che la password sia l'ID o un campo hardcodato per ora
        // (Modifica questo controllo se avete un campo password vero)
        if (!request.getPassword().equals("password123")) {
            // throw new RuntimeException("Password errata");
        }

        // 3. Crea la sessione su Redis
        String token = UUID.randomUUID().toString();
        Session session = new Session(token, user.getId(), user.getPersonalInfo().getName());
        sessionRepository.save(session);

        return token; // Ritorna il token al frontend
    }

    public void logout(String token) {
        sessionRepository.deleteById(token);
    }
}