package Distributed;

import Enums.MessageType;
import Network.Message;
import Network.NodeInfo;
import Network.NodeTCP;
import Uteis.Uteis;

import java.util.ArrayList;
import java.util.List;

public class NodePrinterService extends NodeTCP {

    public NodeInfo currentClient;
    public Integer timestamp = 0;

    public NodePrinterService(NodeInfo currentNode){
        super(currentNode);
    }

    @Override
    public void start() {
        startServer();
    }

    @Override
    protected void handleMessage(Message message, String senderIp) {
        MessageType messageType = MessageType.valueOf(message.getType());

        switch (messageType){
            case PRINT:
                showCriticalSection(message);
        }
    }

    public void showCriticalSection(Message message){

        updateClock(message.getTimestamp());

        String nodeId = message.getSender().id();
        List<Integer> sequence = message.getSequencePrint();

        try {

            System.out.print("[" + nodeId + "] - ");
            while (!sequence.isEmpty()){
                Integer i = sequence.remove(0);
                System.out.print(i + ((sequence.isEmpty()) ? "\n" : ", "));
                Uteis.delay(5000);

                updateClock(timestamp);
            }

            Message printAck = new Message(MessageType.PRINT_ACK.name(),nodeInfo, getTimestamp());
            sendMessage(message.getSender().nodeIP(), message.getSender().nodePort(), printAck);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void updateClock(Integer receveidTimestamp){
        timestamp = Math.max(timestamp, receveidTimestamp) + 1;
    }

    public synchronized Integer getTimestamp() {
        return timestamp;
    }
}
