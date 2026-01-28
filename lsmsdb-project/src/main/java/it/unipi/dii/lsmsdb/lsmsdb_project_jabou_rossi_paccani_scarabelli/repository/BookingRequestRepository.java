package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.BookingRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRequestRepository extends CrudRepository<BookingRequest, String> {
}