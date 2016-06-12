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
		
		if(obj.get("�߽���")==null)
		{
			name = obj.get("������").toString();
		}
		else
		{
			name = obj.get("�߽���").toString();
		}
		
		sender.setText(name);
		sendTime.setText(obj.get("�߽Žð�").toString());
		title.setText(obj.get("�޼�������").toString());
		title.setEditable(false);
		content.setText(obj.get("�޼�������").toString());
		content.setEditable(false);
	}
	
	@FXML public void onConfirm()
	{
		window.setUserData("confirm");
		window.close();		
	}

	
}
