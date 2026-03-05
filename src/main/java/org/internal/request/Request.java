package org.internal.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.streamParser.Bytes;

public class Request {
  public RequestLine requestLine;
  public HashMap<String, String> headers;
  public byte[] body;
  public String test;
  private STATE state;
  private final StringBuffer buffer = new StringBuffer();

  private enum STATE {
    DONE,
    INITIALIZED
  }

  public static Request fromReader(InputStream reader) throws Exception, IOException, InterruptedException {
    // var string = Bytes.returnString(reader);
    var request = new Request();
    request.state = STATE.INITIALIZED;
    int data;
    while ((data = reader.read()) != -1 ||request.state != STATE.DONE) {
      System.out.println(data);
      // request.parse(data);
    }
    // my in-house parser in Bytes made \r\n into \r lol
    // System.out.println("make \\n and \\r visible: " + string.replace("\n",
    // "\\n").replace("\r", "\\r"));
    // request.test = string;
    // request.requestLine = request.parseRequestLine(string.split("\r")[0]);
    return request;
  }

  private int parse(byte[] data) throws IOException {

    return 0;
  }

  private RequestLine parseRequestLine(String requestLineString) throws Exception {
    String[] parts = requestLineString.split(" ");
    int bytesConsumed = 0;
    // return 0 if can't find \r\n (or just \r bytesConsumed if haven't fixed Bytes.parse()
    // method)
    if (parts.length != 3) {
      throw new Exception("Invalid CRLF");
    }
    String method = parts[0].trim();
    String requestTarget = parts[1].trim();
    if (!requestTarget.startsWith("/")) {
      throw new Exception("Invalid path");
    }
    String[] httpParts = parts[2].trim().split("/");

    if (!httpParts[0].equals("HTTP")) {
      throw new Exception("Unsupported Protocol");
    }
    if (!httpParts[1].equals("1.1")) {
      throw new Exception("Unsupported HTTP Version");
    }
    System.out.println(method);
    System.out.println(requestTarget);
    System.out.println(httpParts[1]);
    return new RequestLine(method, requestTarget, httpParts[1]);
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
