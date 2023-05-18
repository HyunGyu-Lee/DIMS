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
	   @FXML TextField classNameField;   //�а���
	   @FXML TextField classNumField;    //�й�
	   @FXML TextField Name;  //�̸�
	   @FXML TextField RoomNum; //���ȣ
	   @FXML TextField bigNum; // �г�
	   @FXML TextField StudentInfoNum; //�ֹε�Ϲ�ȣ
	   @FXML TextField phoneNum; // ����ȣ
	   @FXML TextArea address; // �ּ�
	   @FXML TextField hoomCellNum; // ���ּ�
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
	      Lable_name.setText(obj.get("�̸�").toString());
	      classNameField.setText(obj.get("�Ҽ��а�").toString());
	      classNumField.setText(obj.get("�й�").toString());
	      Name.setText(obj.get("�̸�").toString());
	      RoomNum.setText(obj.get("���ȣ").toString()+"ȣ");
	      int a = (int)obj.get("�г�");
	      System.out.println(a);
	      bigNum.setText(a+"");
	      System.out.println(bigNum);
	      StudentInfoNum.setText(obj.get("�ֹε�Ϲ�ȣ").toString());
	      phoneNum.setText(obj.get("�޴�����ȣ").toString());
	      hoomCellNum.setText(obj.get("������ȭ��ȣ").toString());
	      address.setText(obj.get("�ּ�").toString());
	      
	      if(obj.get("�̹���������").equals("no-image")==false)
	      {
	    	  byte[] data = (byte[])obj.get("�̹���������");
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
