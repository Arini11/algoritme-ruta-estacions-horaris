package org.example.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@XmlRootElement(name = "train")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    @XmlAttribute
    private String id;
    private String departureTime;
    private String arrivalTime;
    private Station departureStation;
    private Station arrivalStation;
    private BigInteger availableSeats;

    public int getDuration() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
        LocalDateTime departureTime = LocalDateTime.parse(this.departureTime, formatter);
        LocalDateTime arrivalTime = LocalDateTime.parse(this.arrivalTime, formatter);
        return (int) Math.abs(ChronoUnit.MINUTES.between(arrivalTime, departureTime));
    }

    @Override
    public String toString() {
        return "<"+departureStation.getName()+"-"+arrivalStation.getName()+"("+departureTime+" <-> "+arrivalTime+">";
    }
}

