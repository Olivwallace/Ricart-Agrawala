package Uteis;

import Network.NodeInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class Uteis {

    public static NodeInfo readService(String caminhoArquivo){
        List<NodeInfo> list = readNodes(caminhoArquivo);

        if(!list.isEmpty()){
            return list.get(0);
        }

        return null;
    }

    public static List<NodeInfo> readNodes(String caminhoArquivo) {
        List<NodeInfo> nodes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                // Exemplo de linha: id,ip,porta
                String[] partes = linha.split(",");
                String id = partes[0].trim();
                String ip = partes[1].trim();
                Integer porta = Integer.parseInt(partes[2].trim());
                nodes.add(new NodeInfo(id, ip, porta));
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar arquivo de nodes: " + e.getMessage());
        }
        return nodes;
    }


    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static final AtomicBoolean stopRequested = new AtomicBoolean(false);

    public static void startBreakListener() {
        Thread t = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    String line = reader.readLine();
                    // Se quiser apenas ENTER, substitua condição por line != null
                    if (line == null || line.equalsIgnoreCase("break") || line.isEmpty()) {
                        stopRequested.set(true);
                        System.out.println("** Interrupção manual acionada **");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);  // não bloqueia a saída da JVM
        t.start();
    }


    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min não pode ser maior que max");
        }

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


    public static double randomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }


    public static boolean isStopRequested() {
        return stopRequested.get();
    }
}
