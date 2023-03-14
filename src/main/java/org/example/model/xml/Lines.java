package org.example.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement(name = "lines")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class Lines {
    @XmlElement(name = "line")
    private List<Line> lines = null;

    public Lines(List<Line> linies) {
        this.lines = linies;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }
}
