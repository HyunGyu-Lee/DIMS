package clients.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import clients.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tools.NetworkProtocols;
import tools.Statics;

public class PasswordFindController implements Initializable{

	SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
	public void INIT_CONTROLLER(SceneManager manager, ObjectInputStream fromServer, ObjectOutputStream toServer)
	{
		this.sManager = manager;
		this.fromServer = fromServer;
		this.toServer = toServer;
	}
	
	public void startListener()
	{
		new Listener().start();
	}
	
	class Listener extends Thread
	{
		@Override
		public void run()
		{
			System.out.println("PasswordFindController 리스너 스레드 시작");
			try
			{
				while(true)
				{
					JSONObject respond = null;
					try
					{
						respond = (JSONObject) fromServer.readObject();
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					
					String type = respond.get("type").toString();
					
					if(type.equals(NetworkProtocols.EXIT_RESPOND))
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
			System.out.println("PasswordFindController 리스너 스레드 종료");
		}
	}
	
	@FXML
	public void onCancle(ActionEvent e)
	{
		sManager.changeListenController("PASS_FIND_CONTROLLER");
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
	
}
