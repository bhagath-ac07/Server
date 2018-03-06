package BlockingServer;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadGenerator {
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);
    static volatile int  i =0;
    public static void main(String ... args){
        Socket socket[] = new Socket[1000];
        for (Socket soc:socket ) {
            try {
                soc = new Socket("localhost",8080);
                i++;
                final int val = i;
                Socket finalSoc = soc;
                executorService.submit(()->{
                    try(OutputStream os = finalSoc.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os);
                        InputStream in = finalSoc.getInputStream();
                        InputStreamReader isr = new InputStreamReader(in);
                        BufferedReader br = new BufferedReader(isr);
                        BufferedWriter bw = new BufferedWriter(osw)) {
                        System.out.println("before Message sent to the server : ");
                        String sendMessage = val+"\n";
                        System.out.println(sendMessage);
                        bw.write(sendMessage);
                        bw.flush();
                        osw.close();
                        String message = br.readLine();
                        System.out.println("Message received from the server : " + message);

                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
