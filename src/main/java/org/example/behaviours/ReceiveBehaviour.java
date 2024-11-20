package org.example.behaviours;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.sound.midi.Soundbank;
import java.util.Arrays;
import java.util.List;

public class ReceiveBehaviour extends Behaviour {

    private MessageTemplate mt;
    private double shortestWayLenght = -1;
    private String shortestWay = "";
    @Override
    public void onStart() {
        mt = MessageTemplate.or(
                MessageTemplate.MatchProtocol("result"),
                MessageTemplate.MatchProtocol("deadlock")
        );
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null){
            if (msg.getProtocol().equals("deadlock")){
                System.err.println("\nНайден тупик :(");
                System.err.println(msg.getContent());
            }
            else {
                List<String> contentLines = Arrays.stream(msg.getContent().split("\n")).toList();
                double currentWayLength = 0;
                for (int i = 0; i < contentLines.size() - 1; i++){
                    double weight = Double.parseDouble(contentLines.get(i).split(": ")[1]);
                    currentWayLength += weight;
                }
                System.out.println("\nНайдено новое решение!");
                System.out.println("Длина пути -> " + currentWayLength);
                System.out.println(msg.getContent());

                //Обрабатываем полученное решение, в случае если оно первое
                if (shortestWayLenght == -1){
                    shortestWay = msg.getContent();
                    shortestWayLenght = currentWayLength;
                }
                //Обрабатываем полученное решение, является ли оно лучшим?
                if (shortestWayLenght > currentWayLength){
                    System.out.println("Это лучшее решение на данный момент!!!");
                    shortestWay = msg.getContent();
                    shortestWayLenght = currentWayLength;
                }

                String greenColor = "\u001B[32m";
                String resetColor = "\u001B[0m";

                System.out.println(greenColor + "\n___Best Solution___" + resetColor);
                System.out.println(greenColor + "Sum Distance: " + shortestWayLenght + resetColor);
                System.out.println(greenColor + shortestWay + resetColor);
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
