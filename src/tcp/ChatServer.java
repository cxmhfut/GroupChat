package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private int port;//服务器监听端口
    private List<Socket> sockets;
    private boolean isRun;

    public ChatServer(int port) {
        this.port = port;
        this.sockets = new ArrayList<>();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            isRun = true;

            while (isRun) {
                Socket socket = serverSocket.accept();
                synchronized (ChatServer.class) {
                    sockets.add(socket);
                }
                new Thread(new ChatServerThread(socket, sockets)).start();
            }

            serverSocket.close();
        } catch (IOException e) {
            System.out.println("服务器启动失败");
        }
    }

    public void shutDown() {
        isRun = false;
    }

    public static void main(String[] args) {
        new ChatServer(ChatConfig.serverPort).start();
    }
}
