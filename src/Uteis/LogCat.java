package Uteis;

import Enums.MessageType;
import Network.Message;
import Network.NodeInfo;

import javax.swing.plaf.synth.SynthTextAreaUI;

public class LogCat {


    public static void logSend(NodeInfo to, Message m){

        switch (MessageType.valueOf(m.getType())){
            case REQUEST:
                System.out.format("[ %4d, %s] -- REQUEST --> [ %s ]\n",
                        m.getTimestamp(), m.getSender().id(), to.id());
                break;
            case RELEASE:
                System.out.format("[ OK, %s] -- RELEASE --> [ %s ]\n", m.getSender().id(), to.id());
                break;
            case REPLY:
                System.out.format("[ OK, %s] -- REPLY --> [ %s ]\n", m.getSender().id(), to.id());
                break;
            case PRINT:
                System.out.format("[ %4d, %s ] -- PRINT --> %s\n", m.getTimestamp(), m.getSender().id(), m.toArrayPrint());
                break;
            case JOIN:
                System.out.format("[ %4d, %s] -- JOIN --> [ %s ]\n", m.getTimestamp(), m.getSender().id(), to.id());
                break;
            case JOIN_ACK:
                System.out.format("[ %4d, %s, K_Nodes( %s ) ] -- JOIN_ACK --> [ %s ]\n",
                        m.getTimestamp(), m.getSender().id(), m.toArrayNodes(), to.id());
                break;
        }
    }

    public static void logReceived(String id, Integer timestamp, Message m){
        switch (MessageType.valueOf(m.getType())){
            case REQUEST:
                System.out.format("[ %4d, %s ] <-- REQUEST -- [ %4d , %s]\n",
                        timestamp,id,m.getTimestamp(), m.getSender().id());
                break;
            case RELEASE:
                System.out.format("[ %4d, %s ] <-- RELEASE -- [ OK , %4d, %s]\n",
                        timestamp,id,m.getTimestamp(), m.getSender().id());
                break;
            case REPLY:
                System.out.format("[ %4d, %s ] <-- REPLY -- [ OK , %4d, %s]\n",
                        timestamp,id,m.getTimestamp(), m.getSender().id());
                break;
            case PRINT_ACK:
                System.out.format("[ %4d, %s ] <-- PRINT_ACK -- [SERVICE]\n",timestamp,id);
                break;
            case JOIN:
                System.out.format("[ %4d, %s] <-- JOIN -- [ %4d, %s ]\n", timestamp,id, m.getTimestamp(), m.getSender().id());
                break;
            case JOIN_ACK:
                System.out.format("[ %4d, %s] <-- JOIN_ACK --> [ %4d, %s, K_Nodes( %s )  ]\n",
                        timestamp,id, m.getTimestamp(), m.getSender().id(), m.toArrayNodes());
        }
    }


}
