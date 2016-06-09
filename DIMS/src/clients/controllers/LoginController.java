package clients.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import com.orsoncharts.util.json.JSONObject;

import clients.SceneManager;
import clients.customcontrols.CustomDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import tools.NetworkProtocols;
import tools.Statics;

public class LoginController implements Initializable {
	
	private SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	
	@FXML AnchorPane root;
	@FXML TextField idField;
	@FXML PasswordField passwordField;
	

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void handle(KeyEvent event) {

				if(event.getCode()==KeyCode.ENTER)
				{
					String id = idField.getText();
					String pw = passwordField.getText();
					
					JSONObject request = new JSONObject();
					request.put("type", NetworkProtocols.LOGIN_REQUEST);
					request.put("id", id);
					request.put("password", pw);
					
					sendProtocol(request);
				}
			}
		});
	}

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
		@SuppressWarnings("unchecked")
		@Override
		public void run()
		{
			System.out.println("LoginController ������ ������ ����");
			try
			{
				while(true)
				{
					JSONObject line = null;
					try {
						line = (JSONObject) fromServer.readObject();
						if(line==null)break;
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					
					
					if(line.get("type").equals(NetworkProtocols.LOGIN_ACCEPT))
					{
						if(line.get("user_level").equals("������"))
						{
							sManager.doFullscreen(true);
							sManager.changeListenController("LOGIN_CONTROLLER");
							sManager.changeScene(Statics.ADMIN_MAIN_FXML);
						}
						else
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									sManager.doFullscreen(true);
									sManager.changeListenController("LOGIN_CONTROLLER");
									sManager.changeScene(Statics.STUDENT_MAIN_FXML);
								}
							});
						}
					}
					else if(line.get("type").equals(NetworkProtocols.LOGIN_DENY))
					{
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								CustomDialog.showMessageDialog("���̵� ��й�ȣ�� Ȯ���ϼ���.", sManager.getStage());
							}
						});

					}
					else if(line.get("type").equals(NetworkProtocols.EXIT_RESPOND))
					{
						break;
					}
					else if(line.get("type").equals(NetworkProtocols.RECIEVE_READY))
					{
						JSONObject protocol = new JSONObject();
						protocol.put("type", NetworkProtocols.RECIEVE_READY_OK);
						sendProtocol(protocol);
					}
				}
			}
			catch(IOException e)
			{
				System.out.println("�α׾ƿ� ó���� ������");
			}
			System.out.println("LoginController ������ ����");
		}
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
	
	@SuppressWarnings("unchecked")
	@FXML
	private void onLogin(ActionEvent e)
	{
		String id = idField.getText();
		String pw = passwordField.getText();
		
		JSONObject request = new JSONObject();
		request.put("type", NetworkProtocols.LOGIN_REQUEST);
		request.put("id", id);
		request.put("password", pw);
		
		sendProtocol(request);
	}
	
	@FXML
	private void onFindPassword(ActionEvent e)
	{
		sManager.changeListenController("LOGIN_CONTROLLER");
		sManager.changeScene(Statics.PASSWORD_FIND_FXML);
	}
	
	@FXML
	private void onRegister()
	{
		sManager.changeListenController("LOGIN_CONTROLLER");
		sManager.changeScene(Statics.REGISTER_FXML);
		
	}
	
	
	
}
