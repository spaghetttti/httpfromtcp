package org.cmd.udpsender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
// import java.net.InetSocketAddress;
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
        InetAddress laddr = InetAddress.getByName("localhost");
        var dataSocket = new DatagramSocket();
        boolean running = true;

        var bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            System.out.println("> ");
            String line = "";
            try {       
                line = bufferedReader.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println(e.getMessage());

            }
            byte [] data = line.getBytes();
            var datagramPacket = new DatagramPacket(data, data.length, laddr, 42069);
            try {

                dataSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            } 
            
        }
        dataSocket.close();

    }

}