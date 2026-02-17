package org.internal.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.streamParser.Bytes;

public class Request {
    public RequestLine requestLine;
    public HashMap<String, String> headers;
    public byte[] body;

    public static Request fromReader(InputStream reader) throws IOException, InterruptedException {
        var string = Bytes.returnString(reader);
        System.out.println(string);
        return new Request();
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
}