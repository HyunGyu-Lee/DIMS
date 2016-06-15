package clients.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;

import clients.SceneManager;
import clients.customcontrols.CustomDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tools.NetworkProtocols;
import tools.Statics;
import tools.Toolbox;

public class PasswordFindController implements Initializable{

	SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	
	@FXML TextField findId, findA, newPass, newPassConfirm;
	@FXML ComboBox<String> findQ;
	
	@FXML AnchorPane newPassBox;
	
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
		@SuppressWarnings("unchecked")
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
						System.out.println(respond.toJSONString());
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
						protocol.put("request-view", "passwordFind");
						sendProtocol(protocol);
					}
					else if(type.equals(NetworkProtocols.PASSWORD_FIND_QUESTION_LIST))
					{
						JSONArray data = (JSONArray)respond.get("data");
						Platform.runLater(()->{
							for(Object o : data)
							{
								findQ.getItems().add(o.toString());
							}
						});
					}
					else if(type.equals(NetworkProtocols.PASSWORD_FIND_IDENTIFY_RESPOND))
					{
						//respond.put("identify-result", "commit");respond.put("identify-result", "fault");
						if(respond.get("identify-result").equals("commit"))
						{
							Platform.runLater(()->{
								findId.setEditable(false);
								CustomDialog.showMessageDialog("인증됐습니다. 새 비밀번호를 설정하세요.", sManager.getStage());
								newPassBox.setDisable(false);
							});
						}
						else
						{
							Platform.runLater(()->{
								CustomDialog.showMessageDialog("인증정보를 확인하세요", sManager.getStage());
							});
						}
					}
					else if(type.equals(NetworkProtocols.PASSWORD_FIND_MODIFY_RESPOND))
					{
						Platform.runLater(()->{
							CustomDialog.showMessageDialog("변경완료!", sManager.getStage());
							sManager.changeListenController("PASS_FIND_CONTROLLER");
							sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
						});
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
	
	@SuppressWarnings("unchecked")
	@FXML
	public void onApply()
	{
		if(newPass.getText().length()==0||newPassConfirm.getText().length()==0)
		{
			CustomDialog.showMessageDialog("한 글자 이상 입력하세요.", sManager.getStage());
			return;
		}
		
		if(!newPass.getText().equals(newPassConfirm.getText()))
		{
			CustomDialog.showMessageDialog("입력한 비밀번호가 서로 다릅니다.", sManager.getStage());
			return;
		}
		else
		{
			JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.PASSWORD_FIND_MODIFY_REQUEST);
			request.put("request-pw", newPass.getText());
			request.put("request-id", findId.getText());
			sendProtocol(request);
		}
	}
	
	@FXML private void onIdentify()
	{
		if(findId.getText().length()==0||findQ.getValue()==null||findA.getText().length()==0)
		{
			CustomDialog.showMessageDialog("인증정보를 정확히 입력하세요!", sManager.getStage());
			return;
		}
		
		String[] keys = {"학번","질문","답변"};
		Object[] values = {findId.getText(),findQ.getSelectionModel().getSelectedIndex()+1,findA.getText()};
		
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.PASSWORD_FIND_IDENTIFY_REQUEST, keys, values));
		
	}
	
	@FXML private void onCancel()
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
