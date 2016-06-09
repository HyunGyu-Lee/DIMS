package clients.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class LoadingDialogController implements Initializable {
	
	@FXML Label fNameView, fSizeView;
	@FXML ProgressBar download_progress;
	int size;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void setProperty(String name, int size)
	{
		int KB = 1024*1024;
		double maxSize = Math.round(Double.parseDouble(String.format("%d.%d", size/KB, size%KB))*100)/100.0;
		
		fNameView.setText(name);
		fSizeView.setText(maxSize+"MB");
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}
	
	public void updateProgress(long cur)
	{
		download_progress.setProgress(((double)cur/(double)size));
		System.out.println(((double)cur/(double)size));
	}
	
	
}
