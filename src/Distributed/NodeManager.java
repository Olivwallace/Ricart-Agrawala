package Distributed;

import Network.NodeInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NodeManager {

    List<NodeInfo> knownNodes = new CopyOnWriteArrayList<>();
    List<NodeInfo> pendingNodes = new CopyOnWriteArrayList<>();

    public NodeManager(List<NodeInfo> initialKnownNodes){
        for(NodeInfo nodeInfo : initialKnownNodes){
            addNode(nodeInfo);
        }
    }

    public synchronized void addNodes(List<NodeInfo> nodes, NodeInfo current){
        for(NodeInfo newNode : nodes){
            if(newNode != current) {
                addNode(newNode);
            }
        }
    }

    public synchronized void addNode(NodeInfo node){
        if (node != null) {
            if (!knownNodes.contains(node) && !pendingNodes.contains(node)) {
                pendingNodes.add(node);
            }
        }
    }

    public synchronized void confirmNode(NodeInfo node){
        if (node != null) {
            if (pendingNodes.remove(node)) {
                joinNewNode(node);
            }
        }
    }

    public synchronized void joinNewNode(NodeInfo newNode){
        if(newNode != null){
            if(!knownNodes.contains(newNode)){
                knownNodes.add(newNode);
            }
        }
    }

    public synchronized void removeNode(NodeInfo node) {
        if (node != null) {
            knownNodes.remove(node);
            pendingNodes.remove(node);
        }
    }

    public List<NodeInfo> getPendingNodes() {
        return pendingNodes;
    }

    public List<NodeInfo> getKnownNodes() {
        return knownNodes;
    }
}
