package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.AnalyticsDaily;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<AnalyticsDaily> getRevenueStats(String start, String end) {
        return analyticsRepository.findReportsBetweenDates(start, end);
    }

    public Map<String, Object> getPriceStats(int distanceKm) {
        double marketAvgPrice = 35.42;
        double ratePerKm = 1.18;
        double suggested = ratePerKm * distanceKm;

        Map<String, Object> response = new HashMap<>();
        response.put("date_of_stat", java.time.LocalDate.now().toString());
        response.put("market_avg_trip_price", marketAvgPrice);
        response.put("calculated_rate_per_km", ratePerKm);
        response.put("suggested_price_for_distance", suggested);
        response.put("distance_km", distanceKm);

        return response;
    }
}