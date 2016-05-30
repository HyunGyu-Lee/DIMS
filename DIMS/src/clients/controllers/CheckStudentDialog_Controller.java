package clients.controllers;


import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CheckStudentDialog_Controller implements Initializable {

	@FXML Label Lable_name;
	@FXML TextField StudentNum;
	@FXML TextField name;
	CustomDialog window;
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}



	public void setWindow(CustomDialog window)
	{
		this.window = window;
	}
	
	
	public void setProperty(JSONObject obj)
	{
		Lable_name.setText(obj.get("이름").toString());
		StudentNum.setText(obj.get("학번").toString());
		StudentNum.setEditable(false);
		name.setText(obj.get("이름").toString());
		StudentNum.setEditable(false);
	}
	
	@FXML public void onConfirm()
	{
		window.setUserData("confirm");
		window.close();		
	}
	
	@FXML public void onReply()
	{
		window.setUserData("reply");
		window.close();
	}

}
