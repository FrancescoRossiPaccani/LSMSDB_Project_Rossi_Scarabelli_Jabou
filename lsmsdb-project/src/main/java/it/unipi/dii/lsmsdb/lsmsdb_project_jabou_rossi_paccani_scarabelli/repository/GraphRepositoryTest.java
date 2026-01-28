package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.repository;

import it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model.GraphTest;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GraphRepositoryTest extends Neo4jRepository<GraphTest, Long> {
}