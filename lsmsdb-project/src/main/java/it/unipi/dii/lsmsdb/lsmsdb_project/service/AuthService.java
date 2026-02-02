package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unipi.dii.lsmsdb.lsmsdb_project.dto.LoginRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.Session;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.SessionRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.UserRepository;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private SessionRepository sessionRepository;

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!request.getPassword().equals("password123")) {
            // throw new RuntimeException("Wrong password");
        }

        String token = UUID.randomUUID().toString();
        Session session = new Session(token, user.getId(), user.getPersonalInfo().getName());
        sessionRepository.save(session);

        return token; // return
    }

    public void logout(String token) {
        sessionRepository.deleteById(token);
    }
}