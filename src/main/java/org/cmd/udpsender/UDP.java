package org.cmd.udpsender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class UDP {
    public static void main(String[] args) throws IOException {
        try {
            createConnection();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void createConnection() throws IOException {
        var hostAddress = InetAddress.getByName("localhost");
        var inetSocket = new InetSocketAddress(hostAddress, 42069);
        var dataSocket = new DatagramSocket(42070);
        dataSocket.connect(inetSocket);

        var bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println(">");
            String line;
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                line = e.getMessage();
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            var datagramPacket = new DatagramPacket(line.getBytes(), line.getBytes().length);
            dataSocket.send(datagramPacket);
            
        }


    }

}