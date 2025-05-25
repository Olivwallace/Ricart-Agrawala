package Distributed;

import Interfaces.DistributedNodeInterface;
import Network.Message;
import Network.NodeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RicartAgrawala {

    private DistributedNodeInterface delegate;

    //Control Critical Section
    private boolean requestingCriticalSection = false;
    private boolean inCriticalSection = false;
    private Integer timestamp = 0;

    private HashSet<NodeInfo> pendingReplies = new HashSet<>();
    private final List<NodeInfo> deferredNodes = new CopyOnWriteArrayList<>();

    public RicartAgrawala(DistributedNodeInterface delegate){
        this.delegate = delegate;
    }

    public synchronized void requestCriticalSection(){

        updateClock(timestamp);

        requestingCriticalSection = true;

        List<NodeInfo> others = delegate.getKnownNodes();  // sem filter

        pendingReplies = new HashSet<>(others);

        delegate.requestBroadCast(timestamp);


        if (pendingReplies.isEmpty()) {
            toggleInCriticalSection();
            delegate.runCriticalSection();
        }

    }

    public synchronized void onRelease(Message messageRelease){
        onReply(messageRelease);
    }

    public synchronized void onRequest(Message messageRequest, String currentID){

        updateClock(messageRequest.getTimestamp());

        NodeInfo fromNode = messageRequest.getSender();
        Integer fromNodeTimestamp = messageRequest.getTimestamp();

        boolean isDeferred = false;

        if(inCriticalSection){
            isDeferred = true;
        } else if(requestingCriticalSection){
            if(fromNodeTimestamp > timestamp){
                isDeferred = true;
            } else if (timestamp.equals(fromNodeTimestamp)) {
                isDeferred = (fromNode.id().compareTo(currentID) > 0);
            }
        }

        if(isDeferred){
            deferredNodes.add(fromNode);
        } else {
            delegate.reply(fromNode, timestamp);
        }

    }

    public synchronized void onReply(Message messageReply){

        updateClock(messageReply.getTimestamp());

        NodeInfo fromNode = messageReply.getSender();
        pendingReplies.remove(fromNode);

        if(requestingCriticalSection && pendingReplies.isEmpty()){
            toggleInCriticalSection();
            delegate.runCriticalSection();
        }
    }

    public synchronized void releaseCriticalSection(){

        updateClock(timestamp);

        toggleInCriticalSection();
        requestingCriticalSection = false;

        while(!deferredNodes.isEmpty()){
            NodeInfo releaseNode = deferredNodes.remove(0);
            delegate.release(releaseNode, timestamp);
        }

    }

    public synchronized void toggleInCriticalSection(){
        inCriticalSection = !inCriticalSection;
    }

    public synchronized boolean isInCriticalSection(){
        return inCriticalSection;
    }

    public synchronized boolean isRequestingCriticalSection(){
        return requestingCriticalSection;
    }

    public synchronized void updateClock(Integer receveidTimestamp){
        timestamp = Math.max(timestamp, receveidTimestamp) + 1;
    }

    public synchronized Integer getTimestamp() {
        return timestamp;
    }
}
