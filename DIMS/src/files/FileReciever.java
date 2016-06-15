package files;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.orsoncharts.util.json.JSONObject;

import clients.controllers.LoadingDialogController;
import clients.customcontrols.CustomDialog;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import tools.Statics;
import tools.Toolbox;

public class FileReciever {

	private Socket fileReciever;
	private byte[] recievedData;
	private String savePath;
	private boolean isTaskFinish = false;
	private String savePathVariable;
	Label curT, maxT;
	Slider timeBar;
	MediaView mvView;
	MediaPlayer mvPlayer;
	CustomDialog loadingDialog;
	Stage owner;
	Thread task;
	private LoadingDialogController con;
	private int recieved = 0;
	
	public FileReciever(String ip, int port)
	{
		try
		{
			fileReciever = new Socket(ip, port);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setUI(MediaView mvView, MediaPlayer mvPlayer, Label curT, Label maxT, Slider timeBar, CustomDialog loadingDialog, Stage owner)
	{
		this.mvView = mvView;
		this.mvPlayer = mvPlayer;
		this.curT = curT;
		this.maxT = maxT;
		this.timeBar = timeBar;
		this.loadingDialog = loadingDialog;
		this.owner = owner;
		con = (LoadingDialogController)loadingDialog.getController();
	}
	
	public void setUI(CustomDialog loadingDialog, Stage owner)
	{
		this.owner = owner;
		con = (LoadingDialogController)loadingDialog.getController();
	}
	
	public void addDownloadFinishEventHandler(Runnable r)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {

				while(!isTaskFinish)
				{
					synchronized (this) {
						try
						{
							this.wait(100);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
				Platform.runLater(r);
			}
		}).start();;
	}
	
	public void openConnection()
	{
		task = new Connection();
		task.start();
	}
	
	public String getSavePath()
	{
		return savePath;
	}
	
	public byte[] getRecieveData()
	{
		return recievedData;
	}
	
	class Connection extends Thread
	{
		ObjectInputStream ois;
		ObjectOutputStream oos;
		String fileName = "";
		String format = "";
		long data_length = 0;
		
		@Override
		public void run() 
		{
			recieved = 0;
			try
			{
				oos = new ObjectOutputStream(fileReciever.getOutputStream());
				ois = new ObjectInputStream(fileReciever.getInputStream());
				
				while(true)
				{
					JSONObject pack = (JSONObject)ois.readObject();
					String type = pack.get("type").toString();
					
					if(type.equals(FileProtocol.CONFIRM_READY))
					{
						send(Toolbox.createJSONProtocol(FileProtocol.READY_OK));
					}
					else if(type.equals(FileProtocol.DATA_INFO))
					{
						fileName = pack.get("fileName").toString();
						format = pack.get("format").toString();
						send(Toolbox.createJSONProtocol(FileProtocol.REQUEST_CONTENT));
					}
					else if(type.equals(FileProtocol.CONTENT))
					{
						recievedData = new byte[(int)con.getSize()];
						
						new Thread(new Runnable() {
							@Override
							public void run() {
								while(recieved!=recievedData.length)
								{
									Platform.runLater(new Runnable() {
										
										@Override
										public void run() {
											con.updateProgress(recieved);
										}
									});
									synchronized (this) {
										try
										{
											this.wait(100);
										}
										catch(InterruptedException e)
										{
											e.printStackTrace();
										}
									}
								}
							}
						}).start();
						
						for(int i=0;i<con.getSize();i++)
						{
							recievedData[i] = ois.readByte();
							recieved++;
						}
						
						savePath = Statics.DEFAULT_DOWNLOAD_DIRECTORY+"submitted_data.zip";
						
						savePathVariable = savePath;
						
						try
						{
							Files.write(new File(savePath).toPath(), recievedData);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						
						send(Toolbox.createJSONProtocol(FileProtocol.BREAK_REQUEST));
					}
					else if(type.equals(FileProtocol.BREAK_RESPOND))
					{
						System.out.println("연결종료 허가");
						break;
					}
				}
				System.out.println("안전종료..");
			}
			catch(IOException|ClassNotFoundException e)
			{
				System.out.println("비정상종료..");
			}
			finally
			{
				try
				{
					oos.close();
					ois.close();
					fileReciever.close();
					isTaskFinish = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
		}
		
		private void send(JSONObject pack)
		{
			try
			{
				oos.writeObject(pack);
				oos.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	}

	public void setFuture(Future f)
	{

	}
	
	public void setSavePathVariable(String savePathVariable) {
		this.savePathVariable = savePathVariable;
	}
	
}
