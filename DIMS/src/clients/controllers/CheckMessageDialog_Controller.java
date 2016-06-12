package clients.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CheckMessageDialog_Controller implements Initializable{

	@FXML Label sender, sendTime;
	@FXML TextField title;
	@FXML TextArea content;
	CustomDialog window;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
	
	public void setWindow(CustomDialog window)
	{
		this.window = window;
	}
	
	public void setProperty(JSONObject obj)
	{
		String name = "";
		
		if(obj.get("발신자")==null)
		{
			name = obj.get("수신자").toString();
		}
		else
		{
			name = obj.get("발신자").toString();
		}
		
		sender.setText(name);
		sendTime.setText(obj.get("발신시각").toString());
		title.setText(obj.get("메세지제목").toString());
		title.setEditable(false);
		content.setText(obj.get("메세지본문").toString());
		content.setEditable(false);
	}
	
	@FXML public void onConfirm()
	{
		window.setUserData("confirm");
		window.close();		
	}

	
}
