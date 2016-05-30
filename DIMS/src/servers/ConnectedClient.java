package servers;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.orsoncharts.util.json.JSONObject;

public class ConnectedClient {

	private ObjectOutputStream outter;
	private String clientID;
	private String clientName;
	private String clientGrade;
	
	public ConnectedClient(String clientID, String clientName, String clientGrade, ObjectOutputStream outter)
	{
		this.clientID = clientID;
		this.clientName = clientName;
		this.clientGrade = clientGrade;
		this.outter = outter;
	}
	
	public String getClientGrade()
	{
		return clientGrade;
	}
	
	public String getClientID()
	{
		return clientID;
	}
	
	public String getClientName()
	{
		return clientName;
	}
	
	public void send(JSONObject obj)
	{
		try
		{
			synchronized (outter)
			{
				outter.writeObject(obj);
				outter.flush();				
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
