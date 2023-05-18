package com.hst.dims.clients.controllers;


import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import com.hst.dims.clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import com.hst.dims.tools.Toolbox;

public class CheckStudentDialog_Controller implements Initializable {

	@FXML Label Lable_name;
	@FXML TextField classNameField;   //학과명
	@FXML TextField classNumField;    //학번
	@FXML TextField Name;  //이름
	@FXML TextField RoomNum; //방번호
	@FXML TextField bigNum; // 학년
	@FXML TextField StudentInfoNum; //주민등록번호
	@FXML TextField phoneNum; // 폰번호
	@FXML TextArea address; // 주소
	@FXML TextField hoomCellNum; // 집주소
	CustomDialog window;
	@FXML ImageView user_image;


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
		classNameField.setText(obj.get("소속학과").toString());
		classNumField.setText(obj.get("학번").toString());
		Name.setText(obj.get("이름").toString());
		RoomNum.setText(obj.get("방번호").toString()+"호");
		int a = (int)obj.get("학년");
		System.out.println(a);
		bigNum.setText(a+"");
		System.out.println(bigNum);
		StudentInfoNum.setText(obj.get("주민등록번호").toString());
		phoneNum.setText(obj.get("휴대폰번호").toString());
		hoomCellNum.setText(obj.get("자택전화번호").toString());
		address.setText(obj.get("주소").toString());

		if(obj.get("이미지데이터").equals("no-image")==false)
		{
			byte[] data = (byte[])obj.get("이미지데이터");
			user_image.setImage(Toolbox.getWritableByArray(data));
		}
		classNameField.setEditable(false);
		classNumField.setEditable(false);
		Name.setEditable(false);
		RoomNum.setEditable(false);
		bigNum.setEditable(false);
		StudentInfoNum.setEditable(false);
		phoneNum.setEditable(false);
		hoomCellNum.setEditable(false);
		address.setEditable(false);

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