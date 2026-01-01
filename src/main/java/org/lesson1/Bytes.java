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
      Integer data = 0;
      ArrayList<Character> chars = new ArrayList<Character>();
      System.out.println("Reading file content:");
      while (data != -1) {
        data = fileInputStream.read();
        chars.add((char) data.byteValue());

        if (chars.size() % 8 == 0) {
          String charsString = chars.stream().map(c -> Character.toString(c)).collect(Collectors.joining());
          System.out.print("read: " + charsString);
          System.err.println("\n");
          chars.removeAll(chars);
        } 
      }

    } catch (FileNotFoundException e) {
      System.err.println("File not found: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }


  }

}
