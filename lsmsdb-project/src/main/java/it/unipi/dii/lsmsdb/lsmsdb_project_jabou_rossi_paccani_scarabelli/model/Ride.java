package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    private String status;

    private Double base_price;

    // --- NESTED SECTIONS (Matching your JSON) ---

    private DriverInfo driver;

    private CarInfo car;

    private RouteInfo route;

    @Field("booking_state")
    private BookingState bookingState;

    private Metadata metadata;

    // --- INNER CLASSES ---

    @Data
    @NoArgsConstructor
    public static class DriverInfo {
        @Field("id")
        private String id;

        @Field("name")
        private String name;

        @Field("phone")
        private String phone;

        @Field("avg_acceptance_rate")
        private Double avgAcceptanceRate;    }

    @Data
    @NoArgsConstructor
    public static class CarInfo {
        private String model;
        private String plate;
        private String comfort;
    }

    @Data
    @NoArgsConstructor
    public static class RouteInfo {
        private String origin;
        private String destination;
        @Field("route_id")
        private String routeId;
    }

    @Data
    @NoArgsConstructor
    public static class BookingState {
        @Field("total_seats")
        private Integer totalSeats;
        @Field("available_seats")
        private Integer availableSeats;
        @Field("has_waiting_list")
        private Boolean hasWaitingList;
    }

    @Data
    @NoArgsConstructor
    public static class Metadata {
        @Field("created_at")
        private String createdAt;
    }
}