package it.unipi.dii.lsmsdb.lsmsdb_project.controller;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired private RideService rideService;

    // 1. CREAZIONE CORSA (MONGO + NEO4J)
    // POST http://localhost:8080/api/rides
    @PostMapping
    public ResponseEntity<Ride> createRide(@RequestBody Ride ride) {
        Ride newRide = rideService.createRide(ride);
        return ResponseEntity.ok(newRide);
    }

    // 2. RICERCA GEOSPAZIALE (Il "Radius Search")
    // GET http://localhost:8080/api/rides/search?latA=43.7&lonA=10.4&latB=45.4&lonB=9.1
    @GetMapping("/search")
    public List<Ride> searchRides(@RequestParam Double latA, @RequestParam Double lonA,
                                  @RequestParam Double latB, @RequestParam Double lonB) {
        return rideService.searchMatchingRides(latA, lonA, latB, lonB);
    }

    // GET http://localhost:8080/api/rides
    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }
}