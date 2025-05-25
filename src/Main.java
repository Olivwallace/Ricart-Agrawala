import Distributed.NodeDistributed;
import Distributed.NodePrinterService;
import Network.NodeInfo;
import Uteis.Uteis;

import java.util.List;

public class Main {

    public static boolean _IS_DEBUG = false;

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("Tente: java Main <id_node> <ip_node> <port_node> <is_service_printer>");
            _IS_DEBUG = true;
        }

        NodeInfo currentNodeInfo = (_IS_DEBUG) ?
                new NodeInfo("Service", "192.168.0.2", 5000)
                : new NodeInfo(args[0], args[1], Integer.parseInt(args[2]));

        String args3 = args[3];
        boolean isServer = _IS_DEBUG || args3.equals("true");

        List<NodeInfo> nodesList = Uteis.readNodes("configNodes.txt");
        NodeInfo service = Uteis.readService("serviceNode.txt");

        if(service != null) {
            if (isServer) {

                NodePrinterService newService = new NodePrinterService(service);
                newService.start();

            } else {

                NodeDistributed newNode = new NodeDistributed(currentNodeInfo, service, nodesList);
                newNode.start();

            }
        }

    }
}