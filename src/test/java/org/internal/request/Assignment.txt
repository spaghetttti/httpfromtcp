
Parsing a Stream Assignment (Java Version)
Unfortunately parsing code tends to be just one edge case after another. Remember how I said TCP guarantees data to be in order? That's true, but I never said it had to be complete. TCP (and by extension, HTTP) is a streaming protocol, which means we receive data in chunks and should be able to parse it as it comes in.

So, instead of a full HTTP request, we might just get the first few characters, like this:

We need to manage the state of our parser to handle incomplete reads. For example, maybe in the first pass, our parser only gets:

It needs to be smart enough to know that it's not done yet and keep reading until it gets the full request line:

Assignment
Create a ChunkReader class in your test directory that simulates reading a variable number of bytes per read:
Update your test suite to use the ChunkReader type and test for different numbers of bytes read per chunk:
Be sure to test values as low as 1 and as high as the length of the request string. Your code should work under all conditions.

Update your parseRequestLine to return the number of bytes it consumed. If it can't find a \r\n (this is important!) it should return 0 and no error. This just means that it needs more data before it can parse the request line.

Add a new internal enum to your Request class to track the state of the parser. For now, you just need 2 states:

INITIALIZED
DONE
Implement a new int parse(byte[] data) method on your Request class:

It accepts all currently unparsed bytes from the buffer
It updates the "state" of the parser, and the parsed RequestLine field
It returns the number of bytes it consumed (meaning successfully parsed)
It throws an Exception if it encountered an error
Update the fromReader method:

Instead of reading all the bytes and then parsing the request line, it should use a loop to continually read from the reader and parse new chunks using the parse method.
The loop should continue until the parser is in the DONE state.
You'll need to keep track of:
A buffer to read data into (byte[]). Start with a size of 8 and grow it as needed. Shift data in and out of it so you don't need to keep storing already-parsed data.
How many bytes you've read from the reader
How many bytes you've parsed from the buffer
The end result is the same (aside from the fact that it properly handles chunks as they arrive) in that it returns a parsed Request once the reader is exhausted.
Run tests with ./gradlew test.

