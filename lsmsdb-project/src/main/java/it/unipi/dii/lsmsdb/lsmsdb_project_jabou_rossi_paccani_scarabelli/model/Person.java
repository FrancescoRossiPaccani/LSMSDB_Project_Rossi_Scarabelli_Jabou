package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Person") // Questo crea un Nodo con etichetta :Person nel grafo
public class Person {

    @Id @GeneratedValue // Neo4j gestisce gli ID internamente
    private Long id;
    private String name;

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}