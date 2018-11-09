package tcp;

import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    private String serverAddress;
    private int serverPort;
    private String name;

    private Socket socket;

    public ChatClient(String serverAddress, int serverPort, String name) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.name = name;
    }

    public void start() {
        try {
            socket = new Socket(serverAddress, serverPort);

            new Thread(new ChatClientSend(socket, name)).start();
            new Thread(new ChatClientReceive(socket)).start();
        } catch (IOException e) {
            System.out.println("客户端启动失败");
        }
    }

    public void shutDown() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("关闭客户端失败");
        }
    }

    public static void main(String[] args) {
        new ChatClient(ChatConfig.serverAddress, ChatConfig.serverPort, ChatConfig.ClientName).start();
    }
}
