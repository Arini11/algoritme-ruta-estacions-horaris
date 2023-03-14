package org.example.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement(name = "stations")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class Stations {
    @XmlElement(name = "station")
    private List<Station> stations = null;

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public Stations(List<Station> stations) {
        this.stations = stations;
    }
}
