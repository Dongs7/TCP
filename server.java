// CMPT 471 Assignment 4 
// Diffie-Hellman Key exchange
// 301008631 Dong-Hyun Chung 


import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class server
{
	static int [] rcvElements; // Array to store elements from Client
	static int p; // Known prime number for both Client and Server
	static int a; // Known Primitive root for both Client and Server
			
	static int Xb; // Server's private number
	static int Yb; // Server's modified public number = (a^Xb mod p)
	static int Ya; // Client's modified public number
	static int Ks; // Server Key
	static int rcvSize; // Store number of elements from Client
	
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
	static void connection() throws Exception, IOException
	{
		ServerSocket sSocket = null;
	    int port = 5000; // set Port number
	    
	    try {
	    	sSocket = new ServerSocket(port); //Initiate server socket with port number 5000, then wait and listen on the port.
	         } catch (IOException e){ //If error occurs, throw error and exit the program.
	         System.err.println(getTime() + " listen failed on port: "+ port);
	         System.exit(1);}

	     Socket cSocket = null;
	     System.out.println (getTime() + " Waiting for connection.....");
	     
	    //server.setSoTimeout(15000); // set Socket timer to 15seconds. Server will close the connection if no client try connect within 15seconds.
	     try {
	    	 boolean check = false;
	    	 cSocket = sSocket.accept();
	    	 BufferedReader datafromClient = new BufferedReader(new InputStreamReader(cSocket.getInputStream())); //Initiate BufferedReader using the connection with the client
	   	     DataOutputStream datatoClient = new DataOutputStream(cSocket.getOutputStream());
	   	     
	   	     //receive keys from client
	    	 p = Integer.parseInt(datafromClient.readLine()); // prime number
	   	     a = Integer.parseInt(datafromClient.readLine()); // primitive root
	   	     Ya =Integer.parseInt(datafromClient.readLine()); // calculated key from client
	   	     

	   	     System.out.print(getTime() + " Please enter the secret key for server (less than " +p+ "): ");
			 String line="";
			 Xb=checkUserPrivate(check,line,br,p,Xb); // secret key for server
			 
			 System.out.println();
	   	     Yb = modifiedPublic(p,a,Xb); // calculated key for server
	   	     
	   	     datatoClient.writeBytes(String.valueOf(Yb) + '\n'); //Send calculated key to client
	   	     	   	     
	   	     Ks = (int)Math.pow(Ya, Xb) % p; // Calculate shared key for server
	   	     
	   	     printStatus(p,a,Xb,Yb,Ya,Ks);
	   	     
	   	     System.out.println (getTime() + " Connection established with IP " + cSocket.getInetAddress() + " On Port " + cSocket.getPort());
		     System.out.println (getTime() + " Waiting for input from client.....");
		     System.out.println();
	   	     
		     
	   	     rcvSize=Integer.parseInt(datafromClient.readLine()); // Receive number of elements from client
		     rcvElements= encrpytedDatafromClient(rcvElements,rcvSize,datafromClient); // Receive and store elements from client
		     
		     beforeDecrypt(rcvElements,rcvSize); 
		     afterDecrypt(rcvElements, rcvSize, Ks);
		     
		     System.out.println();
		     System.out.println(getTime() + " Data received successfully");
		     
		     datafromClient.close(); //close bufferedReader since there are no more data from client
		     
	         }catch (IOException e){ //Throw error if there are other issues, and close the connection.
	         System.err.println(getTime() + " Accept failed.");
	         System.exit(1);}
	     	
	     System.out.println(getTime() + " Connection terminated.");
	     cSocket.close(); // close client socket
	     sSocket.close(); // close server socket
	}
	
	//Check if private number entered by user satisfies the condition, then return the value if satisfied
	static int checkUserPrivate(boolean a, String b, BufferedReader c,int d,int e) throws NumberFormatException, IOException
	{
		while(!a)
		{
			while((b =c.readLine()) != null)
			{
				if(b.isEmpty())
				{
					System.out.println(getTime() + " !!: Value not typed");
					System.out.print(getTime() + " Please enter the secret key for server (less than " +d+ "): ");
				}
				else
				{
					int temp = Integer.parseInt(b);
					if(temp < p)
					{
						e = temp;
						a = true;
						break;		
					}
					else
					{
						System.out.println(getTime() + " !!: Value is greater than " + p);
						System.out.print(getTime() + " Please enter the secret key for server (less than " +d+ "): ");
					}
				}
			}
		}
		return e;
	}
	
	//Get encrypted data from client and store them into array a and return the array.
    static int [] encrpytedDatafromClient(int []a, int b, BufferedReader c) throws NumberFormatException, IOException
	{
		int size = b;
		a = new int[size];
		
		for(int i=0; i < size; i++)
		{
			a[i] = Integer.parseInt(c.readLine());
		}
		return a;
	}
	
    //Show data before decryption
	static void beforeDecrypt(int [] a, int b)
	{
		int size=b;
		System.out.println("**** Data before Decryption****");
		for(int i = 0; i < size; i++)
		{
			System.out.print(a[i] + " ");
		}
		System.out.println();
		System.out.println();
	}
	
	//Show data after decryption
	static void afterDecrypt(int [] a, int b, int c)
	{
		int size=b;
		System.out.println("**** Data after Decryption using shared key****");
		for(int i = 0; i < size; i++)
		{
			a[i] = (int)Math.sqrt(a[i]) / c;
			System.out.print(a[i] + " ");
		}
		System.out.println();
		System.out.println();
	}
	
	//Calculate new key for server using server's secret key and prime number and primitive root
	public static int modifiedPublic(int p,int a, int X)
	{
		int mPublic = 0;
		mPublic = (int)Math.pow(a,X) % p;
		return mPublic;
	}
	
	//Show status 
	public static void printStatus(int p,int a,int Xb,int Yb,int Ya,int Ks)
	{
		System.out.println("***********SERVER SIDE**************");System.out.println();
		System.out.println("****************************************");
		System.out.println("*           PUBLIC KEY - "+p+"             *");
		System.out.println("*   PRIMITIVE ROOT KEY - "+a+"             *");
		System.out.println("*  SERVER'S PRIVATE KEY - "+Xb+"            *");
		System.out.println("*  SERVER'S MODIFIED PUBLIC KEY - "+Yb+"    *");
		System.out.println("*  CLIENT'S MODIFIED PUBLIC KEY - "+Ya+"    *");
		System.out.println("*  SERVER & CLIENT'S SHARED KEY - "+Ks+"    *");
		System.out.println("****************************************");
	}
	public static void main(String [] args) throws Exception, NumberFormatException
	{
		
		connection();
		
	}
	// getTIme() returns the current time
	 static String getTime()
	 {
		 SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		 return f.format(new Date());
	 }
}