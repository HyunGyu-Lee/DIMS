package com.hst.dims.clients.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import com.hst.dims.clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.hst.dims.tools.NetworkProtocols;
import com.hst.dims.tools.Toolbox;

public class ReAuthDialogController implements Initializable {

	@FXML Label requestID;
	@FXML TextField requestPassword;

	private CustomDialog window;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setProperty(String prop)
	{
		requestID.setText(prop);
	}

	public void setWindow(CustomDialog window)
	{
		this.window = window;
	}

	@SuppressWarnings("unchecked")
	@FXML private void onReAuthRequest()
	{
		if(requestPassword.getText().length()==0)
		{
			CustomDialog.showMessageDialog("ºñ¹Ð¹øÈ£¸¦ ÀÔ·ÂÇÏ¼¼¿ä!", window);
			return;
		}

		JSONObject obj = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_REAUTH_REQUEST);
		obj.put("reqID", requestID.getText());
		obj.put("reqPW", requestPassword.getText());

		window.setUserData(obj);
		window.close();
	}

}