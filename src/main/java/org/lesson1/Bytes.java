package org.lesson1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Bytes {
  public static void main(String[] args) {

    System.out.println("Hello, world!");
    // this doesn't work
    try (FileInputStream fileInputStream = new FileInputStream(
        "/Users/asilbekmuminov/code/go-http/httpfromtcp/src/main/java/org/lesson1/messages.txt")) {
      ArrayList<String> lines = new ArrayList<>();
      System.out.println("Reading file content:");
      // parse
      lines = parse(fileInputStream, lines);

      // output
      for (String lline : lines) {
        System.out.print("read: " + lline);
        System.err.println("\n");
      }

    } catch (FileNotFoundException e) {
      System.err.println("File not found: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }

  }

  public static ArrayList<String> parse(FileInputStream fileInputStream, ArrayList<String> lines) throws IOException {
    int data;
    StringBuilder chars = new StringBuilder();
    String line = "";

    while ((data = fileInputStream.read()) != -1) {
      chars.append((char) data);

      if (chars.length() == 8) {
        String charsString = chars.toString();
        if (charsString.contains("\n")) {
          String[] parts = charsString.split("\n", -1);

          for (int i = 0; i < parts.length - 1; i++) {
            line += parts[i];
            lines.add(line);
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
    lines.add(line);
    return lines;
  }

}
