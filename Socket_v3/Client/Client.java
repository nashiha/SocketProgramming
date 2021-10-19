/* @description: Client. Sends objects to server
 * @authors: Akif Batur, Nashiha Ahmed
 * @version: 1 (date: 02.08.16)
 */
package tr.com.bilisim.cnc.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.io.ObjectOutputStream;

public class Client
{
	
    public static void main(String[] args) throws IOException 
    {
		ObjectOutputStream outputStream = null;
		
        while(true)
		{
			Socket s = new Socket("10.9.9.128", 9090);
			outputStream = new ObjectOutputStream( s.getOutputStream());
    		Abc abc = new Abc("Nashiha");
    		System.out.println( "Sending new object: "+abc.getName());
    		outputStream.writeObject(abc);
    		System.out.println("Object sent: " + abc.getName());
    		s.close();
			try
			{
				TimeUnit.SECONDS.sleep(2);
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
    }
}
