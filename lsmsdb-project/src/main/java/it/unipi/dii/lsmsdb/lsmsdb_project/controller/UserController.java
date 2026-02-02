package it.unipi.dii.lsmsdb.lsmsdb_project.controller;

import it.unipi.dii.lsmsdb.lsmsdb_project.dto.UserSummaryDTO;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.User;
import it.unipi.dii.lsmsdb.lsmsdb_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUserById(id);
    }

    // Endpoint ottimizzato per la visualizzazione pubblica (DTO)
    @GetMapping("/{id}/summary")
    public ResponseEntity<UserSummaryDTO> getUserSummary(@PathVariable String id) {
        return ResponseEntity.ok(userService.getPublicProfile(id));
    }
}