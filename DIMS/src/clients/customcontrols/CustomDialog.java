package clients.customcontrols;

import java.io.IOException;

import clients.controllers.MessageDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tools.Statics;

public class CustomDialog extends Stage{

	Object controller = null;
	
	public CustomDialog(String uiName, String title, Stage owner)
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource(uiName));
		this.setTitle(title);

		Parent p = null;

		try
		{
			p = loader.load();
			System.out.println(loader.getController()+"");
			this.setScene(new Scene(p));
			this.initModality(Modality.WINDOW_MODAL);
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
		CustomDialog dlg = new CustomDialog(Statics.ALERT_DIALOG, Statics.ALERT_DIALOG_TITLE, owner);
		MessageDialogController con = (MessageDialogController)dlg.getController();
		con.setWindow(dlg);
		con.setMessage(msg);
		dlg.show();
		return dlg;
	}
	
}

