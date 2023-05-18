package com.hst.dims.clients.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.hst.dims.clients.customcontrols.CustomDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.hst.dims.tools.NetworkProtocols;
import com.hst.dims.tools.Toolbox;

public class EmailSendDialogController implements Initializable{

	@FXML TextField mailId, mailTitle;
	@FXML TextArea mailContent;
	@FXML ComboBox<String> mailServer;
	private CustomDialog window;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		mailServer.getItems().addAll("naver.com","gmail.com");
	}
	
	public void setWindow(CustomDialog window)
	{
		this.window = window;
	}
	
	@FXML
	private void onSendMail()
	{
		if(mailId.getLength()==0||mailServer.getValue()==null||mailTitle.getText().length()==0||mailContent.getText().length()==0)
		{
			CustomDialog.showMessageDialog("�߼������� ��Ȯ�� �Է��ϼ���.", window);
			return;
		}
		
		String[] keys = {"��������","���Ϻ���","�������"};
		Object[] values = {mailTitle.getText(),
						   mailContent.getText(),
						   mailId.getText()+"@"+mailServer.getValue()
		};
		
		window.setUserData(Toolbox.createJSONProtocol(NetworkProtocols.EMAIL_SEND_REQUEST, keys, values));
		window.close();
	}
	
	
	
}
