# Parsing a Stream (Java version, converted from Go)

Unfortunately parsing code tends to be just one edge case after another. Remember how we said TCP guarantees data to be in order? That's true, but we never said it had to be complete. TCP (and by extension, HTTP) is a streaming protocol, which means we receive data in chunks and should be able to parse it as it comes in.

So, instead of a full HTTP request, we might just get the first few characters, like:

```
GE
```

We need to manage the state of our parser to handle incomplete reads. For example, maybe in the first pass, our parser only gets:

```
GE
```

It needs to be smart enough to know that it's not done yet and keep reading until it gets the full request line:

```
GET /coffee HTTP/1.1
```

---

## Assignment

### 1. ChunkReader (provided)

Use the provided `ChunkReader` in your test directory. It extends `InputStream` and overrides `read(byte[] b)` so that each call reads at most `numBytesPerRead` bytes, simulating a variable number of bytes per read from a network connection. You will use **only** `read(byte[] b)` (the 1-arg form) in your solution, so it works correctly with this reader.

Reference implementation (already in the project):

```java
// ChunkReader.read(byte[] p) — reads up to numBytesPerRead bytes per call
@Override
public int read(byte[] p) throws IOException {
    if (pos >= data.length()) {
        return -1;
    }
    int endIndex = Math.min(pos + numBytesPerRead, data.length());
    int n = 0;
    for (int i = pos; i < endIndex && n < p.length; i++) {
        p[n++] = (byte) data.charAt(i);
    }
    pos += n;
    return n;
}
```

### 2. Tests

Update your test suite to use `ChunkReader` and test different chunk sizes:

- **Good GET request line** — e.g. `numBytesPerRead: 3`
- **Good GET request line with path** — e.g. `numBytesPerRead: 1`

Test with values as low as 1 and as high as the length of the request string. Your code should work under all conditions.

Example test pattern:

```java
ChunkReader reader = new ChunkReader(
    "GET / HTTP/1.1\r\nHost: localhost:42069\r\nUser-Agent: curl/7.81.0\r\nAccept: */*\r\n\r\n",
    3);
Request r = Request.fromReader(reader);
assertNotNull(r);
assertEquals("GET", r.getRequestLine().getMethod());
assertEquals("/", r.getRequestLine().getRequestTarget());
assertEquals("1.1", r.getRequestLine().getHttpVersion());
```

### 3. parseRequestLine

Update `parseRequestLine` so that it **returns the number of bytes it consumed** (as an `int`). If it cannot find `\r\n`, it should return **0** and **not** throw. Returning 0 means "I need more data before I can parse the request line."

So you need a way to try parsing from a byte slice: e.g. a method that takes the current buffer (or a string built from it), looks for `\r\n`, and returns either (bytes consumed, including the `\r\n`) or 0 if no `\r\n` is present.

### 4. Parser state

Add an internal enum (or int constants) to your `Request` class to track the state of the parser. For now you need only two states:

- **INITIALIZED**
- **DONE**

### 5. parse(byte[] data)

Implement a method that:

- Accepts **all currently unparsed bytes** (the slice of your buffer that has data).
- Updates the parser **state** and the parsed **RequestLine** field when it successfully parses a line.
- **Returns** the number of bytes it consumed (successfully parsed).
- **Throws** an exception if it encounters an error.

See the Tips section below for detailed behavior.

### 6. fromReader(InputStream reader)

Update `fromReader` so that it:

- Does **not** read the entire stream into one big byte array or string first.
- Uses a **loop** that repeatedly reads from the reader and calls `parse` with new data.
- Continues until the parser is in the **DONE** state.

You must keep track of:

1. **A buffer (`byte[]`)** to hold unparsed data. Start with length 8 and grow it as needed. Shift data so you don’t keep already-parsed bytes in the buffer.
2. **How many bytes are valid in the buffer** — e.g. a variable like `readToIndex`: "I have read this many bytes from the reader into the buffer so far" (and after shifting, "this many bytes of unparsed data remain in the buffer").

Because the provided `ChunkReader` only overrides `read(byte[] b)` (it writes from index 0), use this pattern:

- Allocate a **main buffer** for unparsed data (start size 8).
- When you need more data: read into a **temporary byte array** using `reader.read(byte[] b)` (e.g. size 8 or the remaining space in your main buffer). Then **append** those bytes into your main buffer (e.g. copy the temp bytes into the main buffer at position `readToIndex`). Then increase `readToIndex` by the number of bytes you just read.
- When the main buffer is full before you have parsed a line, **grow** it (e.g. double the size, copy old data into the new array).
- Call `parse` with the **slice** of the main buffer that contains valid data (from 0 to `readToIndex`).
- When `parse` returns a positive number of consumed bytes: **remove** those bytes from the front of the buffer (e.g. copy `buffer[consumed .. readToIndex]` to `buffer[0 ..]`) and decrease `readToIndex` by that amount.

The end result is the same as before: you return a parsed `Request` (with at least the request line set) once the reader is exhausted and the parser is done.

Run tests with `./gradlew test`.

---

## Tips

### parse(byte[] data)

- If the state is **INITIALIZED**, try to parse the request line from `data` (look for `\r\n`).
  - If there is an error (invalid format), throw.
  - If there is no `\r\n` yet, return **0** and do not throw (need more data).
  - If you successfully parse a line, set the `RequestLine` field, set state to **DONE**, and return the number of bytes consumed (including `\r\n`).
- If the state is **DONE**, throw an error such as `"error: trying to read data in a done state"`.
- For any other state, throw something like `"error: unknown state"`.

### fromReader

- Do not use "read everything" (e.g. a helper that reads the whole stream into one array). Instead:
  1. Create a buffer: `byte[] buffer = new byte[BUFFER_SIZE];` with `BUFFER_SIZE = 8` (as a constant).
  2. Use a variable `readToIndex = 0` (how many bytes of valid, unparsed data are in the buffer).
  3. Create a `Request` and set its state to **INITIALIZED**.
  4. **While** the parser state is not **DONE**:
     - If the buffer is full (`readToIndex == buffer.length`), grow it: new array of twice the size, copy `buffer[0..readToIndex]` into it, replace `buffer`.
     - Read from the reader using **`read(byte[] b)`**: read into a temporary array, then copy those bytes into your main buffer at `readToIndex`, and add the number of bytes read to `readToIndex`. If `read` returns `-1` (EOF), set state to **DONE** and break.
     - Call `parse` with the slice of the buffer that has data (e.g. `Arrays.copyOfRange(buffer, 0, readToIndex)` or pass buffer and length).
     - If `parse` returns `consumed > 0`: remove the consumed bytes from the front (e.g. `System.arraycopy(buffer, consumed, buffer, 0, readToIndex - consumed)`), then set `readToIndex -= consumed`.

This keeps the buffer small and avoids storing already-parsed data.
