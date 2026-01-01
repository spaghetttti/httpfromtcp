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
      parse(fileInputStream, lines);

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

  public static String parse(FileInputStream fileInputStream, ArrayList<String> lines) throws IOException {
      Integer data = 0;
      ArrayList<Character> chars = new ArrayList<Character>();
      ArrayList<String> parts = new ArrayList<>();
      String line = "";

      while (data != -1) {
        data = fileInputStream.read();
        chars.add((char) data.byteValue());

        if (chars.size() % 8 == 0) {
          String charsString = chars.stream().map(c -> Character.toString(c)).collect(Collectors.joining());
          if (charsString.contains("\n")) {
            parts.add(charsString.split("\n")[0]);
            parts.add(charsString.split("\n")[1]);
          }

          if (parts.size() == 2) {
            line += parts.getFirst();
            parts.removeFirst();
            lines.add(line);
            line = parts.getFirst();
            parts.removeFirst();
          } else { 
            line += charsString;
          }
          chars.removeAll(chars);
        }
      }
    return "";
  }

}
