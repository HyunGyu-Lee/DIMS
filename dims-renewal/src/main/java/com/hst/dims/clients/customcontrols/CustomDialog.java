package com.hst.dims.clients.customcontrols;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.hst.dims.clients.controllers.InputDialogController;
import com.hst.dims.clients.controllers.MessageDialogController;
import com.hst.dims.clients.controllers.OK_CANCEL_DialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.hst.dims.tools.Statics;

public class CustomDialog extends Stage{

	public static final int OK_OPTION = 1;
	public static final int CANCEL_OPTION = 2;
	
	Object controller = null;
	
	public CustomDialog(String uiName, String title, Stage owner, Modality modalType)
	{

		URL url;
		try {
			url = new File("build/resources/main/" + uiName).toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		FXMLLoader loader = new FXMLLoader(url);
		this.setTitle(title);

		Parent p = null;

		try
		{
			p = loader.load();
			System.out.println(loader.getController()+"");
			this.setScene(new Scene(p));
			this.initModality(modalType);
			this.initOwner(owner);
			controller = loader.getController();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		this.setResizable(false);
	}
	
	public Object getController()
	{
		return controller;
	}
	
	public static CustomDialog showMessageDialog(String msg, Stage owner)
	{
		CustomDialog dlg = new CustomDialog(Statics.ALERT_DIALOG, Statics.ALERT_DIALOG_TITLE, owner, Modality.WINDOW_MODAL);
		MessageDialogController con = (MessageDialogController)dlg.getController();
		con.setWindow(dlg);
		con.setMessage(msg);
		dlg.show();
		return dlg;
	}
	
	public static int showConfirmDialog(String msg, Stage owner)
	{
		CustomDialog dlg = new CustomDialog(Statics.OK_CANCLE_DIALOG, Statics.OK_CANCEL_DAILOG_TITLE, owner, Modality.WINDOW_MODAL);
		OK_CANCEL_DialogController con = (OK_CANCEL_DialogController)dlg.getController();
		con.setWindow(dlg);
		con.setMessage(msg);
		dlg.showAndWait();
		return (int)dlg.getUserData();
	}
	
	public static String showInputDialog(String msg, Stage owner)
	{
		CustomDialog dlg = new CustomDialog(Statics.INPUT_DIALOG, Statics.INPUT_DIALOG_TITLE, owner, Modality.WINDOW_MODAL);
		InputDialogController con = (InputDialogController)dlg.getController();
		con.setWindow(dlg);
		con.setMessage(msg);
		dlg.showAndWait();
		
		if((int)dlg.getUserData()==OK_OPTION)
		{
			return con.input();
		}
		else
		{
			return null;
		}
	}
	
}

