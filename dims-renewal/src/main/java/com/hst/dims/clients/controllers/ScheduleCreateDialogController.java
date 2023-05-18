package com.hst.dims.clients.controllers;


import com.orsoncharts.util.json.JSONObject;

import com.hst.dims.clients.customcontrols.CustomDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ScheduleCreateDialogController implements Initializable{
	
	private Stage window;
	@FXML TextField sTitle;
	@FXML DatePicker datePick;
	@FXML ComboBox<String> hourPick, minitePick;
	@FXML CheckBox cate_im, cate_ev, cate_norm;
	@FXML TextArea contentA; 
	
	public void initialize(java.net.URL location, java.util.ResourceBundle resources)
	{
		initCategory();
		initDateTime();
	}
	
	private void initCategory()
	{
		cate_im.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cate_ev.setSelected(false);
				cate_norm.setSelected(false);
			}
		});
		
		cate_ev.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cate_im.setSelected(false);
				cate_norm.setSelected(false);
			}
		});
		
		cate_norm.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cate_im.setSelected(false);
				cate_ev.setSelected(false);
			}
		});
	}
	
	private void initDateTime()
	{
		hourPick.getItems().addAll("00","01","02","03","04","05","06","07","08","09","10"
				,"11","12","13","14","15","16","17","18","19","20","21"
				,"22","23");

		minitePick.getItems().addAll("00","01","02","03","04","05","06","07","08","09"
				,"10","11","12","13","14","15","16","17","18","19"
				,"20","21","22","23","24","25","26","27","28","29"
				,"30","31","32","33","34","35","36","37","38","39"
				,"40","41","42","43","44","45","46","47","48","49"
				,"50","51","52","53","54","55","56","57","58","59");
	}
	
	
	public void setWindow(Stage window)
	{
		this.window = window;
		this.window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				window.setUserData("cancel");
			}
		});
	}
			
	@SuppressWarnings("unchecked")
	@FXML public void onCreate()
	{
		String date = "";
		if(datePick.getValue()==null)
	    {
	    	CustomDialog.showMessageDialog("��¥�� �������ּ���!", window);
	    }
	    else
	    {
	    	date = datePick.getValue().toString();
	    }
	   
	    if(hourPick.getValue()==null||minitePick.getValue()==null)
	    {
	    	CustomDialog.showMessageDialog("�ð��� �������ּ���!", window);
		    return;
	    }
		
	    date = date + " " + hourPick.getValue() + ":" + minitePick.getValue();
	    String cate = "";

		if(cate_im.isSelected()==true)
		{
			cate = "�߿�";
		}
		   
	    if(cate_ev.isSelected()==true)
	    {
		    cate = "���";
	    }
	   
	    if(cate_norm.isSelected()==true)
	    {
		   cate = "�Ϲ�";
	    }
	    
	    JSONObject create_info = new JSONObject();
		create_info.put("����", sTitle.getText());
		create_info.put("�Ͻ�", date);
		create_info.put("�з�", cate);
		create_info.put("����", contentA.getText());
		System.out.println(create_info.toJSONString());
		window.setUserData(create_info);
		window.close();
	}
	
}
