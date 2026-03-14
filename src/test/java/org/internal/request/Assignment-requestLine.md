
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

TIPS (in golang): 
ips
Implementation help for func (r *Request) parse(data []byte) (int, error):

If the state of the parser is "initialized", it should call parseRequestLine.
If there is an error, it should just return the error.
If zero bytes are parsed, but no error is returned, it should return 0 and nil: it needs more data.
If bytes are consumed successfully, it should update the .RequestLine field and change the state to "done".
If the state of the parser is "done", it should return an error that says something like "error: trying to read data in a done state"
If the state is anything else, it should return an error that says something like "error: unknown state"
Implementation help for RequestFromReader:

It shouldn't call io.ReadAll anymore. Instead, it should create a new buffer: buf := make([]byte, bufferSize, bufferSize). Set bufferSize as a constant at the top of the file, and for now, just a size of 8. We want to test with small buffers to make sure our parser can handle it.
Create a new readToIndex variable and set it to 0. This will keep track of how much data we've read from the io.Reader into the buffer.
Create a new Request struct and set the state to "initialized".
While the state of the parser is not "done":
If the buffer is full (we've read data into the entire buffer), grow it. Create a new slice that's twice the size and copy the old data into the new slice.
Read from the io.Reader into the buffer starting at readToIndex.
If you hit the end of the reader (io.EOF) set the state to "done" and break out of the loop.
Update readToIndex with the number of bytes you actually read
Call r.parse passing the slice of the buffer that has data that you've actually read so far
Remove the data that was parsed successfully from the buffer (this keeps our buffer small and memory efficient). I used the copy function and a new slice to do this.
Decrement the readToIndex by the number of bytes that were parsed so that it matches the new length of the buffer.