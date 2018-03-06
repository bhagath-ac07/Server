package NIOServer;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOBlockingServer {
    public static void main(String ... args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            handle(socketChannel);
        }
    }

    private static void handle(SocketChannel serverSocketChannel) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8080);
        try {
            int data;
            while ((data=serverSocketChannel.read(byteBuffer))!=-1){
                byteBuffer.flip();
                transmorgify(byteBuffer);
                while (byteBuffer.hasRemaining()){
                    serverSocketChannel.write(byteBuffer);
                }
                byteBuffer.compact();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void transmorgify(ByteBuffer byteBuffer) {

        for(int i=0;i<byteBuffer.limit();i++){
            byteBuffer.put(i, (byte) transmorgify(byteBuffer.get(i)));
        }
    }

    private static int transmorgify(int data) {
        return Character.isLetter(data)? data^' ':data;
    }
}
