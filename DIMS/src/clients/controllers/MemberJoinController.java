package clients.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import clients.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tools.NetworkProtocols;
import tools.Statics;

public class MemberJoinController implements Initializable  {

	private SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;

	@FXML TextField idField;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		
	}
	
	public void startListener()
	{
		new Listener().start();
	}
	
	public void INIT_CONTROLLER(SceneManager manager, ObjectInputStream fromServer, ObjectOutputStream toServer)
	{
		this.sManager = manager;
		this.fromServer = fromServer;
		this.toServer = toServer;
	}
	
	@SuppressWarnings("unchecked")
	@FXML
	private void onDuplicate()
	{
		JSONObject request = new JSONObject();
		request.put("type", NetworkProtocols.ID_DUP_CHECK_REQUEST);
		request.put("id", idField.getText());
		sendProtocol(request);
	}
	
	@FXML
	private void onCancle()
	{
		sManager.changeListenController("MEMBER_JOIN");
		sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
	}
	
	public void sendProtocol(JSONObject protocol)
	{
		try
		{
			toServer.writeObject(protocol);
			toServer.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	class Listener extends Thread
	{
		@Override
		public void run()
		{
			System.out.println("MemberJoin 리스너 스레드 시작");
			
			try
			{
				while(true)
				{
					JSONObject respond = null;
					try
					{
						respond = (JSONObject)fromServer.readObject();
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					
					String type = respond.get("type").toString();
					
					if(type.equals(NetworkProtocols.ID_DUP_RESPOND_OK))
					{
						System.out.println("아이디생성가능");
					}
					else if(type.equals(NetworkProtocols.ID_DUP_RESPOND_DENY))
					{
						System.out.println("중복");
					}
					else if(type.equals(NetworkProtocols.EXIT_RESPOND))
					{
						break;
					}
					else if(type.equals(NetworkProtocols.RECIEVE_READY))
					{
						JSONObject protocol = new JSONObject();
						protocol.put("type", NetworkProtocols.RECIEVE_READY_OK);
						sendProtocol(protocol);
					}
					
					
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			System.out.println("MemberJoin 리스너 스레드 종료");
		}
	}
	
	
	
}
