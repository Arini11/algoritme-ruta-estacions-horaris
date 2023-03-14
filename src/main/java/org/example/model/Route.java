package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Route {

    private String id;
    private String departureStationId;
    private String arrivalStationId;
    private Date departureTime;
    // LLista d'ids de trains
    private List<String> trains;

    public Route() {
        this.departureStationId = "asd";
        this.arrivalStationId = "asd";
        this.departureTime = new Date();
    }
}

