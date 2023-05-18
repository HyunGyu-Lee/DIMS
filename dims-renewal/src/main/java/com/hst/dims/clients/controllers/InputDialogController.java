package com.hst.dims.clients.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.hst.dims.clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class InputDialogController implements Initializable {

	private CustomDialog window;
	@FXML private Label msg_area;
	@FXML private TextField inputArea;
	
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
	
	public String input()
	{
		return inputArea.getText();
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
