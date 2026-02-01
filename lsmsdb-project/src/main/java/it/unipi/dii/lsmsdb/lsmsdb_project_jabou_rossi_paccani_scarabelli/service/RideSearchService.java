package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Ride;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.RideRepository;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RideSearchService {

    private final RouteRepository routeRepository;
    private final RideRepository rideRepository;

    public RideSearchService(RouteRepository routeRepository, RideRepository rideRepository) {
        this.routeRepository = routeRepository;
        this.rideRepository = rideRepository;
    }

    public List<Ride> searchRides(String fromCity, String toCity) {
        // 1. Neo4j restituisce gli ID delle corse (Stringhe)
        List<String> rideIds = routeRepository.findRideIdsBetweenLocations(fromCity, toCity);

        if (rideIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Recupera i dettagli da Mongo
        List<Ride> availableRides = new ArrayList<>();
        Iterable<Ride> ridesFromMongo = rideRepository.findAllById(rideIds);

        for (Ride r : ridesFromMongo) {
            // Filtra: Status OPEN e Posti disponibili > 0
            // Accesso annidato sicuro a bookingState
            if ("OPEN".equals(r.getStatus()) &&
                    r.getBookingState() != null &&
                    r.getBookingState().getAvailableSeats() > 0) {
                availableRides.add(r);
            }
        }

        return availableRides;
    }

    @Transactional
    public Ride publishRide(Ride ride) {
        ride.setStatus("OPEN");
        // Assicura che lo stato prenotazione sia inizializzato
        if (ride.getBookingState() == null) {
            Ride.BookingState state = new Ride.BookingState();
            state.setTotalSeats(4); // Default o preso da input
            state.setAvailableSeats(4);
            state.setHasWaitingList(false);
            ride.setBookingState(state);
        }
        return rideRepository.save(ride);
    }
}