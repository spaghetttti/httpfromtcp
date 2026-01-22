UDP Sender Assignment
So we've seen that a TCP sender (in our case nc, or netcat) requires a connection to be established before data can be sent. Let's build our own UDP sender to see how it differs.

Assignment

Create a new program that yeets UDP packets at a server. We want to fully understand the difference between TCP and UDP.

Create a new UDP.java in a new org/cmd/udpsender directory that can be run with ./gradlew runUdp (you'll need to configure this task)
The program should start by using InetAddress.getByName to resolve the address localhost and create an InetSocketAddress with port 42069
Use DatagramSocket to prepare a UDP connection, and ensure the socket is closed properly
Create a new BufferedReader that reads from System.in
Start an infinite loop that:
Prints a > character to the console (to indicate that the program is ready for user input)
Reads a line from the BufferedReader using readLine(), and log any errors
Writes the line to the UDP connection using DatagramPacket and socket.send(), and log any errors
In a separate terminal, run nc -u -l 42069 to listen for UDP packets and log them to the console.
Use ./gradlew runUdp (or java -cp ...) to run your program. Type in some messages and hit enter. You should see the messages appear in the netcat terminal.
Turn off the netcat listener by hitting ctrl+c in the terminal.
Restart your program, and try to send a message. You should not see any errors, because UDP doesn't care if the receiver is listening or not!
However, your OS might yeet you like UDP yeets packets.

========================================================================================================================================
wait, so is it like this : the datagramSocket (UDP server) is created to be running on a specific port (42070) , when runing our program and reading system.in stream this UDP server will send the streams to an address port (42069) (receiver), and no matter if we're listening with nc -u -l 42069 or not , the streams will still arrive to the destination ?
if so checkout my change with :
var hostAddress = InetAddress.getByName("localhost");
var inetSocket = new InetSocketAddress(hostAddress, 42069);
var dataSocket = new DatagramSocket(42070);
dataSocket.connect(inetSocket);
, but im still getting following stuff
➜ java git:(main) ✗ java org.cmd.udpsender.UDP

lol

this

this

working

fr

fr

(and here i stoped the listener in the other terminal)
and without a listener

working still??
Exception in thread "main" java.net.PortUnreachableException
at java.base/sun.nio.ch.DatagramDispatcher.write0(Native Method)
at java.base/sun.nio.ch.DatagramDispatcher.write(DatagramDispatcher.java:51)
at java.base/sun.nio.ch.IOUtil.writeFromNativeBuffer(IOUtil.java:137)
at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:81)
at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:58)
at java.base/sun.nio.ch.DatagramChannelImpl.send(DatagramChannelImpl.java:865)
at java.base/sun.nio.ch.DatagramChannelImpl.blockingSend(DatagramChannelImpl.java:952)
at java.base/sun.nio.ch.DatagramSocketAdaptor.send(DatagramSocketAdaptor.java:193)
at java.base/java.net.DatagramSocket.send(DatagramSocket.java:662)
at org.cmd.udpsender.UDP.createConnection(UDP.java:41)
at org.cmd.udpsender.UDP.main(UDP.java:15)

You're on the right track, but let me clarify your understanding:

Terminology Fix
DatagramSocket isn't really a "UDP server" - it's just a UDP socket. Unlike TCP, UDP doesn't have a clear server/client distinction. A DatagramSocket can both send and receive.

What's Happening
Your socket binds to port 42070 (your local port)
You're trying to send to port 42069 (the destination)
When no listener exists, packets are sent into the void ✨
The Problem: That connect() Call
You're still getting PortUnreachableException because of line 26!

When you call dataSocket.connect(inetSocket), you're enabling your OS to send you ICMP error notifications. The assignment says:

"You should not see any errors, because UDP doesn't care if the receiver is listening or not!"

Hint: What if you DON'T call connect() at all?

Instead, when creating your DatagramPacket on line 40, you can specify the destination address directly. Look at the DatagramPacket constructors - there's one that takes the data, length, AND the destination address.

Try removing the connect() call and passing the destination to the packet constructor instead. Then UDP will truly not care! 🚀