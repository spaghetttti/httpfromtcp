package org.internal.request;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class Request {
  public RequestLine requestLine;
  public HashMap<String, String> headers;
  public byte[] body;
  public String test;
  private STATE state;

  private enum STATE {
    DONE,
    INITIALIZED
  }

  public static Request fromReader(InputStream reader) throws Exception {
    var request = new Request();
    request.state = STATE.INITIALIZED;
    byte[] buffer = new byte[8];
    byte[] mainBuffer = new byte[8];
    int bytesRead = 0;
    int readToIndex = 0;
    int consumedBytes = 0;
    int counter = 0;

    while (request.state != STATE.DONE) {
      // just for testing
      System.out.println("read counter: " + counter++);
      // fill out buffer with read bytes and return how many did we read
      bytesRead = reader.read(buffer);
      System.out.println("[bytesRead]: " + bytesRead);
      if (bytesRead != -1) {

        // if the number of newly read bytes does not fit in the mainBuffer , we double
        // mainBuffers size while saving it's previous values
        if (readToIndex + bytesRead >= mainBuffer.length - 1) {
          System.out.println("[mainBuffer.length]: " + mainBuffer.length);
          var tempBuffer = new byte[mainBuffer.length * 2];
          System.out.println("[tempBuffer.length]: " + tempBuffer.length);
          System.arraycopy(mainBuffer, 0, tempBuffer, 0, readToIndex);
          mainBuffer = tempBuffer;
        }
        // we safely copy the newly read bytes into mainBuffer now
        System.out.println("[consumedBytes] pre: " + consumedBytes);
        System.arraycopy(buffer, 0, mainBuffer, readToIndex, bytesRead);
        // new readToIndex meaning previous point up until we have read + newly read
        // bytes (readToIndex + bytesRead)
        readToIndex += bytesRead;
        System.out.println("[readToIndex]: " + readToIndex);
        consumedBytes = request.parse(Arrays.copyOfRange(mainBuffer, 0, readToIndex), request);
        System.out.println("[consumedBytes] post: " + consumedBytes);
        if (consumedBytes != 0) {

          // we found the CRLF, time to assign requestLine
          // later to add logic to continue reading the steam to parse other http parts,
          // but save the leftover unparsed yet bytes
          readToIndex -= consumedBytes;
          System.arraycopy(mainBuffer, consumedBytes, mainBuffer, 0, readToIndex);
          // just so the tests pass, will remove later
        }
      } else {
        break;
      }

      if (request.state == STATE.DONE) {
        break;
      }
    }

    return request;
  }

  private int parse(byte[] data, Request request) throws Exception {
    // var stringBuffer = new StringBuffer();
    // int bytesConsumed = 0;
    if (request.state == STATE.INITIALIZED) {
      for (int i = 0; i < data.length - 1; i++) {
        System.out.println(new String(Arrays.copyOfRange(data, 0, i), StandardCharsets.UTF_8));

        if (i > 3 && (char) data[i - 3] == '\r' && data[i - 2] == '\n' && (char)
          data[i - 1] == '\r' && data[i] == '\n') {
          System.out.println("why");
          // state = STATE.DONE;
          }

        if (i > 1 && (char) data[i - 1] == '\r' && data[i] == '\n') {
          System.out.println("here!!!");
          request.state = STATE.DONE;
          request.requestLine = parseRequestLine(new String(Arrays.copyOfRange(data, 0, i - 1), StandardCharsets.UTF_8));
          // bytesConsumed = stringBuffer.toString().indexOf("\r\n");
          // stringBuffer.setLength(0);
          return 1 + i;
        }


      }
    } else {
      throw new Exception("state in not initialized");
    }
    // stringBuffer.setLength(0);
    return 0;
  }

  private RequestLine parseRequestLine(String requestLineString) throws Exception {
    String[] parts = requestLineString.split(" ");
    // method
    if (parts.length != 3) {
      throw new Exception(" Invalid CRLF");
    }

    String method = parseMethod(parts[0]);
    String requestTarget = parseTarget(parts[1].trim());
    String httpVersion = parseHTTPVersion(parts[2].trim().split("/"));

    // System.out.println("[parseRequestLine]: " + method);
    // System.out.println("[parseRequestLine]: " + requestTarget);
    // System.out.println("[parseRequestLine]: " + httpVersion);
    return new RequestLine(method, requestTarget, httpVersion);
  }

  private String parseMethod(String method) {
    return method.trim();
  }

  private String parseTarget(String target) throws Exception {
    if (!target.startsWith("/")) {
      throw new Exception("Invalid path");
    }
    return target;
  }

  private String parseHTTPVersion(String[] httpParts) throws Exception {
    if (!httpParts[0].equals("HTTP")) {
      throw new Exception("Unsupported Protocol");
    }
    if (!httpParts[1].split(" ")[0].trim().equals("1.1")) {
      throw new Exception("Unsupported HTTP Version: " + httpParts[1]);
    }
    return httpParts[1];
  }

  public RequestLine getRequestLine() {
    return requestLine;
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }

  public byte[] getBody() {
    return body;
  }

  public String getTest() {
    return test;
  }

}
