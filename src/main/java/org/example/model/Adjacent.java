package org.example.model;

import lombok.*;
import org.example.model.xml.Station;
import org.example.model.xml.Train;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Adjacent {
    private Station estacio;
    private Train train;
    private int cost;

}
