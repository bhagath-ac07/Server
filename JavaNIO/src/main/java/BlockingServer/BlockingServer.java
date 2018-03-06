package BlockingServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingServer {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    public static void main(String ... args) throws IOException {
        ServerSocket ss = new ServerSocket(8080);


        while (true) {
            Socket socket =ss.accept();

            System.out.println("Accepting");
            CompletableFuture.runAsync(()->{
                new Handler(socket).handle();
            },executorService);
            socket.setTcpNoDelay(false);
            socket.setSendBufferSize(8120);
            socket.setReceiveBufferSize(8120);
            socket.setKeepAlive( true );
        }
    }

    private static void handleRequest(Socket s) {
        System.out.println("submit");
        try (InputStream in = s.getInputStream();
             OutputStream outputStreamWriter = s.getOutputStream()){
            int data;
            System.out.println("sss");
            while ((data = in.read()) != -1){
                data =transmogrify(data);
                outputStreamWriter.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
