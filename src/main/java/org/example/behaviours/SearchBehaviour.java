package org.example.behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.NodeAgent;
import org.example.XMLParsingClasses.Neighbour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchBehaviour extends Behaviour {
    private final List<String> passedAgentNames = new ArrayList<>();
    private boolean isDone = false;
    private MessageTemplate mt;
    @Override
    public void onStart() {
        mt = MessageTemplate.or(
                MessageTemplate.MatchProtocol("initiate"),
                MessageTemplate.MatchProtocol("search"));
    }

    @Override
    public void action() {
        /*try {
            Thread.sleep(4_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/

        ACLMessage msg = myAgent.receive(mt);
        if (msg != null){
            StringBuilder agentWay = new StringBuilder();
            List<String> contentList = Arrays.stream(msg.getContent().split("\n")).toList();
            String aimAgent = contentList.get(0);
            for (int i = 1; i < contentList.size(); i++) {
                agentWay.append(contentList.get(i)).append("\n");
                passedAgentNames.add(contentList.get(i).split(": ")[0]); // берем только имя агента, который уже был
            }

            NodeAgent nodeAgent = null;
            try {
                nodeAgent = (NodeAgent) myAgent;
            } catch (Exception e){
                System.err.println("Classcast exception");
            }


            //  Проверка является ли данный агент искомым (если да, отправляем результат инициатору)
            if (myAgent.getLocalName().equals(aimAgent)){

                ACLMessage resultMsg = new ACLMessage(ACLMessage.REQUEST);
                resultMsg.addReceiver(new AID(passedAgentNames.get(0), AID.ISLOCALNAME));
                resultMsg.setProtocol("result");
                resultMsg.setContent(agentWay + myAgent.getLocalName());
                myAgent.send(resultMsg);

                //System.out.println("\nНайден путь c длиной " + sumDistance + "\n" + agentWay + myAgent.getLocalName());
            }

            else {
                List<String> neighboursNames = nodeAgent.getAgentInfo().getNeighbours().stream().map(Neighbour::getName).toList();
                List<String> notVisitedNeighboursNames = new ArrayList<>();
                for(String neighboursName: neighboursNames){
                    if(!passedAgentNames.contains(neighboursName)){
                        notVisitedNeighboursNames.add(neighboursName);
                    }
                }
                //  (если нет непройденных соседей) -> Тупик: Отправка пути тупика агенту-инициатору
                if(notVisitedNeighboursNames.isEmpty()){
                    ACLMessage resultMsg = new ACLMessage(ACLMessage.REQUEST);
                    resultMsg.addReceiver(new AID(passedAgentNames.get(0), AID.ISLOCALNAME));
                    resultMsg.setProtocol("deadlock");

                    String content = agentWay + myAgent.getLocalName();

                    resultMsg.setContent(content);
                    myAgent.send(resultMsg);

                    //System.err.println("\nНайден тупик :( " + "\n" + agentWay + myAgent.getLocalName());
                } else { //  (если нет) -> Отправка сообщений о поиске соседям

                    ACLMessage searchMsg = new ACLMessage(ACLMessage.PROPOSE);
                    searchMsg.setProtocol("search");
                    for (String nbrName: notVisitedNeighboursNames){
                        searchMsg.addReceiver(new AID(nbrName, AID.ISLOCALNAME));

                        Neighbour nbr = null;
                        for(Neighbour neighbour: nodeAgent.getAgentInfo().getNeighbours()){
                            if(neighbour.getName().equals(nbrName)){
                                nbr = neighbour;
                            }
                        }
                        String content = msg.getContent() +
                                "\n" + myAgent.getLocalName() + ": " + nbr.getWeight();

                        searchMsg.setContent(content);
                        myAgent.send(searchMsg);
                        searchMsg.clearAllReceiver();
                    }
                }
            }
        } else {
            block();
        }

    }

    @Override
    public boolean done() {
        return isDone;
    }
}
