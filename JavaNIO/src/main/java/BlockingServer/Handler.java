package BlockingServer;

import java.io.*;
import java.net.Socket;

public class Handler {
    private Socket socket;
    //private MyService myservice;

    public Handler(Socket socket){
        this.socket=socket;
    }
    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketOut=socket.getOutputStream();
        return new PrintWriter(socketOut,true);
    }
    private BufferedReader getReader(Socket socket) throws IOException{
        InputStream socketIn=socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn));
    }

    public void handle(){
        try {
            //Log.save(loglevel,"New connection accepted "+socket.getInetAddress()+":"+socket.getPort());
            BufferedReader br=getReader(socket);
            PrintWriter pw=getWriter(socket);
            String msg = null;
            int data;
            System.out.println("before");
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            System.out.println("before");

            while(  (data=inputStream.read())!=-1 ){
                data = transform(data);
                Thread.sleep(1000/data);
                outputStream.write(data);
                outputStream.flush();
            }
//            String sendout = "" ;
//            while( (msg=br.readLine())!=null ) {
//                System.out.println("recv: [" + msg + "]");
//                //System.out.println("send:["+sendout+"]");
//                pw.println(msg);
//                pw.flush();
//            }
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            if(socket!=null){
                //-- keep alive?
                //System.out.println("socket is not null and keep ready...");
                //socket.close();
            }
        }
    }

    private int transform(int data) {
        return Character.isLetter(data) ? data^' ':data;
    }
}
