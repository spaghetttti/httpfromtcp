package org.internal;

import java.io.InputStream;
import java.io.IOException;

public class ChunkReader extends InputStream {
    private String data;
    private int numBytesPerRead;
    private int pos;

    public ChunkReader(String data, int numBytesPerRead) {
        this.data = data;
        this.numBytesPerRead = numBytesPerRead;
        this.pos = 0;
    }

    @Override
    public int read() throws IOException {
        if (pos >= data.length()) {
            return -1;
        }
        return data.charAt(pos++);
    }

    @Override
    public int read(byte[] p) throws IOException {
        if (pos >= data.length()) {
            return -1;
        }
        int endIndex = pos + numBytesPerRead;
        if (endIndex > data.length()) {
            endIndex = data.length();
        }
        int n = 0;
        for (int i = pos; i < endIndex && n < p.length; i++) {
            p[n++] = (byte) data.charAt(i);
        }
        pos += n;
        return n;
    }
}