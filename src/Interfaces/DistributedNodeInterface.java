package Interfaces;

import Network.Message;
import Network.NodeInfo;
import java.util.List;

public interface DistributedNodeInterface {
    void release(NodeInfo toNode, Integer timestamp);
    void reply(NodeInfo toNode, Integer timestamp);
    void requestBroadCast(Integer timestamp);
    void sendMessage(NodeInfo toNode, Message message);
    List<NodeInfo> getKnownNodes();
    void runCriticalSection();
}
