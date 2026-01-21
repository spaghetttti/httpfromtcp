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