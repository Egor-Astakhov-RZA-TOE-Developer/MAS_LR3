package org.example.XMLParsingClasses;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Neighbour {
    @XmlElement
    private String name;
    @XmlElement
    private int weight;
}
