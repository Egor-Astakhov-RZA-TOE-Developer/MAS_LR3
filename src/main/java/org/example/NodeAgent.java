package org.example;

import jade.core.Agent;
import lombok.Getter;
import org.example.behaviours.InitiateBehaviour;
import org.example.XMLParsingClasses.AgentInfo;
import org.example.behaviours.ReceiveBehaviour;
import org.example.behaviours.SearchBehaviour;


@Getter
public class NodeAgent extends Agent {
    private AgentInfo agentInfo;

    @Override
    protected void setup() {
        super.setup();

        this.agentInfo = XMLParser.deserialize("src/main/resources/tests/test2/cfg" + this.getLocalName() + ".xml", AgentInfo.class).get();
//        DFHelper.registerAgent(this, "receiveService");

        if (agentInfo.isInitiator()){
            this.addBehaviour(new InitiateBehaviour());
            this.addBehaviour(new ReceiveBehaviour());
        } else {
            this.addBehaviour(new SearchBehaviour());
        }

    }
}
