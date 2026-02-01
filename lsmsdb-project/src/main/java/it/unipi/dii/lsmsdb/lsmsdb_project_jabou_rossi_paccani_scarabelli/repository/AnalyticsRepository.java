package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.AnalyticsDaily;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends MongoRepository<AnalyticsDaily, String> {

    // Trova i report tra due date (essendo stringhe YYYY-MM-DD, l'ordinamento lessicografico funziona)
    @Query("{ '_id': { $gte: ?0, $lte: ?1 } }")
    List<AnalyticsDaily> findReportsBetweenDates(String startDate, String endDate);
}