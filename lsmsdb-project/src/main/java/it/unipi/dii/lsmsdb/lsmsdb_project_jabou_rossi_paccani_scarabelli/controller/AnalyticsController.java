package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.controller;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Analytics;
import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public List<Analytics> getAllAnalytics() {
        return analyticsService.getAllAnalytics();
    }

    @GetMapping("/{date}")
    public Analytics getAnalyticsByDate(@PathVariable String date) {
        return analyticsService.getAnalyticsByDate(date).orElse(null);
    }

    // Example: /api/analytics/range?start=2025-07-28&end=2025-07-30
    @GetMapping("/range")
    public List<Analytics> getAnalyticsRange(@RequestParam String start, @RequestParam String end) {
        return analyticsService.getAnalyticsByRange(start, end);
    }
}