package org.internal.request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class TestRequest {

    @Test
    public void testRequestLineParse() {
        assertEquals("TheTestagen", "TheTestagen");
    }


    // Helper: turn a string into an InputStream (Java's strings.NewReader equivalent)
    private InputStream toInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testGoodGetRequestLine() throws Exception {
        Request r = Request.fromReader(
            toInputStream("GET / HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
        );
        assertNotNull(r);
        assertEquals("GET", r.getRequestLine().getMethod());
        assertEquals("/", r.getRequestLine().getRequestTarget());
        assertEquals("1.1", r.getRequestLine().getHttpVersion());
    }

    @Test
    public void testGoodGetRequestLineWithPath() throws Exception {
        Request r = Request.fromReader(
            toInputStream("GET /coffee HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
        );
        assertNotNull(r);
        assertEquals("GET", r.getRequestLine().getMethod());
        assertEquals("/coffee", r.getRequestLine().getRequestTarget());
        assertEquals("1.1", r.getRequestLine().getHttpVersion());
    }

     @Test
    public void testGoodGetRequestLineWithLongPath() throws Exception {
        Request r = Request.fromReader(
            toInputStream("GET /coffee/brew/cup HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
        );
        assertNotNull(r);
        assertEquals("GET", r.getRequestLine().getMethod());
        assertEquals("/coffee/brew/cup", r.getRequestLine().getRequestTarget());
        assertEquals("1.1", r.getRequestLine().getHttpVersion());
    }

    @Test
    public void testInvalidNumberOfPartsInRequestLine() {
        assertThrows(Exception.class, () -> {
            Request.fromReader(
                toInputStream("/coffee HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
            );
        });
    }

    @Test
    public void testGoodPostRequestLine() throws Exception {
        Request r = Request.fromReader(
            toInputStream("POST / HTTP/1.1\r\nHost: randomUrl:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
        );
        assertNotNull(r);
        assertEquals("POST", r.getRequestLine().getMethod());
        assertEquals("/", r.getRequestLine().getRequestTarget());
        assertEquals("1.1", r.getRequestLine().getHttpVersion());
    }

    @Test
    public void testGoodPostRequestLineWithPath() throws Exception {
        Request r = Request.fromReader(
            toInputStream("POST /home HTTP/1.1\r\nHost: randomUrl:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
        );
        assertNotNull(r);
        assertEquals("POST", r.getRequestLine().getMethod());
        assertEquals("/home", r.getRequestLine().getRequestTarget());
        assertEquals("1.1", r.getRequestLine().getHttpVersion());
    }


    @Test
    public void testInvalidPathInRequestLine() {
        assertThrows(Exception.class, () -> {
            Request.fromReader(
                toInputStream("POST google HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
            );
        });
    }

    @Test
    public void testInvalidMethodNameRequestLine() {
        assertThrows(Exception.class, () -> {
            Request.fromReader(
                toInputStream("READ /stuff HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
            );
        });
    }

    @Test
    public void testInvalidRequestLineCRLF() {
        assertThrows(Exception.class, () -> {
            Request.fromReader(
                toInputStream("POST /books HTTP/1.1Host: localhost:42069\nUser-Agent: curl/7.81.0\rAccept: */*\r\n\r\n")
            );
        });
    }

    @Test
    public void testInvalidHttpVersion() {
        assertThrows(Exception.class, () -> {
            Request.fromReader(
                toInputStream("PUT /stuff HTTP/1.2\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n")
            );
        });
    }
}