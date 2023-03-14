package org.example.model.xml;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Data {

    @XmlElement(name = "lines")
    private Lines lines = null;
    @XmlElement(name = "trains")
    private Trains trains = null;
    @XmlElement(name = "vehicles")
    private Vehicles vehicles = null;
    @XmlElement(name = "stations")
    private Stations stations = null;

}
