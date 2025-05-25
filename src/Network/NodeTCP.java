package Network;

import Uteis.LogCat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class NodeTCP {

    protected NodeInfo nodeInfo;
    protected ServerSocket serverSocket;
    protected Executor executor;

    public NodeTCP(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
        this.executor = Executors.newFixedThreadPool(2);
    }

    public abstract void start();

    public void startServer() {
        try {
            serverSocket = new ServerSocket(nodeInfo.nodePort());
            System.out.println("[" + nodeInfo.id() + "] Listening on port " + nodeInfo.nodePort() + "...");
            executor.execute(this::listenForConnections);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForConnections() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                executor.execute(() -> handleIncomingConnection(clientSocket));
            } catch (IOException e) {
                System.out.println("[" + nodeInfo.id() + "] Error accepting connection: " + e.getMessage());
            }
        }
    }

    protected void handleIncomingConnection(Socket socket) {
        try (
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Message) {
                    Message message = (Message) obj;
                    handleMessage(message, socket.getInetAddress().getHostAddress());
                }
            }
        } catch (EOFException eof) {
            // end of stream
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[" + nodeInfo.id() + "] Error handling connection: " + e.getMessage());
        }
    }

    protected abstract void handleMessage(Message message, String senderIp);

    public void sendMessage(String targetIp, int targetPort, Message message) {
        try (
                Socket socket = new Socket(targetIp, targetPort);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            out.writeObject(message);
            out.flush();

        } catch (IOException e) {
            System.out.println("[" + nodeInfo.id() + "] Error sending message: " + e.getMessage());
        }
    }
}
