package clients.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONArray;

import clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tools.Toolbox;

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
			CustomDialog.showMessageDialog("������ 1���� �̻� �Է��ϼ���!", window);
			return;
		}

		if(subDate.getValue()==null)
		{
			CustomDialog.showMessageDialog("���������� �����ϼ���!", window);
			return;
		}
		
		String[] keys = {"title","date"};
		Object[] values = {subTitle.getText(), Date.from(subDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())};
		window.setUserData(Toolbox.createJSONProtocol(keys, values));
		window.close();
	}
	
}
