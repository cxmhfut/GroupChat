import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

class ServerThread implements Runnable {

    private Socket socket;
    private List<Socket> list;

    ServerThread(Socket socket, List<Socket> list) {
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

class TCPServer {
    public static void main(String[] args) throws IOException {
        int serverPort = 10001;
        ServerSocket serverSocket = new ServerSocket(serverPort);

        List<Socket> list = new ArrayList<>();

        while (true) {
            Socket socket = serverSocket.accept();
            synchronized (TCPServer.class) {
                list.add(socket);
            }
            new Thread(new ServerThread(socket, list)).start();
        }
    }
}

class ClientSend implements Runnable {

    private Socket socket;
    private String name;

    ClientSend(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(System.in));
            PrintWriter prOut =
                    new PrintWriter(socket.getOutputStream(), true);
            prOut.println(name);//向服务器发送姓名
            String line;
            while ((line = br.readLine()) != null) {
                prOut.println(line);
                if ("886".equals(line)) {
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("发送消息失败");
        }

    }
}

class ClientReceive implements Runnable {

    private Socket socket;

    ClientReceive(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufIn =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = bufIn.readLine()) != null) {
                System.out.println(line);
            }
        } catch (SocketException e) {
            System.out.println("您已离线");
        } catch (IOException e) {
            System.out.println("接收消息失败");
        }
    }
}

class TCPClient {
    public static void main(String[] args) throws IOException {
        String name = args[1];

        String serverAddress = args[0];
        int serverPort = 10001;

        Socket socket = new Socket(serverAddress, serverPort);

        new Thread(new ClientSend(socket, name)).start();
        new Thread(new ClientReceive(socket)).start();
    }
}
