package org.example.XMLParsingClasses;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlRootElement(name = "Agent")
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentInfo {
    @XmlElement(name = "agentName")
    private String agentName;
    @XmlElement
    private boolean isInitiator;
    @XmlElement
    private String requiredAgent;
    @XmlElementWrapper(name = "neighbours")
    @XmlElement(name = "neighbour")
    private List<Neighbour> neighbours;
}
