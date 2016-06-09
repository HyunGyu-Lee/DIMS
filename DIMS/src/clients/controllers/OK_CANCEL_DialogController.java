package clients.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class OK_CANCEL_DialogController implements Initializable{
	
	private CustomDialog window;
	@FXML private Label msg_area;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void setWindow(CustomDialog window)
	{
		this.window = window;
	}
	
	public void setMessage(String msg)
	{
		msg_area.setText(msg);
	}
	
	@FXML
	public void onOK()
	{
		window.setUserData(CustomDialog.OK_OPTION);
		window.close();
	}
	
	@FXML
	public void onCancel()
	{
		window.setUserData(CustomDialog.CANCEL_OPTION);
		window.close();
	}
	
}
