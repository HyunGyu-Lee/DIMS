package com.hst.dims.clients.controllers;

import com.hst.dims.clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import com.hst.dims.tools.Toolbox;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class AddSubmitDialogController implements Initializable{

	@FXML TextField subTitle;
	@FXML DatePicker subDate;
	private CustomDialog window;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void setWindow(CustomDialog window)
	{
		this.window = window;
	}

	@FXML private void onSubmit()
	{
		if(subTitle.getText().length()==0)
		{
			CustomDialog.showMessageDialog("제목을 1글자 이상 입력하세요!", window);
			return;
		}

		if(subDate.getValue()==null)
		{
			CustomDialog.showMessageDialog("마감기한을 선택하세요!", window);
			return;
		}

		String[] keys = {"title","date"};
		Object[] values = {subTitle.getText(), Date.from(subDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())};
		window.setUserData(Toolbox.createJSONProtocol(keys, values));
		window.close();
	}

}