/* @description: Multi-threaded server. Receives objects from Client
 * @authors: Akif Batur, Nashiha Ahmed
 * @version: 1 (date: 02.08.16)
 */
package tr.com.bilisim.cnc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import tr.com.bilisim.cnc.client.Abc;

public class Server implements Runnable 
{
	Socket nsocket;
	Server(Socket nsocket) 
	{
		this.nsocket = nsocket;
	}

	public static Thread th = null;
	ObjectInputStream inStream = null;
	static Socket socket = null;
	
	public static void main(String[] args) throws IOException 
	{
		ServerSocket listener = new ServerSocket(9090);
		
		System.out.println("listening...");
		try 
		{
			while (true) 
			{
				try 
				{
					socket = listener.accept();
					th = new Thread(new Server(socket));
					th.start();
					
				} 
				catch(Exception e) 
				{
                   e.printStackTrace();
                }
			}
		}
		finally 
		{
            listener.close();
        }
	}

	@SuppressWarnings("deprecation")
	public void run() 
	{
		try 
		{
			inStream = new ObjectInputStream( socket.getInputStream());
			Abc abc = (Abc) inStream.readObject();
			System.out.println("New object received = " + abc.getName());
		} 
		catch (IOException e) 
		{
			System.out.println(e);
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			th.stop();
			try 
			{
				nsocket.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
