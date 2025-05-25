package Network;

import java.io.Serializable;
import java.util.Objects;

public class NodeInfo implements Serializable {

    String id;
    String nodeIP;
    Integer nodePort;

    public NodeInfo(){}

    public NodeInfo(String id, String nodeIP, Integer nodePort){
        this.id = id;
        this.nodeIP = nodeIP;
        this.nodePort = nodePort;
    }

    public String id(){
        return id;
    }

    public String nodeIP(){
        return nodeIP;
    }

    public Integer nodePort(){
        return nodePort;
    }

    // ---------------- Uteis
    @Override
    public String toString() {
        return String.format("%s,%s,%d", id, nodeIP, nodePort);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInfo)) return false;
        NodeInfo that = (NodeInfo) o;
        return id.equals(that.id) &&
                nodeIP.equals(that.nodeIP) &&
                nodePort.equals(that.nodePort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nodeIP, nodePort);
    }

}
