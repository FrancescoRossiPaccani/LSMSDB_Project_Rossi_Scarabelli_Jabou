package it.unipi.dii.lsmsdb.lsmsdb_project.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookingAnalyticsService {

    @Autowired private MongoTemplate mongoTemplate;

    // 1. REVENUE STATS
    public Document getRevenueStats(String start, String end) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("bookingDate").gte(start).lte(end)),
                Aggregation.group().sum("finalPrice").as("totalRevenue")
                        .count().as("totalConfirmedBookings")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "bookings", Document.class);
        return results.getUniqueMappedResult();
    }

    // 2. HIGH VALUE CHURNERS (Utenti che spendono ma danno voti alti e spariscono?)
    public List<Document> getHighValueChurners(int daysThreshold) {
        // Filtra utenti con rating > 4.0
        MatchOperation filterRated = Aggregation.match(Criteria.where("reviews_passanger.average_rating").gte(4.0));

        // Join con bookings
        LookupOperation lookupBookings = LookupOperation.newLookup()
                .from("bookings")
                .localField("_id")
                .foreignField("passenger.id")
                .as("ub");

        // Calcola totale speso
        ProjectionOperation projectFields = Aggregation.project("personalInfo.name")
                .and("reviews_passanger.average_rating").as("rating")
                .and(AccumulatorOperators.Sum.sumOf("ub.finalPrice")).as("totalSpent");

        Aggregation agg = Aggregation.newAggregation(
                filterRated,
                Aggregation.limit(100), // Ottimizzazione
                lookupBookings,
                projectFields,
                Aggregation.sort(Sort.Direction.DESC, "totalSpent"),
                Aggregation.limit(10)
        );
        return mongoTemplate.aggregate(agg, "users", Document.class).getMappedResults();
    }

    // 3. TOP DRIVERS (Weighted Score)
    public List<Document> getTopDriverLeaderboard() {
        // Calcolo punti fiducia basati sui documenti
        AddFieldsOperation trustPointsStage = Aggregation.addFields()
                .addFieldWithValue("trustPoints",
                        ConditionalOperators.when(Criteria.where("personalInfo.is_identity_verified").is(true))
                                .then(50).otherwise(0)
                ).build();

        // Calcolo score finale: Trust * Rating * Acceptance
        ProjectionOperation projectStage = Aggregation.project("personalInfo.name")
                .and("reviews_driver.average_rating").as("rating")
                .and("driverInfo.avg_acceptance_rate").as("acceptanceRate")
                .and(
                        ArithmeticOperators.Multiply.valueOf("trustPoints")
                                .multiplyBy(ConditionalOperators.ifNull("reviews_driver.average_rating").then(1))
                                .multiplyBy(ConditionalOperators.ifNull("driverInfo.avg_acceptance_rate").then(0.5))
                ).as("performanceScore");

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("driverInfo.number_of_acceptance").gt(0)),
                trustPointsStage,
                projectStage,
                Aggregation.sort(Sort.Direction.DESC, "performanceScore"),
                Aggregation.limit(10)
        );

        return mongoTemplate.aggregate(agg, "users", Document.class).getMappedResults();
    }
}