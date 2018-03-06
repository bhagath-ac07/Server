package NIOServerBad;

import NIOServerSelector.NIOServerSelector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class NIOServerBad {
    public static void main(String ... args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);
        Map<SocketChannel,ByteBuffer> sockets = new ConcurrentHashMap<>();
        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(socketChannel!=null){
                socketChannel.configureBlocking(false);
                sockets.put(socketChannel,ByteBuffer.allocate(8080));
            }
            sockets.keySet().removeIf(new Predicate<SocketChannel>() {
                @Override
                public boolean test(SocketChannel socketChannel) {
                    return !socketChannel.isOpen();
                }
            });
            sockets.forEach(NIOServerBad::handle);


        }
    }

    private static void close(SocketChannel socket) {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    private static void handle(SocketChannel socket,ByteBuffer buffer) {
        try {
            int data = socket.read(buffer);
            if(data==-1){
                close(socket);
            } else if(data!=0){
                buffer.flip();
                transmorgify(buffer);
                socket.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            close(socket);
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
