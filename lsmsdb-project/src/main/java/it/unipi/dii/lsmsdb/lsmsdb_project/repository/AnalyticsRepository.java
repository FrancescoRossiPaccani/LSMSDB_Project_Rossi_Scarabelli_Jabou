package it.unipi.dii.lsmsdb.lsmsdb_project.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project.model.Analytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnalyticsRepository extends MongoRepository<Analytics, String> {
    // Poiché l'ID è la data (YYYY-MM-DD), possiamo usare Between sulle stringhe
    List<Analytics> findByDateBetween(String startDate, String endDate);
}