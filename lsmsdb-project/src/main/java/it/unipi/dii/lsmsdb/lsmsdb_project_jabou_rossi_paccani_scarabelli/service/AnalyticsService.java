package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Analytics;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    public List<Analytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }

    public Optional<Analytics> getAnalyticsByDate(String date) {
        return analyticsRepository.findById(date);
    }

    public List<Analytics> getAnalyticsByRange(String startDate, String endDate) {
        return analyticsRepository.findByDateBetween(startDate, endDate);
    }
}