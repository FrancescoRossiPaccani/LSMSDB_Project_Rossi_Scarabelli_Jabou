package it.unipi.dii.lsmsdb.lsmsdb_project.controller;

import it.unipi.dii.lsmsdb.lsmsdb_project.dto.LoginRequest;
import it.unipi.dii.lsmsdb.lsmsdb_project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;

    // POST http://localhost:8080/api/auth/login
    // Body: { "email": "mario.rossi@example.com", "password": "..." }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            return ResponseEntity.ok(token); // Ritorna il token di sessione (UUID)
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Login fallito: " + e.getMessage());
        }
    }

    // POST http://localhost:8080/api/auth/logout?token=...
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logout effettuato con successo (Sessione Redis rimossa)");
    }
}