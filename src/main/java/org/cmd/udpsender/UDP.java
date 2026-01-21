package org.cmd.udpsender;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDP {
    public static void main(String[] args) {
        try {
            createConnection();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void createConnection() throws UnknownHostException, SocketException {
        var hostAddress = InetAddress.getByName("localhost");
        var inetSocket = new InetSocketAddress(hostAddress, 42069);

        var dataSocket = new DatagramSocket();
        dataSocket.connect(inetSocket);
        System.out.println(">");

        while (true) {
            dataSocket.send();
        }
        

    }

}