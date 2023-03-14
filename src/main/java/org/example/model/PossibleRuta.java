package org.example.model;

import lombok.*;
import org.example.model.xml.Train;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PossibleRuta {
    private List<String> ruta;
    private int baseDuration;
    Map<Train,Integer> tempsEsperaList;
}
