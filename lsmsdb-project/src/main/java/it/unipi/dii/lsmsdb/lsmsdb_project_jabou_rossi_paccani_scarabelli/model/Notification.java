package it.unipi.dii.lsmsdb.lsmsdb_project_jabou_rossi_paccani_scarabelli.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Notification {
    private int id;
    private String type;
    private String msg;
    private LocalDateTime timestamp;
    private Boolean read;
}
