package org.lesson1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Bytes {
  public static void main(String[] args) throws InterruptedException {

    System.out.println("Hello, world!");
    try (FileInputStream fileInputStream = new FileInputStream("src/main/java/org/lesson1/messages.txt")) {
      System.out.println("Reading file content:");
      read(fileInputStream);

    } catch (FileNotFoundException e) {
      System.err.println("File not found: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }

  }

  public static void read(InputStream inputStream) throws IOException, InterruptedException {
    // parse
    BlockingQueue<String> lines = parse(inputStream);

    // output
    String lline = "";
    while ((lline = lines.take()) != null) {
      if (lline.contains("__EOF__"))
        break;
      System.out.print("read: " + lline);
      System.err.println("\n");
    }
  }

  public static BlockingQueue<String> parse(InputStream inputStream) throws IOException {
    BlockingQueue<String> lines = new ArrayBlockingQueue<String>(4);
    Thread readeThread = new Thread(() -> {
      try {
        int data;
        StringBuilder chars = new StringBuilder();
        String line = "";

        while ((data = inputStream.read()) != -1) {
          chars.append((char) data);

          if (chars.length() == 8) {
            String charsString = chars.toString();
            if (charsString.contains("\n")) {
              String[] parts = charsString.split("\n", -1);

              for (int i = 0; i < parts.length - 1; i++) {
                line += parts[i];
                lines.put(line);
                line = "";
              }

              line = parts[parts.length - 1];

            } else {
              line += charsString;
            }
            chars.setLength(0);
          }
        }
        line += chars.toString();
        lines.put(line);
      } catch (Exception e) {
        // TODO: handle exception
      } finally {
        try {
          lines.put("__EOF__");
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });

    readeThread.start();
    return lines;
  }

}
