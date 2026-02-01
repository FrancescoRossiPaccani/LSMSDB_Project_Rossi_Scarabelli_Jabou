package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.LocationRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final LocationRepository locationRepo;

    public RideService(RideRepository rideRepository, LocationRepository locationRepo) {
        this.rideRepository = rideRepository;
        this.locationRepo = locationRepo;
    }

    /**
     * THE MASTER ENGINE: Matches GPS to Neo4j Radius, then searches MongoDB.
     */
    public List<Ride> searchMatchingRides(Double latA, Double lonA, Double latB, Double lonB) {
        // 1. Find nearby stops in Neo4j (The Walking Logic)
        List<LocationRepository.NearbyLocationProjection> starts = locationRepo.findNearbyNodes(latA, lonA, 1200.0);
        List<LocationRepository.NearbyLocationProjection> ends = locationRepo.findNearbyNodes(latB, lonB, 1200.0);

        List<String> startNames = new ArrayList<>();
        if (starts != null) {
            for (var s : starts) {
                if (s.getName() != null) {
                    startNames.add(s.getName());
                    startNames.add(s.getName().toLowerCase());
                }
            }
        }

        List<String> endNames = new ArrayList<>();
        if (ends != null) {
            for (var e : ends) {
                if (e.getName() != null) {
                    endNames.add(e.getName());
                    endNames.add(e.getName().toLowerCase());
                }
            }
        }

        if (startNames.isEmpty() || endNames.isEmpty()) return List.of();

        // 2. Query MongoDB, filter for seats, sort by cheapest, and limit to 10
        return rideRepository.findByRouteOriginInAndRouteDestinationIn(startNames, endNames)
                .stream()
                .filter(ride -> ride.getBookingState() != null &&
                        ride.getBookingState().getAvailableSeats() != null &&
                        ride.getBookingState().getAvailableSeats() > 0)
                .sorted(Comparator.comparing(Ride::getBase_price))
                .limit(10)
                .collect(Collectors.toList());
    }

    // --- STANDARD CRUD METHODS (To fix your errors) ---

    public Ride createRide(Ride ride) {
        return rideRepository.save(ride);
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public List<Ride> searchRides(String from, String to) {
        return rideRepository.findByRouteOriginIgnoreCaseAndRouteDestinationIgnoreCase(from, to);
    }
}