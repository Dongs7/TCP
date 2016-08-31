import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class TCPserver
{
 public static void main(String[] args) throws IOException
 {
	 ServerSocket server = null;
     int port = 5000; // set Port number
     try {
         server = new ServerSocket(port); //Initiate server socket with port number 5000, then wait and listen on the port.
         } catch (IOException e){ //If error occurs, throw error and exit the program.
         System.err.println(getTime() + " listen failed on port: "+ port);
         System.exit(1);}

     Socket client = null;
     System.out.println (getTime() + " Waiting for connection.....");
     server.setSoTimeout(15000); // set Socket timer to 15seconds. Server will close the connection if no client try connect within 15seconds.
     try {
    		client = server.accept();
         } catch (SocketTimeoutException e){ // Throw error when the timer is up and close the connection.
         System.err.println(getTime() + " Socket Time out.");
         System.exit(1);
         } catch (IOException e){ //Throw error if there are other issues, and close the connection.
         System.err.println(getTime() + " Accept failed.");
         System.exit(1);}

     System.out.println (getTime() + " Connection established with IP " + client.getInetAddress() + " On Port " + server.getLocalPort());
     System.out.println (getTime() + " Waiting for input from client.....");

     PrintWriter out = new PrintWriter(client.getOutputStream(),true); //Initiate PrintWriter using the connection with the client
     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream())); //Initiate BufferedReader using the connection with the client

     String inputLine;

     while ((inputLine = in.readLine()) != null){
         System.out.println (getTime() + " [client-msg] " + inputLine); // print what client sent
         	out.println(inputLine);  // send exactly the same msg what server receives to the client

         if (inputLine.equals("exit")) //if client sends 'exit', then exit while loop.
             break;}

     out.close(); // close PrintWriter
     in.close();  // close BufferedReader
     System.out.println(getTime() + " Connection terminated.");
     client.close(); // close client socket
     server.close(); // close server socket
 }

 // getTIme() returns the current time
 static String getTime()
 {
	 SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
	 return f.format(new Date());
 }

} // End class