package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.controller;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service.RideService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RideService rideService;

    public RouteController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/match")
    public List<Ride> matchRides(@RequestParam Double latA, @RequestParam Double lonA,
                                 @RequestParam Double latB, @RequestParam Double lonB) {
        return rideService.searchMatchingRides(latA, lonA, latB, lonB);
    }
}