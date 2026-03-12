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
  private static final StringBuffer stringBuffer = new StringBuffer();

  private enum STATE {
    DONE,
    INITIALIZED
  }

  public static Request fromReader(InputStream reader) throws Exception, IOException, InterruptedException {
    // var string = Bytes.returnString(reader);
    var request = new Request();
    request.state = STATE.INITIALIZED;
    final int initialBufferSize = 8;
    byte[] buffer = new byte[initialBufferSize];
    int pos = 0;

    while ((pos = reader.read(buffer)) == -1 || request.state != STATE.DONE) {
      System.out.println("pos: " + pos);
      System.out.println("can i even read the stream?: " + (char) reader.read());
      if (pos != -1) {
        int consumedBytes = request.parse(Arrays.copyOfRange(buffer, 0, pos));
        if (consumedBytes == 0) { 
          buffer = new byte[initialBufferSize * 2];
        } else {
          request.parseRequestLine(stringBuffer.toString());
        }
      }
    }

    return request;
  }

  private int parse(byte[] data) throws IOException {
    int bytesConsumed = 0;
    int initialSize = stringBuffer.length();
    // return 0 if can't find \r\n (or just \r bytesConsumed if haven't fixed Bytes.parse()
    for (byte bd: data) {
      System.out.println(bd);
      System.out.println((char) bd);
      stringBuffer.append((char) bd);
      System.out.println(stringBuffer.toString());
      bytesConsumed++;
    }

    if (!stringBuffer.toString().contains("\\r\\n")) {
      stringBuffer.delete(initialSize, bytesConsumed);
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
      throw new Exception("Unsupported HTTP Version");
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
