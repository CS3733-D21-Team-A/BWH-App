package edu.wpi.cs3733.d21.teamA.extras;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient {
    private final Socket client;
    private final InputStream in;
    private final OutputStream out;

    public SocketClient(String host, int port) throws IOException {
        client = new Socket(host, port);
        in = client.getInputStream();
        out = client.getOutputStream();
        //System.out.println("connected");
    }

    public String getMassage() throws IOException {
        int len;
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[1024];
//        while ((len = in.read(bytes)) == 1023) {
//            sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
//        }
        len = in.read(bytes);
        sb.append(new String(bytes,0,len, StandardCharsets.UTF_8));
        //System.out.println("massage: "+sb);
        return sb.toString();
    }

    public void send(String massage) throws IOException {
        out.write(massage.getBytes(StandardCharsets.UTF_8));
    }

}