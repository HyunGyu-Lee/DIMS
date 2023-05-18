package com.hst.dims.clients;

import com.hst.dims.clients.customcontrols.CustomDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.hst.dims.tools.Statics;
import com.hst.dims.tools.Toolbox;

public class MainApplication extends Application {
	
	private SceneManager sManager;
	private CustomDialog retryDlg;
	@Override
	public void start(Stage primaryStage) throws Exception {

		sManager = new SceneManager(primaryStage);
		sManager.setHost(this);
		int c = sManager.connectToServer();
		switch(c)
		{
		case Statics.CONNECT_ERROR      : retryDlg = CustomDialog.showMessageDialog("�������ӽ���, �ڵ��������� �Ͻ÷��� â�� �������� ������", primaryStage); break;
		case Statics.UNKNOWN_HOST_ERROR : Toolbox.showMessageDialog(primaryStage, 200, 50, "���� IP�ּҰ� �߸��ƽ��ϴ�."); break;
		case Statics.COMMIT				: restart(primaryStage); break;
		}
		
		if(c!=Statics.COMMIT)
		{
			new Retry(sManager).start();
		}
	}

	public void restart(Stage primaryStage)
	{
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event)
			{
				System.exit(1);
			}
		});

		sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
		
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	class Retry extends Thread
	{
		SceneManager m;
		Retry(SceneManager m)
		{
			this.m = m;
		}
		
		@Override
		public void run()
		{
			System.out.println("���� ������ �õ��� �����մϴ�.");
			int cnt = 0;
			while(true)
			{
				System.out.println("������ �õ�Ƚ�� : "+cnt++);
				System.out.print("���                 : ");
				int c = m.connectToServer();
				if(c!=Statics.COMMIT)
				{
					System.out.println("����..");
					synchronized (this)
					{
						try
						{
							this.wait(500);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					System.out.println("����! ���α׷��� �����ϰ� �����ӽ����带 �����մϴ�.");
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							restart(sManager.getStage());
							retryDlg.close();
						}
					});
					break;
				}
				
			}
		}
	}
	
}
