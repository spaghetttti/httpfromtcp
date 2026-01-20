package org.lesson2;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.lesson1.Bytes;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TCP {

  static ServerSocket socket;

  public static void main(String[] args) {
    try {
      createConnection();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  static public void createConnection() throws IOException, InterruptedException {
    socket = new ServerSocket(42069);
    Socket data = null;
    while ((data = socket.accept()) != null) {
      System.out.println("connection accepted");

      BlockingQueue<String> lines = Bytes.parse(data.getInputStream());
      
      String line;
      while ((line = lines.take()) != null) {
        if (line.equals("__EOF__"))
          break;
        System.out.println(line);
      }

      data.close();
      System.out.println("connection closed");
    }
  }

}
