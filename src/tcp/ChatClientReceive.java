package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ChatClientReceive implements Runnable {
    private Socket socket;

    ChatClientReceive(Socket socket) {
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
