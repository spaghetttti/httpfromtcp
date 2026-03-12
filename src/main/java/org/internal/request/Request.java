package org.internal.request;

import  java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

public class Request {
  public RequestLine requestLine;
  public HashMap<String, String> headers;
  public byte[] body;
  public String test;
  private STATE state;
  private static final StringBuilder stringBuffer = new StringBuilder();

  private enum STATE {
    DONE,
    INITIALIZED
  }

  public static Request fromReader(InputStream reader) throws Exception, IOException, InterruptedException {
    // var string = Bytes.returnString(reader);
    var request = new Request();
    request.state = STATE.INITIALIZED;
    final int initialBufferSize = 8;
    byte[] buffer = new byte[8];
    byte[] totalBuffer = new byte[initialBufferSize];
    int pos = 0;

    while (request.state != STATE.DONE) {
      pos = reader.read(buffer);
      // System.out.println("pos: " + pos);
      // System.out.println("can i even read the stream?: " + (char) pos);
      if (pos != -1) {
        for (int i = 0; i < pos; i++) {
          System.out.println((char) buffer[i]);
        }
        int consumedBytes = request.parse(Arrays.copyOfRange(buffer, 0, pos));
        if (consumedBytes == 0) { 
          totalBuffer = new byte[totalBuffer.length * 2];
          System.arraycopy(buffer, 0, totalBuffer, 0, buffer.length);
        } else {
          System.arraycopy(buffer, 0, totalBuffer, 0, consumedBytes);
        }
      } else {
        break;
      }
    }

    request.parseRequestLine(stringBuffer.toString());


    return request;
  }

  private int parse(byte[] data) throws IOException {
    int bytesConsumed = 0;
    for (byte bd: data) {
      // System.out.println("step 1 " + bd);
      // System.out.println("step 2 " + (char) bd);
      stringBuffer.append((char) bd);
      System.out.println(stringBuffer.toString());
      bytesConsumed++;
    }
    if (stringBuffer.toString().contains("\r\n")) {
      System.out.println("DELETE");
      state = STATE.DONE;
      // stringBuffer.setLength(0);
      return 0;
    }

    return bytesConsumed;
  }

  private RequestLine parseRequestLine(String requestLineString) throws Exception {
    String[] parts = requestLineString.split(" ");
    // method)
    if (parts.length != 3) {
      throw new Exception("Invalid CRLF");
    }

    String method = parseMethod(parts[0]);
    String requestTarget = parseTarget(parts[1].trim());
    String httpVersion = parseHTTPVersion(parts[2].trim().split("/"));
    
    System.out.println(method);
    System.out.println(requestTarget);
    System.out.println(httpVersion);
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
    if (!httpParts[1].equals("1.1")) {
      throw new Exception("Unsupported HTTP Version" + httpParts[1]);
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
