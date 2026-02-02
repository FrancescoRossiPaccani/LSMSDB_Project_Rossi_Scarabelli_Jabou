package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import com.mongodb.ReadPreference;
import it.unipi.dii.lsmsdb.lsmsdb_project.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.RideRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideService {

    @Autowired private RideRepository rideRepository;
    @Autowired private RouteRepository routeRepository; // Neo4j
    @Autowired private MongoTemplate mongoTemplate; // Required for CAP ReadPreferences

    // --- CRITICO: CREAZIONE (CP - Consistency) ---
    @Transactional
    public Ride createRide(Ride ride) {
        // 1. Completa i dati mancanti
        ride.setStatus("OPEN");
        if (ride.getMetadata() == null) {
            Ride.Metadata meta = new Ride.Metadata();
            meta.setCreatedAt(LocalDateTime.now().toString());
            ride.setMetadata(meta);
        }

        // 2. Salva su MongoDB (Master Data)
        Ride savedRide = rideRepository.save(ride);

        // 3. Aggiorna Neo4j (Graph Data)
        if (savedRide.getRoute() != null) {
            String from = savedRide.getRoute().getOrigin();
            String to = savedRide.getRoute().getDestination();

            Double fromLat = savedRide.getRoute().getOriginLat() != null ? savedRide.getRoute().getOriginLat() : 0.0;
            Double fromLon = savedRide.getRoute().getOriginLon() != null ? savedRide.getRoute().getOriginLon() : 0.0;
            Double toLat = savedRide.getRoute().getDestLat() != null ? savedRide.getRoute().getDestLat() : 0.0;
            Double toLon = savedRide.getRoute().getDestLon() != null ? savedRide.getRoute().getDestLon() : 0.0;

            try {
                routeRepository.createRideRelationship(
                        from, fromLat, fromLon,
                        to, toLat, toLon,
                        savedRide.getId(), savedRide.getBasePrice()
                );
                System.out.println("DEBUG: Graph relation created for ride " + savedRide.getId());
            } catch (Exception e) {
                System.err.println("WARNING: Could not update Neo4j: " + e.getMessage());
            }
        }
        return savedRide;
    }

    // --- LOGICA AP (High Availability) ---
    public List<Ride> searchMatchingRides(Double latA, Double lonA, Double latB, Double lonB) {
        List<String> startNames = new ArrayList<>();
        List<String> endNames = new ArrayList<>();

        try {
            // 1. PRIMARY PATH: Neo4j proximity search
            List<RouteRepository.NearbyLocationProjection> starts = routeRepository.findNearbyNodes(latA, lonA, 2000.0);
            List<RouteRepository.NearbyLocationProjection> ends = routeRepository.findNearbyNodes(latB, lonB, 2000.0);

            if (starts != null) startNames = starts.stream().map(s -> s.getName()).collect(Collectors.toList());
            if (ends != null) endNames = ends.stream().map(e -> e.getName()).collect(Collectors.toList());

        } catch (Exception e) {
            // 2. FALLBACK PATH: If Neo4j is down, we don't crash.
            // We log the error and allow the system to remain AVAILABLE.
            System.err.println("NEO4J ERROR: Proximity search failed. System remaining AP via Mongo Fallback.");
        }

        // 3. MONGO STRATEGY:
        // If Neo4j worked, we search by specific nearby names.
        // If Neo4j failed (lists are empty), we search for ALL available rides to keep the app alive.
        Query query;
        if (startNames.isEmpty() || endNames.isEmpty()) {
            // Fallback: Just find all open rides so the user sees *something*
            query = new Query(Criteria.where("status").is("OPEN")
                    .and("booking_state.available_seats").gt(0));
        } else {
            // Normal proximity result
            query = new Query(Criteria.where("route.origin").in(startNames)
                    .and("route.destination").in(endNames)
                    .and("booking_state.available_seats").gt(0));
        }

        // Set read preference to secondary for High Availability
        mongoTemplate.setReadPreference(ReadPreference.secondaryPreferred());

        return mongoTemplate.find(query, Ride.class)
                .stream()
                .sorted(Comparator.comparing(Ride::getBasePrice))
                .collect(Collectors.toList());
    }

    public List<Ride> getAllRides() {
        // Use secondaryPreferred to offload the Primary node during listing
        Query query = new Query();
        mongoTemplate.setReadPreference(ReadPreference.secondaryPreferred());
        return mongoTemplate.find(query, Ride.class);
    }
}