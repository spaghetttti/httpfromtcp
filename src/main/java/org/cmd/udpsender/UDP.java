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
        // var inetSocket = new InetSocketAddress(InetAddress.getByName("localhost"), 42069);
        byte[] buffer = new byte[20];
        InetAddress laddr = InetAddress.getByName("localhost");
        // System.out.println(laddr.getHostAddress());
        // System.out.println(laddr.getHostName());
        var dataSocket = new DatagramSocket();
        // // dataSocket.bind(laddr);
        // System.out.println(dataSocket.isBound());
        // System.out.println(dataSocket.isConnected());
        // System.out.println(dataSocket.getLocalPort());
        // System.out.println(dataSocket.getInetAddress());

        var bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println(">");
            String line = "";
            try {       
                line = bufferedReader.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println(e.getMessage());

            }
            var datagramPacket = new DatagramPacket(line.getBytes(), line.getBytes().length, laddr, 42069);
            try {

                dataSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                // lol do nothing
            } 
            
        }


    }

}