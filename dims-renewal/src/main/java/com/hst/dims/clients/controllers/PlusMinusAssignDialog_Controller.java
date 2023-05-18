package com.hst.dims.clients.controllers;
import java.net.URL;
import java.util.ResourceBundle;

import javax.print.DocFlavor.STRING;

import com.orsoncharts.util.json.JSONObject;

import com.hst.dims.clients.customcontrols.CustomDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import com.hst.dims.tools.NetworkProtocols;
import com.hst.dims.tools.Toolbox;

public class PlusMinusAssignDialog_Controller implements Initializable{
       
		CustomDialog window = null;
		
		/*����� �ο� �޴�*/
		@FXML TextField num_Textfield;
		@FXML TextField name_Textfield;
		@FXML TextArea content_Textarea;
		@FXML TextField score_Textfield;
		@FXML Label name_Label;
		
		ObservableList<String> option = FXCollections.observableArrayList("����" , "����");
		@FXML ComboBox combo = new ComboBox<String>();
			     
		JSONObject json = new JSONObject();
		
		
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
			json = obj;
			name_Textfield.setText(json.get("�̸�").toString());
			name_Textfield.setEditable(false);
			num_Textfield.setText(json.get("�й�").toString());
			num_Textfield.setEditable(false);
			combo.setItems(option);
			
			
		}
		
		@FXML public void onConfirm()
		{
			String name = json.get("�̸�").toString();
			
			name_Label.setText(name);
			
			JSONObject userdata = Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_OVER_REQUEST);
			userdata.put("�̸�", name);
			userdata.put("�й�", json.get("�й�").toString());
			userdata.put("����", score_Textfield.getText().toString());
			userdata.put("�����Ÿ��", combo.getSelectionModel().getSelectedItem().toString());
			userdata.put("����", content_Textarea.getText().toString());
			window.setUserData(userdata);
			window.close();		
		}
		
		@FXML public void onReply()
		{
			window.setUserData("reply");
			window.close();
		}


		

	}


