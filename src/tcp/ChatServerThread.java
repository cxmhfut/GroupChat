package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ChatServerThread implements Runnable{

    private Socket socket;
    private List<Socket> list;

    public ChatServerThread(Socket socket, List<Socket> list) {
        this.socket = socket;
        this.list = list;
    }

    @Override
    public void run() {
        String ip = socket.getInetAddress().getHostAddress();
        int port = socket.getPort();
        String name = ip + ":" + port;

        try {
            BufferedReader bufIn =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufIn.readLine();
            String onlineMessage = name + "...上线了";
            System.out.println(onlineMessage);
            sendToAll(name, onlineMessage);
            PrintWriter prOut =
                    new PrintWriter(socket.getOutputStream(), true);
            prOut.println("Server:Hi " + name);
            String line;
            while ((line = bufIn.readLine()) != null) {
                String message = name + ":" + line;
                System.out.println(message);
                sendToAll(name, message);
            }
            socket.close();
            offline(name);
        } catch (SocketException e) {
            offline(name);
        } catch (IOException e) {
            System.out.println("Server-->" + name + ":消息发送失败");
        }
    }

    private synchronized void offline(String name) {
        String offlineMessage = name + "...下线了";
        System.out.println(offlineMessage);
        list.remove(socket);
        sendToOthers(name, offlineMessage);
    }

    private synchronized void sendToAll(String name, String message) {
        for (Socket socket : list) {
            try {
                PrintWriter prOut =
                        new PrintWriter(socket.getOutputStream(), true);
                prOut.println(message);
            } catch (SocketException e) {
                offline(name);
            } catch (IOException e) {
                System.out.println("Server-->" + name + ":消息发送失败");
            }
        }
    }

    private synchronized void sendToOthers(String name, String message) {
        for (Socket socket : list) {
            if (socket == this.socket || socket.isClosed()) {
                continue;
            }
            try {
                PrintWriter prOut =
                        new PrintWriter(socket.getOutputStream(), true);
                prOut.println(message);
            } catch (SocketException e) {
                offline(name);
            } catch (IOException e) {
                System.out.println("Server-->" + name + ":消息发送失败");
            }
        }
    }
}
