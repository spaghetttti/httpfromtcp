package org.internal.request;

import java.io.IOException;
import java.io.InputStream;
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

  public static Request fromReader(InputStream reader) throws Exception, IOException, InterruptedException {
    // var string = Bytes.returnString(reader);
    var request = new Request();
    request.state = STATE.INITIALIZED;
    byte[] buffer = new byte[8];
    int position = 0;
    int readFromIndex = 0;
    int consumedBytes = 0;
    int counter = 0;

    while (request.state != STATE.DONE) {
      System.out.println("read counter: " + counter++); 
      position = reader.read(buffer, readFromIndex, buffer.length - readFromIndex);
      readFromIndex += position;
      System.out.println("[position]: " + position);
      System.out.println("[readFromIndex]: " + readFromIndex);
      if (position != -1) {
        System.out.println("[consumedBytes] pre: " + consumedBytes);
        consumedBytes = request.parse(buffer, request);
        System.out.println("[consumedBytes] post: " + consumedBytes);
        if (consumedBytes != 0) {
          System.arraycopy(new byte[consumedBytes], 0, buffer, 0, consumedBytes);
        } else if (readFromIndex + consumedBytes >= buffer.length - 1 ) {
          System.out.println("[buffer.length]: " + buffer.length);
          var tempBuffer = new byte[buffer.length * 2];
          System.out.println("[tempBuffer.length]: " + tempBuffer.length);
          System.arraycopy(buffer, 0, tempBuffer, 0, buffer.length);
          buffer = tempBuffer;
          System.out.println("[buffer.length]: " + buffer.length);
        }
      } else {
        break;
      }
    }

    
    // request.requestLine = request.parseRequestLine(stringBuffer.toString().split("\r\n")[0]);
    System.out.println("request.requestLine" + request.requestLine.getMethod());

    return request;
  }

  private int parse(byte[] data, Request request) throws Exception {
    var stringBuffer = new StringBuffer();
    int bytesConsumed = 0;
    for (byte bd: data) {
      stringBuffer.append((char) bd);
      System.out.println(stringBuffer.toString());
      bytesConsumed++;
    }
    if (stringBuffer.toString().contains("\r\n")) {
      state = STATE.DONE;
      request.requestLine = parseRequestLine(stringBuffer.toString().split("\r\n")[0]);
      stringBuffer.setLength(0);
      return bytesConsumed;
    }
    stringBuffer.setLength(0);
    return 0;
  }

  private RequestLine parseRequestLine(String requestLineString) throws Exception {
    String[] parts = requestLineString.split(" ");
    // method
    if (parts.length != 3) {
      throw new Exception("Invalid CRLF");
    }

    String method = parseMethod(parts[0]);
    String requestTarget = parseTarget(parts[1].trim());
    String httpVersion = parseHTTPVersion(parts[2].trim().split("/"));
    
    System.out.println("[parseRequestLine]: " + method);
    System.out.println("[parseRequestLine]: " + requestTarget);
    System.out.println("[parseRequestLine]: " + httpVersion);
    return new RequestLine(method, requestTarget, httpVersion);
  }

  private String parseMethod(String method) {
    return method.trim();
  }

  private String parseTarget(String target)throws Exception {
    if (!target.startsWith("/")) {
      throw new Exception("Invalid path");
    }
    return target;
  }

  private String parseHTTPVersion(String[] httpParts) throws Exception {
    if (!httpParts[0].equals("HTTP")) {
      throw new Exception("Unsupported Protocol");
    }
    System.out.println("wtf: " + httpParts[1] + " / " + httpParts[1].equals("1.1"));
    if (!httpParts[1].split(" ")[0].trim().equals("1.1")) {
      throw new Exception("Unsupported HTTP Version: " + httpParts[1]);
    }
    return httpParts[1];
  }

  public RequestLine getRequestLine() {
    System.out.println("[getRequestLine] " + requestLine);
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
