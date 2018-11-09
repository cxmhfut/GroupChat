package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientSend implements Runnable{
    private Socket socket;
    private String name;

    ChatClientSend(Socket socket, String name) {
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
