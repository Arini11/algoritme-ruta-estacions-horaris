package org.example.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

@XmlRootElement(name = "train")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    private String id;
    private String arrivalTime;
    private String departureTime;
    private String way;
    private Integer seats;
    private String updateTime;
    private Vehicle vehicle;
    private Line line;

    private boolean valid = true;

    @Override
    public String toString() {
        return line.getName()+" ("+departureTime.split(", ")[1].substring(0,5)+ " - " +arrivalTime.split(", ")[1].substring(0,5)+")";
    }
}

