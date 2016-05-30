package clients;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.orsoncharts.util.json.JSONObject;

import clients.controllers.AdministratorMainController;
import clients.controllers.LoginController;
import clients.controllers.MemberJoinController;
import clients.controllers.PasswordFindController;
import clients.controllers.StudentMain;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tools.NetworkProtocols;
import tools.Statics;

public class SceneManager {
	
	/* 스테이지, 스트림 */
	private Stage stage;
	private Socket socket;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private boolean FULLSCREEN_MODE;
	
	public SceneManager(Stage stage)
	{
		this.stage = stage;
		this.FULLSCREEN_MODE = false;
	}
	
	public Stage getStage()
	{
		return stage;
	}
	
	public int connectToServer()
	{
		try
		{
			socket = new Socket(Statics.SERVER_ID_ADDRESS, 8080);
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource(scene));
		System.out.println(scene);
		System.out.println();
		try
		{
			final Parent root = loader.load();
			if(!Platform.isFxApplicationThread())
			{
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						stage.setResizable(true);
						stage.setScene(new Scene(root));					
						stage.setFullScreen(FULLSCREEN_MODE);
						stage.setResizable(false);
					}
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
		
		/* 화면별 처리할 작업 정의 */
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
		Platform.runLater(new Runnable() {			
			@Override
			public void run() {
				stage.setTitle(Statics.ADMIN_MAIN_TITLE);
			}
		});
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
