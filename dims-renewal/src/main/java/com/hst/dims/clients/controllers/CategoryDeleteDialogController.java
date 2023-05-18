package com.hst.dims.clients.controllers;

import com.hst.dims.clients.customcontrols.CustomDialog;
import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoryDeleteDialogController implements Initializable{

	private CustomDialog window;
	@FXML ComboBox<String> category_selector;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setWindow(CustomDialog window)
	{
		this.window = window;
	}

	@SuppressWarnings("unchecked")
	public void setProperty(JSONArray property)
	{
		property.forEach((str)->{
			category_selector.getItems().add(str.toString());
		});
	}

	@SuppressWarnings("unchecked")
	@FXML private void onDeleteCategory()
	{
		JSONObject uData = new JSONObject();
		uData.put("selected", category_selector.getSelectionModel().getSelectedIndex()+1);
		System.out.println("Á¦ÀÌ½¼ : "+uData.toJSONString());
		System.out.println("À©µµ¿ì : "+window);
		window.setUserData(uData);
		window.close();
	}

}