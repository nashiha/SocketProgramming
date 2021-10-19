/* @description: Object for Server
 * @authors: Akif Batur, Nashiha Ahmed
 * @version: 1 (date: 02.08.16)
 */
package tr.com.bilisim.cnc.client;

import java.io.Serializable; 

public class Abc implements Serializable
{
	private static final long serialVersionUID = 1L; 
	private String name = null;
	
	public Abc(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
}
