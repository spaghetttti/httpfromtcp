# HTTP from TCP

A learning project that implements HTTP protocol parsing from scratch using raw TCP sockets in Java.

## What's Inside

- **TCP Listener** — A basic TCP server that accepts connections and reads raw bytes
- **HTTP Request Parser** — Parses HTTP request lines (method, path, version), headers, and body
- **Stream Parser** — Reads byte streams and breaks them into lines for processing
- **UDP Sender** — Simple UDP client for experimentation

## Running

```bash
# Start the TCP listener
./gradlew runTcp

# Start the UDP sender
./gradlew runUdp
```

## Purpose

Understanding how HTTP works under the hood by building it up from TCP sockets — no high-level HTTP libraries, just raw networking.
