package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.Analytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends MongoRepository<Analytics, String> {

    // Useful for finding stats within a date range (String comparison works for YYYY-MM-DD)
    List<Analytics> findByDateBetween(String startDate, String endDate);
}