package NIOServerSelector;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NIOServerSelector {
    private static final Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

    public static void main(String... args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Set<SelectionKey> key = selector.selectedKeys();
            try {
                for (Iterator<SelectionKey> iterator = key.iterator(); iterator.hasNext(); ) {
                    SelectionKey key1 = iterator.next();
                    iterator.remove();
                    if (key1.isValid()) {
                        if (key1.isAcceptable()) {
                            accept(key1);
                        } else if (key1.isReadable()) {
                            read(key1);
                        } else if (key1.isReadable()) {
                            write(key1);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer =sockets.get(socketChannel);
        socketChannel.write(byteBuffer);
        if(!byteBuffer.hasRemaining()){
            byteBuffer.compact();
            key.interestOps(SelectionKey.OP_READ);
        }

    }

    private static void read(SelectionKey key) throws IOException {
        System.out.println("Waiting for read");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer =sockets.get(socketChannel);
        int data = socketChannel.read(byteBuffer);
        if (data == -1) {
            close(socketChannel);
        } else if (data != 0) {
            byteBuffer.flip();
            transmorgify(byteBuffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }

    }

    private static void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ);
        sockets.put(socketChannel, ByteBuffer.allocate(8080));
    }

    private static void close(SocketChannel socket) {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }



    private static void transmorgify(ByteBuffer byteBuffer) {

        for (int i = 0; i < byteBuffer.limit(); i++) {
            byteBuffer.put(i, (byte) transmorgify(byteBuffer.get(i)));
        }
    }

    private static int transmorgify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
