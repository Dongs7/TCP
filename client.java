// CMPT 471 Assignment 4 
// Diffie-Hellman Key exchange
// 301008631 Dong-Hyun Chung 


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.*;

public class client
{
	
	static int [] element; //store the data to be sent
	static int eleSize; // store number of elements
	static int port = 5000;
	static int p; // Known prime number for both Client and Server
	static int a; // Known Primitive root for both Client and Server
		
		
	static int Xa; // Client's private number (Xa < P)
	static int Ya; // Client's modified public number (Ya = a^Xa mod p)
	static int Yb; // Server's modified public number 
	static int Kc; // Client Key
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	static void connection() throws Exception, IOException
	{
		Socket cSocket = null;
		BufferedReader datafromServer = null;
		DataOutputStream datatoServer = null;
		
		try{
		boolean check = false;
		System.out.print(getTime() + " Enter the Server IP: ");
		String serverIP = br.readLine();
		
		cSocket = new Socket(serverIP,port);
		
		datafromServer = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
		datatoServer = new DataOutputStream(cSocket.getOutputStream());
		
		
		datatoServer.writeBytes(String.valueOf(p) + '\n'); // Send the prime number to the server
		datatoServer.writeBytes(String.valueOf(a) + '\n'); // Send the primitive root to the server
		datatoServer.writeBytes(String.valueOf(Ya) + '\n'); // Send the Modified public key to the server
		
		
		Yb = Integer.parseInt(datafromServer.readLine()); // Receive server's calculated key from server
		Kc = (int)Math.pow(Yb, Xa)%p; // calculate secret key for client using data from server and client's secret key and prime number
								
		printStatus(p,a,Xa,Ya,Yb,Kc);
		
		
		System.out.print(getTime() + " How many elements do you want to send? :");
		String line="";
		eleSize = checkUserElements(check,line,br,eleSize); // Store number of elements to be sent
		datatoServer.writeBytes(String.valueOf(eleSize) + '\n'); // Send element size to server
		
		element = makeData(element,eleSize,br); // Make data array using the data entered by user
		System.out.println();
		
		beforeEncrypt(element,eleSize); // Show data before encryption
		afterEncrypt(element,eleSize,Kc); // Show data after encryption
		sendEncrpytData(element,eleSize,datatoServer); // Send encrypted data to server
		
		System.out.println();
		System.out.println(getTime() + " Data sent to Server");
		}catch (IOException e){ //Throw error if there are other issues, and close the connection.
	         System.err.println(getTime() + " Accept failed.");
	         System.exit(1);}
		

		datafromServer.close(); //close bufferedReader since there are no more data from server
		cSocket.close(); // close socket
		
	}
	
	// Send encrypted data to server 
	static void sendEncrpytData(int []a , int b, DataOutputStream dos) throws IOException
	{
		for(int i = 0; i < b; i++)
		{
			dos.writeBytes(String.valueOf(a[i]) + '\n');
		}
	}
	
	// Get prime number, primitive root, secret key for client and calculated key for client
	static void setKey() throws Exception
	{
		boolean check = false;
		boolean primeCheck = false;
		
		System.out.print(getTime() + " Please enter the prime number for both Client and Server: ");
		String prime="";
		p=checkUserPrime(primeCheck,prime,br,p);
		
		a = getpRoot(p);
		
		System.out.print(getTime() + " Please enter the secret key for client (less than " +p+ "): ");
		String line="";
		Xa=checkUserPrivate(check,line,br,p,Xa);
		
		System.out.println();
		
		Ya=modifiedPublic(p,a,Xa);
		
	}
	
	//Check if prime number entered by user satisfies the condition, then return the value if satisfied
	static int checkUserPrime(boolean a, String b, BufferedReader c,int d) throws NumberFormatException, IOException
	{
		while(!a)
		{
			while((b =c.readLine()) != null)
			{
				if(b.isEmpty())
				{
					System.out.println(getTime() + " !!: Value not typed");
					System.out.print(getTime() + " Please enter the prime number for both Client and Server: ");
				}
				else
				{
					int temp = Integer.parseInt(b);
					if(checkPrime(temp))
					{
						d = temp;
						a = true;
						break;		
					}
					else
					{
						System.out.println(getTime() + " !!: Value is not a prime number.");
						System.out.print(getTime() + " Please enter the prime number for both Client and Server: ");
					}
				}
			}
		}
		return d;
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
					System.out.print(getTime() + " Please enter the secret key for client (less than " +d+ "): ");
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
						System.out.print(getTime() + " Please enter the secret key for client (less than " +d+ "): ");
					}
				}
			}
		}
		return e;
	}
	
	//Check if element number entered by user satisfies the condition, then return the value if satisfied 
	static int checkUserElements(boolean a, String b, BufferedReader c,int d) throws NumberFormatException, IOException
	{
		while(!a)
		{
			while((b =c.readLine()) != null)
			{
				if(b.isEmpty())
				{
					System.out.println(getTime() + " !!: Value not typed");
					System.out.print(getTime() + " Please enter the number of elements you want to send: ");
				}
				else
				{
					int temp = Integer.parseInt(b);
					if(temp > 0)
					{
						d = temp;
						a = true;
						break;		
					}
				}
			}
		}
		
		return d;
	}
	
	//Check the condition and if it satisfies, then assign array size, store value into array and return the array
	static int [] makeData(int [] a, int b, BufferedReader c) throws NumberFormatException, IOException
	{
		int size = b;
		int count = 0;
		a = new int[size];
		
		while(size!=count)
		{
			System.out.print(getTime() + " Please enter the data to be sent :");
			String line;
			while((line =c.readLine()) != null)
			{
				if(line.isEmpty())
				{
					System.out.println(getTime() + " !!: Data not entered");
					System.out.print(getTime() + " Please enter the data to be sent: ");
				}
				else
				{
					
					int temp = Integer.parseInt(line);
					a[count] = temp;
					count++;
					System.out.println(getTime() + " " + count + " element(s) stored in data queue. "+(size-count)+" element(s) left.");
					System.out.println();
					if (size==count)
						break;
					System.out.print(getTime() + " Please enter the data to be sent: ");
				}
			}
		}
			System.out.println(getTime() + " You have total "+count+(" element(s) in your queue out of "+size+" elements."));
			return a;
	}
	
	// Print array before encryption
	static void beforeEncrypt(int []a, int b)
	{
		int size=b;
		System.out.println("**** Data before Encryption****");
		for(int i = 0; i<size; i++)
		{
			System.out.print(a[i] + " ");
		}
		System.out.println();
		System.out.println();
	}
	
	// Print array after encryption
	static void afterEncrypt(int []a, int b,int c)
	{
		int size=b;
		System.out.println("**** Data after Encryption using shared Key****");
		for(int i = 0; i<size; i++)
		{
			a[i] = (int)Math.pow(a[i]*c,2);
			System.out.print(a[i] + " ");
		}
		System.out.println();
	}
	
	// Check whether the value entered by user is prime.
	public static boolean checkPrime(int a)
	{
		boolean prime_check = true;
		for(int i=2; i<a;i++)
		{
			if(a % i == 0)
				prime_check = false;
		}
		return prime_check;
	}
	
	// get minimum primitive root
	public static int getpRoot(int a)
	{
		
		int min = 50;
		for (int i=1; i<a; i++) {
		
			// See if i is a primitive root.
			if (calpRoot(i, a)) 
			{
				if (min >i)
				 min = i;
			}
		}
		return min;
	}
	
	// Calculation to get primitive value 
	public static boolean calpRoot(int a, int p) 
	{
		
		int num = a, i;
		
		// Calculate  modular exponent a^2, a^3... mod p.
		for (i=2; i<p; i++) {
			num = num * a % p;
			if (num == 1)
				break;
		}
		
		// If the exponent is p-1, return primitive root
		return (i == p-1);
	}
	
	//Calculate new key for client using client's secret key and prime number and primitive root
	public static int modifiedPublic(int p,int a, int X)
	{
		int mPublic = 0;
		mPublic = (int)Math.pow(a,X) % p;
		return mPublic;
	}
	
	
	public static void printStatus(int p,int a,int Xa,int Ya, int Yb, int Kc)
	{
		System.out.println("***********CLIENT SIDE**************");System.out.println();
		System.out.println("****************************************");
		System.out.println("*           PUBLIC KEY - "+p+"             *");
		System.out.println("*   PRIMITIVE ROOT KEY - "+a+"             *");
		System.out.println("*  CLIENT'S PRIVATE KEY - "+Xa+"            *");
		System.out.println("*  CLIENT'S MODIFIED PUBLIC KEY - "+Ya+"    *");
		System.out.println("*  SERVER'S MODIFIED PUBLIC KEY - "+Yb+"    *");
		System.out.println("*  SERVER & CLIENT'S SHARED KEY - "+Kc+"    *");
		System.out.println("****************************************");
	}
	
	
	public static void main(String [] args) throws Exception
	{
		setKey();
		connection();
	}
	
	// getTIme() returns the current time
	static String getTime()
	 {
		 SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		 return f.format(new Date());
	 }
}