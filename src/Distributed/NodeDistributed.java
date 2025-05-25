package Distributed;

import Enums.MessageType;
import Interfaces.DistributedNodeInterface;
import Network.*;
import Uteis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NodeDistributed extends NodeTCP implements DistributedNodeInterface {

    private final NodeManager manager;
    private final RicartAgrawala ricartAgrawala;
    private CountDownLatch latchJoinConnect;

    private CountDownLatch latchCriticalSection;
    private CountDownLatch latchCritialSectionToServer;

    private NodeInfo serviceInfo;

    public NodeDistributed(NodeInfo currentNode, NodeInfo service, List<NodeInfo> initialKnownNodes){
        super(currentNode);
        serviceInfo = service;

        initialKnownNodes.remove(nodeInfo);
        manager = new NodeManager(initialKnownNodes);
        ricartAgrawala = new RicartAgrawala(this);
    }

    @Override
    public void start (){
        startServer();
        joinNetworkIfNeeded();
        run();
    }

    public void joinNetworkIfNeeded(){
        while (!manager.getPendingNodes().isEmpty()) {

            List<NodeInfo> currentPendingNodes = new ArrayList<>(manager.getPendingNodes());
            latchJoinConnect = new CountDownLatch(currentPendingNodes.size());

            for (NodeInfo pendingNode : currentPendingNodes) {
                if(pendingNode != nodeInfo) {
                    Message message = new Message(MessageType.JOIN.name(), nodeInfo, 0);
                    sendMessage(pendingNode, message);
                }
            }

            try {
                boolean completed = latchJoinConnect.await(1, TimeUnit.MINUTES);

                if(!completed){
                    for(NodeInfo node : currentPendingNodes){
                        manager.removeNode(node);
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void run() {

        Uteis.startBreakListener();

        while (!Uteis.isStopRequested()) {

            double p = Uteis.randomDouble();

            if(!ricartAgrawala.isInCriticalSection() && !ricartAgrawala.isRequestingCriticalSection()) {

                if (p > 0.5) {
                    ricartAgrawala.requestCriticalSection();
                }

                Uteis.delay(2000);

            }
        }

        //BroadCast de Saida...
        for(NodeInfo knownNode : manager.getKnownNodes()){
            Message leaveMsg = new Message(MessageType.LEAVE.name(), nodeInfo, ricartAgrawala.getTimestamp());
            sendMessage(knownNode, leaveMsg);
        }
    }

    @Override
    protected void handleMessage(Message message, String senderIp) {

        MessageType messageType = MessageType.valueOf(message.getType());

        switch (messageType) {
            case JOIN:

                NodeInfo sender = message.getSender();
                Integer currentTimestamp = ricartAgrawala.getTimestamp();
                Message messageAck = new Message(MessageType.JOIN_ACK.name(), nodeInfo, currentTimestamp, manager.getKnownNodes());
                sendMessage(sender, messageAck);
                manager.joinNewNode(sender);
                break;

            case JOIN_ACK:

                manager.confirmNode(message.getSender());
                manager.addNodes(message.getKnownNodes(), nodeInfo);
                if (latchJoinConnect != null) {
                    latchJoinConnect.countDown();
                }
                break;

            case LEAVE:

                manager.removeNode(message.getSender());
                break;

            case REQUEST:

                ricartAgrawala.onRequest(message, nodeInfo.id());
                break;

            case RELEASE:

//                if (latchCriticalSection != null) latchCriticalSection.countDown();
                ricartAgrawala.onRelease(message);

                break;

            case REPLY:

//                if (latchCriticalSection != null) latchCriticalSection.countDown();
                ricartAgrawala.onReply(message);


                break;

            case PRINT_ACK:

                ricartAgrawala.releaseCriticalSection();
//                if (latchCritialSectionToServer != null) latchCritialSectionToServer.countDown();

                break;
        }

        LogCat.logReceived(nodeInfo.id(), ricartAgrawala.getTimestamp(), message);
    }

    @Override
    public void requestBroadCast(Integer timestamp){
        List<NodeInfo> knownNodes = new ArrayList<>(manager.getKnownNodes());
//        latchCriticalSection = new CountDownLatch(knownNodes.size());

        Message releaseMsg = new Message(MessageType.REQUEST.name(), nodeInfo, timestamp);
        for(NodeInfo to : knownNodes){
            sendMessage(to, releaseMsg);
        }

//        try {
//            latchCriticalSection.await();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    public void reply(NodeInfo toNode, Integer timestamp){
        Message replyMessage = new Message(MessageType.REPLY.name(), nodeInfo, timestamp);
        sendMessage(toNode,replyMessage);
    }

    @Override
    public void release(NodeInfo toNode, Integer timestamp){
        Message requestMsg = new Message(MessageType.RELEASE.name(), nodeInfo, timestamp);
        sendMessage(toNode, requestMsg);
    }

    @Override
    public void sendMessage(NodeInfo toNode, Message message) {
        LogCat.logSend(toNode, message);
        sendMessage(toNode.nodeIP(), toNode.nodePort(), message);
    }

    @Override
    public List<NodeInfo> getKnownNodes() {
        return manager.getKnownNodes();
    }

    @Override
    public void runCriticalSection() {
        int k = Uteis.randomInt(1,3);
        Integer timestamp = ricartAgrawala.getTimestamp();
        ArrayList<Integer> sequencePrint = new ArrayList<>();

        for(int i = timestamp;  i < (k + timestamp); i++){
            sequencePrint.add(i);
        }

        Message criticalMsg = new Message(MessageType.PRINT.name(), nodeInfo, timestamp, sequencePrint);
        sendMessage(serviceInfo,criticalMsg);

        try {

//            latchCritialSectionToServer = new CountDownLatch(1);
//            long waitTime = 5L * (k + 1);
//
//            if(!latchCritialSectionToServer.await(waitTime, TimeUnit.SECONDS)){
//                System.out.println("NÃO FOI RECEBIDA PRINT_ACK");
//                ricartAgrawala.releaseCriticalSection();
//            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
