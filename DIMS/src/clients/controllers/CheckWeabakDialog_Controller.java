package clients.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CheckWeabakDialog_Controller {
   @FXML Label Lable_name;
   @FXML TextField date_filed;
   @FXML TextArea content_field;
   @FXML TextField name_field;
   @FXML TextField num_field;
   @FXML TextField destination_field;
   @FXML TextField date1_field;
   
   CustomDialog window;
   
   public void initialize(URL location, ResourceBundle resources) {
      // TODO Auto-generated method stub
   
   }

   public void setWindow(CustomDialog window)
   {
      this.window = window;
   }
   
   
   public void setProperty(JSONObject obj)
   {
      System.out.println("확인  : " +  window.getController().toString());
      Lable_name.setText(obj.get("이름").toString());
      name_field.setText(obj.get("이름").toString());
      num_field.setText(obj.get("학번").toString());
      date_filed.setText(obj.get("신청일자").toString());
      date1_field.setText(obj.get("외박일자").toString());
      content_field.setText(obj.get("사유").toString());
      destination_field.setText(obj.get("목적지").toString());
   }
   
   @SuppressWarnings("unchecked")
   @FXML public void onApply()
   {
	   JSONObject obj = new JSONObject();
	   obj.put("action", "1");
	   window.setUserData(obj);
	   window.close();
   }
   
   @SuppressWarnings("unchecked")
   @FXML public void onDeny()
   {
	   JSONObject obj = new JSONObject();
	   obj.put("action", "2");
	   window.setUserData(obj);   
	   window.close();
   }
   
   @SuppressWarnings("unchecked")
   @FXML public void onCancle()
   {
	   JSONObject obj = new JSONObject();
	   obj.put("action", "not");
	   window.setUserData(obj);   
	   window.close();
   }

}