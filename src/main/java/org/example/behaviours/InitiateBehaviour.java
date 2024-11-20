package org.example.behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.NodeAgent;
import org.example.XMLParsingClasses.Neighbour;

import java.util.List;

public class InitiateBehaviour extends Behaviour {
    private boolean hasSendInitialMsg = false;
    private boolean isDone = false;
    MessageTemplate mt;

    @Override
    public void onStart() {
        mt = MessageTemplate.MatchProtocol("result");
    }

    @Override
    public void action() {
        if (!hasSendInitialMsg){

            // Кого ищем
            NodeAgent nodeAgent = null;

            /*try {
                Thread.sleep(15_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/

            try {
                nodeAgent = (NodeAgent) myAgent;
            } catch (Exception e){
                System.err.println("Classcast exception");
            }

            ACLMessage initMsg = new ACLMessage(ACLMessage.PROPOSE);
            initMsg.setProtocol("initiate");
            List<Neighbour> neighbourList = nodeAgent.getAgentInfo().getNeighbours();
            for (Neighbour nbr: neighbourList){
                String content = nodeAgent.getAgentInfo().getRequiredAgent() +
                        "\n" + myAgent.getLocalName() + ": " + nbr.getWeight();
//                String content = nodeAgent.getAgentInfo().getRequiredAgent();
                initMsg.setContent(content);
                initMsg.addReceiver(new AID(nbr.getName(), AID.ISLOCALNAME));
                myAgent.send(initMsg);
                initMsg.clearAllReceiver();
            }


            System.out.println(this.myAgent.getLocalName() + " -> инициировал поиск.");
            hasSendInitialMsg = true;
        }

        ACLMessage msg = myAgent.receive(mt);
        if (msg != null){
            // TODO Обработка присланного результата
        } else {
            block();
        }

    }

    @Override
    public boolean done() {
        return isDone;
    }
}
