package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.example.model.xml.Train;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Ticket {
    //Posar anotació @Id quan passi el programa a tickets/clients
    private String uuid;
    private String departureStationId;
    private String departureTime;
    private String arrivalStationId;
//    private String arrivalTime;
    private List<Train> trainsList;

    public Ticket(){
        this.uuid = UUID.randomUUID().toString();
    }

    public Ticket(String departureStationId, String departureTime, String arrivalStationId, List<Train> trainsList) {
        this.uuid = UUID.randomUUID().toString();
        this.departureStationId = departureStationId;
        this.departureTime = departureTime;
        this.arrivalStationId = arrivalStationId;
//        this.arrivalTime = arrivalTime;
        this.trainsList = trainsList;
    }


}

