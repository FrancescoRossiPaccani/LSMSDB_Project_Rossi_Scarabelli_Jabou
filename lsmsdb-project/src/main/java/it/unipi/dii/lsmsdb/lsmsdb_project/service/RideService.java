package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.RideRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // --- CRITICO: CREAZIONE SINCRONIZZATA (MONGO + NEO4J) ---
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

            // Prendiamo le coordinate (con fallback a 0.0 se null per evitare crash)
            Double fromLat = savedRide.getRoute().getOriginLat() != null ? savedRide.getRoute().getOriginLat() : 0.0;
            Double fromLon = savedRide.getRoute().getOriginLon() != null ? savedRide.getRoute().getOriginLon() : 0.0;
            Double toLat = savedRide.getRoute().getDestLat() != null ? savedRide.getRoute().getDestLat() : 0.0;
            Double toLon = savedRide.getRoute().getDestLon() != null ? savedRide.getRoute().getDestLon() : 0.0;

            try {
                // Passiamo TUTTI i parametri
                routeRepository.createRideRelationship(
                        from, fromLat, fromLon,
                        to, toLat, toLon,
                        savedRide.getId(), savedRide.getBasePrice()
                );
                System.out.println("DEBUG: Graph relation AND nodes created/updated for ride " + savedRide.getId());
            } catch (Exception e) {
                System.err.println("WARNING: Could not update Neo4j graph: " + e.getMessage());
            }
        }
        return savedRide;
    }

    // --- LOGICA DEL COLLEGA: Matching Geospaziale ---
    public List<Ride> searchMatchingRides(Double latA, Double lonA, Double latB, Double lonB) {
        // 1. Trova nodi vicini (Walk logic) su Neo4j (raggio 2km)
        List<RouteRepository.NearbyLocationProjection> starts = routeRepository.findNearbyNodes(latA, lonA, 2000.0);
        List<RouteRepository.NearbyLocationProjection> ends = routeRepository.findNearbyNodes(latB, lonB, 2000.0);

        List<String> startNames = new ArrayList<>();
        if (starts != null) starts.forEach(s -> startNames.add(s.getName()));

        List<String> endNames = new ArrayList<>();
        if (ends != null) ends.forEach(e -> endNames.add(e.getName()));

        if (startNames.isEmpty() || endNames.isEmpty()) return new ArrayList<>();

        // 2. Cerca su MongoDB usando i nomi trovati
        return rideRepository.findByRouteOriginInAndRouteDestinationIn(startNames, endNames)
                .stream()
                .filter(r -> r.getBookingState().getAvailableSeats() > 0)
                .sorted(Comparator.comparing(Ride::getBasePrice))
                .collect(Collectors.toList());
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }
}