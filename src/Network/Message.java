package Network;

import org.w3c.dom.Node;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private String  type;
    private NodeInfo sender;
    private ArrayList<Integer> sequencePrint;
    private List<NodeInfo> knownNodes;
    private Integer timestamp;


    public Message(){ this(null,null,null,null, null); }

    public Message(String type, NodeInfo sender, Integer timestamp, ArrayList<Integer> sequence, List<NodeInfo> knownNodes){
        this.type = type;
        this.sender = sender;
        this.timestamp = timestamp;
        this.sequencePrint = sequence;
        this.knownNodes = knownNodes;
    }

    public Message(String type, NodeInfo sender, Integer timestamp, List<NodeInfo> knownNodes){
        this(type,sender,timestamp,null,knownNodes);
    }

    public Message(String type, NodeInfo sender, Integer timestamp, ArrayList<Integer> sequence){
        this(type,sender,timestamp, sequence,null);
    }

    public Message(String type, NodeInfo sender, Integer timestamp){
        this(type,sender,timestamp,null, null);
    }

    public Message(String type, NodeInfo sender){
        this(type,sender,null,null, null);
    }

    @Override
    public String toString() {
        return String.format("Message[type=%s, sender=%s, timestamp=%s, sequence=%s, knownNodes=%s]",
                type, sender, timestamp, sequencePrint, knownNodes);
    }

    public String toArrayPrint() {
        String s = "[ ";
        int max = sequencePrint.size();
        for(int i = 0; i < max; i++){
            Integer v = sequencePrint.get(i);
            s = s.concat((i == max - 1) ? (v + " ]") : (v + ", "));
        }
        return s;
    }

    public String toArrayNodes() {
        String s = "";
        int max = knownNodes.size();
        for(int i = 0; i < max; i++){
            String n = knownNodes.get(i).id();
            s = s.concat((i == max - 1) ? (n) : (n + ", "));
        }
        return s;
    }


    // Getters e Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public NodeInfo getSender() { return sender; }
    public void setSender(NodeInfo sender) { this.sender = sender; }

    public ArrayList<Integer> getSequencePrint() { return sequencePrint; }
    public void setSequencePrint(ArrayList<Integer> sequencePrint) { this.sequencePrint = sequencePrint; }

    public Integer getTimestamp() { return timestamp; }
    public void setTimestamp(Integer timestamp) { this.timestamp = timestamp; }

    public List<NodeInfo> getKnownNodes() { return  knownNodes; }
    public void setKnownNodes(List<NodeInfo> knownNodes){ this.knownNodes = knownNodes; }

}
