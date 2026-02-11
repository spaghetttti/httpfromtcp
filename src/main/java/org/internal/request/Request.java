package org.internal.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Request {
    public RequestLine requestLine;
    public HashMap<String, String> headers;
    public byte[] body;

    public static Request fromReader(InputStream reader) throws IOException {
        return new Request();
    }


    public RequestLine getRequestLine() {
        return requestLine;
    }
}