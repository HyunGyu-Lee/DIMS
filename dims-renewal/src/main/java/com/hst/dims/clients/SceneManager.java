package com.hst.dims.clients;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import com.hst.dims.clients.controllers.AdministratorMainController;
import com.hst.dims.clients.controllers.LoginController;
import com.hst.dims.clients.controllers.MemberJoinController;
import com.hst.dims.clients.controllers.PasswordFindController;
import com.hst.dims.clients.controllers.StudentMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.hst.dims.tools.NetworkProtocols;
import com.hst.dims.tools.Statics;

public class SceneManager {
	
	/* ��������, ��Ʈ�� */
	private Stage stage;
	private Socket socket;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private boolean FULLSCREEN_MODE;
	private Application clientHost;
	
	public SceneManager(Stage stage)
	{
		this.stage = stage;
		this.FULLSCREEN_MODE = false;
	}
	
	public Stage getStage()
	{
		return stage;
	}
	
	public void setHost(Application host)
	{
		this.clientHost = host;
	}
	
	public Application getHost()
	{
		return clientHost;
	}
	
	public int connectToServer()
	{
		try
		{
			socket = new Socket(Statics.DIMS_SERVER_IP_ADDRESS, Statics.DIMS_SERVER_PORT_NUMBER);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
		}
		catch (UnknownHostException e)
		{
			return Statics.UNKNOWN_HOST_ERROR;
		}
		catch (IOException e)
		{
			return Statics.CONNECT_ERROR;
		}
		
		return Statics.COMMIT;
	}
	
	public void changeScene(String scene)
	{
		URL url;
		try {
			url = new File("build/resources/main/" + scene).toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		FXMLLoader loader = new FXMLLoader(url);
		System.out.println(scene);
		System.out.println();
		try
		{
			final Parent root = loader.load();
			if(!Platform.isFxApplicationThread())
			{
				Platform.runLater(() -> {
					stage.setResizable(true);
					stage.setScene(new Scene(root));
					stage.setFullScreen(FULLSCREEN_MODE);
					stage.setResizable(false);
				});
			}
			else
			{
				stage.setResizable(true);
				stage.setScene(new Scene(root));
				stage.setFullScreen(FULLSCREEN_MODE);
				stage.setResizable(false);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		/* ȭ�麰 ó���� �۾� ���� */
		if(scene.equals(Statics.LOGIN_WINDOW_FXML))
		{
			toLoginWindow(loader);
		}
		else if(scene.equals(Statics.PASSWORD_FIND_FXML))
		{
			toPassFindWindow(loader);
		}
		else if(scene.equals(Statics.ADMIN_MAIN_FXML))
		{
			toAdminMainWindow(loader);
		}
		else if(scene.equals(Statics.REGISTER_FXML))
		{
			toMemberJoinWindow(loader);
		}
		else if(scene.equals(Statics.STUDENT_MAIN_FXML))
		{
			toStudentMain(loader);
		}
	}
	
	

	public void doFullscreen(boolean FULLSCREEN_MODE)
	{
		this.FULLSCREEN_MODE = FULLSCREEN_MODE;
	}
	
	private void toStudentMain(FXMLLoader loader)
	{
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				stage.setTitle(Statics.STUDENT_MAIN_TITLE);
				stage.setX(0);
				stage.setY(0);
			}
		});
		StudentMain control = loader.getController();
		control.INIT_CONTROLLER(this, fromServer, toServer);
		control.startListener();
	}
	
	private void toMemberJoinWindow(FXMLLoader loader)
	{
		stage.setTitle(Statics.REGISTER_TITLE);
		MemberJoinController control = loader.getController();
		control.INIT_CONTROLLER(this, fromServer, toServer);
		control.startListener();
	}

	private void toLoginWindow(FXMLLoader loader)
	{
		stage.setTitle(Statics.LOGIN_WINDOW_TITLE);
		LoginController control = loader.getController();
		control.INIT_CONTROLLER(this, fromServer, toServer);
		control.startListener();
	}
	
	private void toPassFindWindow(FXMLLoader loader)
	{
		stage.setTitle(Statics.PASSWORD_FIND_TITLE);
		PasswordFindController control = loader.getController();
		control.INIT_CONTROLLER(this, fromServer, toServer);
		control.startListener();
	}
	
	private void toAdminMainWindow(FXMLLoader loader)
	{
		Platform.runLater(() -> stage.setTitle(Statics.ADMIN_MAIN_TITLE));
		AdministratorMainController control = loader.getController();
		control.INIT_CONTROLLER(this, fromServer, toServer);
		control.startListener();
	}
	
	@SuppressWarnings("unchecked")
	public void changeListenController(String name)
	{
		JSONObject request = new JSONObject();
		request.put("type", NetworkProtocols.EXIT_REQUEST);
		request.put("name", name);
		try
		{
			toServer.writeObject(request);
			toServer.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
