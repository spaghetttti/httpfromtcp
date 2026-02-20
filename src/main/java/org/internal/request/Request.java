package org.internal.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.streamParser.Bytes;

public class Request {
    public RequestLine requestLine;
    public HashMap<String, String> headers;
    public byte[] body;
    public static String test;

    public static Request fromReader(InputStream reader) throws IOException, InterruptedException {
        var string = Bytes.returnString(reader);

        test = string;
        System.out.println(string);
        RequestLine requestLine = parseRequestLine(string.split("\r\n")[0]);
        return new Request();
    }

    private static RequestLine parseRequestLine(String requestLineString) {
      String[] parts = requestLineString.split("/");
      String method = parts[0].trim();
      String String = parts[1].trim();
      String httpVersion = parts[2].trim();
      return new RequestLine(method, String, httpVersion);
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