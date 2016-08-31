import java.io.*;
import java.net.*;
import java.util.*;

public class TCPclient {
    public static void main(String[] args) throws IOException {

	String IPentered;
	System.out.print("Please enter the Server IP: ");
	Scanner input = new Scanner(System.in);
	IPentered = input.next();
    System.out.println ("Trying to connect to IP " + IPentered + " on port 5000.");

    Socket echoSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    try
    {
        echoSocket = new Socket (IPentered,5000);
        out = new PrintWriter(echoSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
    } catch (UnknownHostException e) {
        System.err.println("Unknown IP: " + IPentered);
        System.exit(1);
    } catch (IOException e) {
        System.err.println("Couldn't get I/O for " + "the connection to: " + IPentered );
        System.exit(1);
    }

	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	String userInput;
		System.out.println("Type 'exit' to terminate the connection.");
        System.out.print ("[input] ");
	while (!(userInput = stdIn.readLine()).equals("exit"))
	{
	    out.println(userInput);
	    System.out.println("[Server-echo] " + in.readLine());
        System.out.print ("[input] ");
	}

	out.close();
	in.close();
	System.out.println("Server terminates the connection.");
	System.out.println("Connection terminated.");
	stdIn.close();
	echoSocket.close();
    }
}