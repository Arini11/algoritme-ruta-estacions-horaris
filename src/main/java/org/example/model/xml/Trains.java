package org.example.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement(name = "trains")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class Trains {
    @XmlElement(name = "train")
    private List<Train> trains = null;

    public List<Train> getTrains() {
        return trains;
    }

    public void setTrains(List<Train> trains) {
        this.trains = trains;
    }

    public Trains(List<Train> trains) {
        this.trains = trains;
    }
}
