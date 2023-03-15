package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.xml.Train;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainAux {
    int duracioTotal;
    Train train;

    //Ids de les estacions
    List<String> rutaEstacions;
//    List<Train> rutaTrains;
}
