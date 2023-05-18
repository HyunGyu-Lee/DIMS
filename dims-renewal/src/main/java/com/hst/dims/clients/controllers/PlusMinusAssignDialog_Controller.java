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

	/*상벌점 부여 메뉴*/
	@FXML TextField num_Textfield;
	@FXML TextField name_Textfield;
	@FXML TextArea content_Textarea;
	@FXML TextField score_Textfield;
	@FXML Label name_Label;

	ObservableList<String> option = FXCollections.observableArrayList("상점" , "벌점");
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
		name_Textfield.setText(json.get("이름").toString());
		name_Textfield.setEditable(false);
		num_Textfield.setText(json.get("학번").toString());
		num_Textfield.setEditable(false);
		combo.setItems(option);


	}

	@FXML public void onConfirm()
	{
		String name = json.get("이름").toString();

		name_Label.setText(name);

		JSONObject userdata = Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_OVER_REQUEST);
		userdata.put("이름", name);
		userdata.put("학번", json.get("학번").toString());
		userdata.put("점수", score_Textfield.getText().toString());
		userdata.put("상벌점타입", combo.getSelectionModel().getSelectedItem().toString());
		userdata.put("내용", content_Textarea.getText().toString());
		window.setUserData(userdata);
		window.close();
	}

	@FXML public void onReply()
	{
		window.setUserData("reply");
		window.close();
	}




}

