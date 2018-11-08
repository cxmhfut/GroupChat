import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPServer {
    public static void main(String[] args) throws IOException {
        int serverPort = 10000;
        DatagramSocket ds = new DatagramSocket(serverPort);

        byte buf[] = new byte[1024];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);

        while (true) {
            ds.receive(dp);
            String ip = dp.getAddress().getHostAddress();
            int port = dp.getPort();
            String data = new String(dp.getData(), 0, dp.getLength());
            System.out.println(ip + ":" + port);
            System.out.println(data);
        }
    }
}

class UDPClient {
    public static void main(String[] args) throws IOException {
        int serverPort = 10000;
        DatagramSocket ds = new DatagramSocket();
        String serverAddress = "192.168.238.1";

        BufferedReader br =
                new BufferedReader(new InputStreamReader(System.in));

        String line;
        while ((line = br.readLine()) != null) {
            if ("886".equals(line)) {
                break;
            }
            byte buf[] = line.getBytes();
            DatagramPacket dp = new DatagramPacket(buf, buf.length, InetAddress.getByName(serverAddress), serverPort);
            ds.send(dp);
        }

        br.close();
        ds.close();
    }
}