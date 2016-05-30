package clients.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;

import clients.SceneManager;
import clients.customcontrols.CustomDialog;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import tools.NetworkProtocols;
import tools.Statics;
import tools.Toolbox;

public class StudentMain implements Initializable {
	
	private SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private String uName, uID;
	
	// �ֻ��
	@FXML ImageView image;
	@FXML Label dateText, dateTime, userName;
	@FXML StackPane OVERNIGHT_MAIN_VIEW;
	@FXML AnchorPane OVERNIGHT_INFO_VIEW;
	@FXML AnchorPane OVERNIGHT_REQUEST_VIEW;
	
	// �ܹڰ��� - ����
	@FXML ListView<HBox> overnight_list_view;	// �ܹ� ���� ����Ʈ��
	private ObservableList<HBox> overnight_list_view_data;	// �ܹ� ���� ����Ʈ�� ������
	
	// �ܹڰ��� - ��û
	@FXML DatePicker overnight_date_pciker;	// �����¥
	@FXML TextField destination;			// ������
	@FXML TextArea reason;					// ����
	
	// �޼���
	@FXML StackPane MESSAGE_VIEW;			// �޼��� ���� ����
	@FXML AnchorPane message_re_view;		// ���� �޼�����
	@FXML AnchorPane message_se_view;		// ���� �޼�����
	@FXML AnchorPane message_write_view;	// �޼��� �ۼ���

	// �޼��� ����
	@FXML ListView<HBox> recieve_message_view;	// ���� �޼��� ����Ʈ
	@FXML ListView<HBox> send_message_view;		// ���� �޼��� ����Ʈ
	private ObservableList<HBox> recieve_message_view_data;	// ���� �޼��� ����Ʈ ������
	private ObservableList<HBox> send_message_view_data;	// ���� �޼��� ����Ʈ ������
	
	// �޼��� ������
	@FXML TextField msgTitle, msgRecieverSearchField;	// ����, �̸��˻�
	@FXML ListView<HBox> whole_reciever_list, selected_reciever_list;	// ��ü ������� ����Ʈ, ���õ� ��� ����Ʈ
	@FXML TextArea msgContentArea;	// �޼��� ����
	private ObservableList<HBox> wholeRListData, selectedRListData;		// �� �� �� ����Ʈ ������
	private ArrayList<String> rcList;	// ��ü ������� ������

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				Calendar cal = Calendar.getInstance();
				java.util.Date date = cal.getTime();
				dateText.setText(Toolbox.getCurrentTimeFormat(date, "YYYY-MM-dd (E)"));
				dateTime.setText(Toolbox.getCurrentTimeFormat(date, "a hh:mm:ss"));
			}
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		
		overnight_list_view_data = FXCollections.observableArrayList();
		
		recieve_message_view_data = FXCollections.observableArrayList();
		send_message_view_data = FXCollections.observableArrayList();
		wholeRListData = FXCollections.observableArrayList();;
		selectedRListData = FXCollections.observableArrayList();
		selected_reciever_list.setItems(selectedRListData);
		shutdown();
		
		
	}
	
	public void INIT_CONTROLLER(SceneManager manager, ObjectInputStream fromServer, ObjectOutputStream toServer)
	{
		this.sManager = manager;
		this.fromServer = fromServer;
		this.toServer = toServer;
	}
	
	public void startListener()
	{
		new Listener().start();
	}
	
	class Listener extends Thread
	{
		@SuppressWarnings("unchecked")
		@Override
		public void run()
		{
			System.out.println("StudentMainController ������ ������ ����");
			
			while(true)
			{
				try
				{
					JSONObject line = null;
					try
					{
						line = (JSONObject) fromServer.readObject();
						System.out.println(line.toJSONString());
						
						String type = line.get("type").toString();
						
						if(type.equals(NetworkProtocols.WINDOW_INIT_PROPERTY))
						{
							uName = line.get("uName").toString();
							uID = line.get("uID").toString();
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									userName.setText(uName);
								}
							});
						}
						else if(type.equals(NetworkProtocols.PLZ_REQUEST))
						{
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.WINDOW_INIT_PROPERTY));							
						}
						else if(type.equals(NetworkProtocols.RECIEVE_READY))
						{
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.RECIEVE_READY_OK));
						}
						else if(type.equals(NetworkProtocols.MY_OVERNIGHT_LIST_RESPOND))
						{
							JSONArray data = (JSONArray)line.get("data");
							Platform.runLater(new Runnable() {
								@Override
								public void run()
								{
									createOvernightListView(data);
								}
							});
						}
						else if(type.equals(NetworkProtocols.ENROLL_OVERNIGHT_RESPOND))
						{
							String result = line.get("result").toString();
							
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									if(result.equals("OK"))
									{
										CustomDialog.showMessageDialog("�ܹ��� ���������� ��û�ƽ��ϴ�.", sManager.getStage());
									}
									else
									{
										CustomDialog.showMessageDialog("����", sManager.getStage());								
									}
								}
							});
							JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.MY_OVERNIGHT_LIST_REQUEST);
							req.put("uID", uID);
							sendProtocol(req);
						}
						else if(type.equals(NetworkProtocols.STUDENT_RECIEVE_MESSAGE_RESPOND))
						{
							JSONArray arr = (JSONArray)line.get("data");
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									createMessageList("r", arr);
									MESSAGE_VIEW.setVisible(true);
									message_re_view.setVisible(true);
									message_se_view.setVisible(false);
									message_write_view.setVisible(false);
								}
							});
						}
						else if(type.equals(NetworkProtocols.STUDENT_SEND_MESSAGE_RESPOND))
						{
							JSONArray arr = (JSONArray)line.get("data");
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									createMessageList("s", arr);
									MESSAGE_VIEW.setVisible(true);
									message_se_view.setVisible(true);
									message_re_view.setVisible(false);
									message_write_view.setVisible(false);
								}
							});
						}
						else if(type.equals(NetworkProtocols.MESSAGE_USER_LIST_RESPOND))
						{
							rcList = (ArrayList<String>)line.get("rcList");
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									createReciverList(rcList);
									MESSAGE_VIEW.setVisible(true);
									message_se_view.setVisible(false);
									message_re_view.setVisible(false);
									message_write_view.setVisible(true);
								}
							});
						}
						else if(type.equals(NetworkProtocols.MESSAGE_SEND_RESPOND))
		                {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									CustomDialog.showMessageDialog("�޼��� �߼� ����!", sManager.getStage());
								}
							});
						}
						else if(type.equals(NetworkProtocols.SHOW_MESSAGE_DIALOG))
						{
							JSONObject prc = line;
							Platform.runLater(new Runnable() {
							
								@Override
								public void run() {
									CustomDialog.showMessageDialog(prc.get("msg").toString(), sManager.getStage());
								}
							});
							
						}
						else if(type.equals(NetworkProtocols.EXIT_RESPOND))
						{
							break;
						}
						
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void shutdown()
	{
		OVERNIGHT_MAIN_VIEW.setVisible(false);
		MESSAGE_VIEW.setVisible(false);
	}
	
	public void createMessageList(String string, JSONArray arr)
	{
		String key = string;
		
		if(key.equals("r"))
		{
			recieve_message_view_data.clear();
			
			for(Object o : arr)
			{
				HBox item = new HBox();
				JSONObject target = (JSONObject)o;
				
				Label refferKey = new Label(target.get("�޼�������").toString());
				refferKey.setPrefWidth(0);
				refferKey.setVisible(false);
				Label rDate = new Label(Toolbox.getSQLDateToFormat((java.sql.Date)target.get("�߽Žð�"), "yyyy�� MM�� dd��"));
				Label sender = new Label(target.get("�߽���").toString());
				Label title = new Label(target.get("�޼�������").toString());
				
				Separator s0 = new Separator(Orientation.VERTICAL);
				s0.setPrefWidth(6);
				Separator s1 = new Separator(Orientation.VERTICAL);
				s1.setPrefWidth(6);
				
				rDate.setPrefSize(185, 35);
				rDate.setFont(Font.font("HYwulM",15));
				rDate.setAlignment(Pos.CENTER);
				
				sender.setPrefSize(132, 35);
				sender.setFont(Font.font("HYwulM",15));
				sender.setAlignment(Pos.CENTER);
				
				title.setPrefSize(708, 35);
				title.setFont(Font.font("HYwulM",15));
				title.setAlignment(Pos.CENTER);
				
				item.getChildren().addAll(refferKey, rDate, s0, sender, s1, title);
				
				item.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event)
					{
						if(event.getClickCount()==2)
						{
							CustomDialog dlg = new CustomDialog(Statics.CHECK_MESSAGE_FXML, Statics.CHECK_MESSAGE_TITLE, sManager.getStage());
							CheckMessageDialog_Controller con = (CheckMessageDialog_Controller)dlg.getController();
							con.setWindow(dlg);
							con.setProperty(target);
							dlg.show();
						}
					};
				});
				
				recieve_message_view_data.add(item);
			}
			recieve_message_view.setItems(recieve_message_view_data);
		}
		else if(key.equals("s"))
		{
			send_message_view_data.clear();
			
			for(Object o : arr)
			{
				HBox item = new HBox();
				JSONObject target = (JSONObject)o;
				
				Label refferKey = new Label(target.get("�޼�������").toString());
				refferKey.setPrefWidth(0);
				refferKey.setVisible(false);
				Label rDate = new Label(Toolbox.getSQLDateToFormat((java.sql.Date)target.get("�߽Žð�"), "yyyy�� MM�� dd��"));
				Label sender = new Label(target.get("������").toString());
				Label title = new Label(target.get("�޼�������").toString());
				
				Separator s0 = new Separator(Orientation.VERTICAL);
				s0.setPrefWidth(6);
				Separator s1 = new Separator(Orientation.VERTICAL);
				s1.setPrefWidth(6);
				
				rDate.setPrefSize(185, 35);
				rDate.setFont(Font.font("HYwulM",15));
				rDate.setAlignment(Pos.CENTER);
				
				sender.setPrefSize(132, 35);
				sender.setFont(Font.font("HYwulM",15));
				sender.setAlignment(Pos.CENTER);
				
				title.setPrefSize(708, 35);
				title.setFont(Font.font("HYwulM",15));
				title.setAlignment(Pos.CENTER);
				
				item.getChildren().addAll(refferKey, rDate, s0, sender, s1, title);
				
				item.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event)
					{
						if(event.getClickCount()==2)
						{
							CustomDialog dlg = new CustomDialog(Statics.CHECK_MESSAGE_FXML, Statics.CHECK_MESSAGE_TITLE, sManager.getStage());
							CheckMessageDialog_Controller con = (CheckMessageDialog_Controller)dlg.getController();
							con.setWindow(dlg);
							con.setProperty(target);
							dlg.show();
						}
					};
				});
				
				send_message_view_data.add(item);
			}
			send_message_view.setItems(send_message_view_data);
		}
	}

	public void createOvernightListView(JSONArray data)
	{
		overnight_list_view_data.clear();
		for(Object obj : data)
		{
			JSONObject target = (JSONObject)obj;
			
			// ��û���� �ܹ����� ������ ���� ���ο���
			HBox item = new HBox();
			
			Label reqDate = new Label(Toolbox.getCurrentTimeFormat((java.sql.Timestamp)target.get("��û����"), "yyyy�� MM�� dd�� HH:mm:ss"));
			Label reqOverDate = new Label(Toolbox.getSQLDateToFormat((java.sql.Date)target.get("�ܹ�����"), "yyyy-MM-dd"));
			Label destination = new Label(target.get("������").toString());
			Label reason = new Label(target.get("����").toString());
			
			String iG = "";
			
			switch(Integer.parseInt(target.get("���ο���").toString()))
			{
			case 0 : iG = "�����"; break;
			case 1 : iG = "��   ��"; break;
			case 2 : iG = "��   ��"; break;
			}
			
			Label isGrant = new Label(iG);
			
			reqDate.setPrefSize(225, 35);
			reqDate.setFont(Font.font("HYwulM",15));
			reqDate.setAlignment(Pos.CENTER);

			reqOverDate.setPrefSize(230, 35);
			reqOverDate.setFont(Font.font("HYwulM",15));
			reqOverDate.setAlignment(Pos.CENTER);
			
			destination.setPrefSize(232, 35);
			destination.setFont(Font.font("HYwulM",15));
			destination.setAlignment(Pos.CENTER);
			
			reason.setPrefSize(422, 35);
			reason.setFont(Font.font("HYwulM",15));
			reason.setAlignment(Pos.CENTER);
			
			isGrant.setPrefSize(157, 35);
			isGrant.setFont(Font.font("HYwulM",15));
			isGrant.setAlignment(Pos.CENTER);
			
			Separator s0 = new Separator(Orientation.VERTICAL);
			s0.setPrefWidth(6);
			Separator s1 = new Separator(Orientation.VERTICAL);
			s1.setPrefWidth(6);
			Separator s2 = new Separator(Orientation.VERTICAL);
			s2.setPrefWidth(6);
			Separator s3 = new Separator(Orientation.VERTICAL);
			s3.setPrefWidth(6);
			
			// ��Ÿ�� ��������
			item.getChildren().addAll(reqDate, s0,reqOverDate, s1,destination, s2,reason, s3, isGrant);
			
			System.out.println(target.get("����")+", "+target.get("��û����"));
			overnight_list_view_data.add(item);
		}
		
		overnight_list_view.setItems(overnight_list_view_data);
		OVERNIGHT_MAIN_VIEW.setVisible(true);
		OVERNIGHT_REQUEST_VIEW.setVisible(false);
		OVERNIGHT_INFO_VIEW.setVisible(true);
	}
	
	@SuppressWarnings("unchecked")
	@FXML
	public void onOvernightListRequest()
	{
		// �ܹڿ�û
		JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.MY_OVERNIGHT_LIST_REQUEST);
		req.put("uID", uID);
		sendProtocol(req);
	}
	
	@FXML
	public void onShowEnrollOvernight()
	{
		shutdown();
		OVERNIGHT_MAIN_VIEW.setVisible(true);
		OVERNIGHT_INFO_VIEW.setVisible(false);
		OVERNIGHT_REQUEST_VIEW.setVisible(true);
	}
	
	@FXML
	public void onEnrollOvernightRequest()
	{
		String[] keys = {"�ܹ�����","������","����"};
		
		LocalDate reqDate = overnight_date_pciker.getValue();
		
		if(reqDate==null||destination.getText().length()==0||reason.getText().length()==0)
		{
			CustomDialog.showMessageDialog("�ʿ� ������ ��Ȯ�ϰ� �Է��ϼ���", sManager.getStage());
			return;
		}
		
		Object[] values = {reqDate.getYear()+"-"+reqDate.getMonthValue()+"-"+reqDate.getDayOfMonth(),destination.getText(),reason.getText()};
		
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ENROLL_OVERNIGHT_REQUEST, keys, values));
	}
	
	@FXML
	public void onMainLogoClicked()
	{
		// ���� �ΰ� Ŭ����
		shutdown();
	}
	
	@FXML
	public void onLogOut()
	{
		sManager.doFullscreen(false);
		sManager.changeListenController("STUDENT_MAIN");
		sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
	}
	
	@SuppressWarnings("unchecked")
	@FXML public void onRMessage()
	{
		// ������ ��û
		JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_RECIEVE_MESSAGE_REQUEST);
		req.put("uID", uID);
		sendProtocol(req);
	}
	
	@SuppressWarnings("unchecked")	
	@FXML public void onSMessage()
	{
		JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SEND_MESSAGE_REQUEST);
		req.put("uID", uID);
		sendProtocol(req);
	}
	
	@SuppressWarnings("unchecked")
	@FXML public void onSendMessage()
	{
		JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_REQUEST);
		
		long curTime = System.currentTimeMillis();
		
		//
		ArrayList<String> sList = new ArrayList<String>();
      
		for(HBox target : selectedRListData)
		{
			sList.add(((Label)target.getChildren().get(0)).getText()+","+((Label)target.getChildren().get(2)).getText());
		}
		
		req.put("sender", uID);
		req.put("reciever", sList);
		req.put("msgTitle", msgTitle.getText());
		req.put("msgContent", msgContentArea.getText());
		req.put("sendTime", curTime);
 
		sendProtocol(req);
	}
	
	@FXML public void onWriteMessage()
	{
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_USER_LIST_REQUEST));
	}
	
	private void createReciverList(ArrayList<String> data)
	{
		wholeRListData.removeAll(wholeRListData);
		for(String t : data)
		{
			String[] info = t.split(",");
			Label left = new Label(info[0]);
			Label right = new Label(info[1]);
			
			left.setFont(Font.font("HYwulM",18));
			left.setAlignment(Pos.CENTER);
			right.setFont(Font.font("HYwulM",18));
			right.setAlignment(Pos.CENTER);
			left.setMaxWidth(Double.MAX_VALUE);
			left.setMaxHeight(Double.MAX_VALUE);
			right.setMaxWidth(Double.MAX_VALUE);
			right.setMaxHeight(Double.MAX_VALUE);
			Separator s0 = new Separator(Orientation.VERTICAL);
			s0.setPrefWidth(6);
			
			HBox item = new HBox();
			HBox.setHgrow(right, Priority.ALWAYS);
			HBox.setHgrow(left, Priority.ALWAYS);
			item.getChildren().addAll(left, s0, right);
			wholeRListData.add(item);
		}
		
		whole_reciever_list.setItems(wholeRListData);
		
	}
	
	@FXML public void onAddReciever()
	{
		Label left = new Label(((Label)whole_reciever_list.getSelectionModel().getSelectedItem().getChildren().get(0)).getText());
		Label right = new Label(((Label)whole_reciever_list.getSelectionModel().getSelectedItem().getChildren().get(2)).getText());
		
		left.setFont(Font.font("HYwulM",18));
		left.setAlignment(Pos.CENTER);
		right.setFont(Font.font("HYwulM",18));
		right.setAlignment(Pos.CENTER);
		left.setMaxWidth(Double.MAX_VALUE);
		left.setMaxHeight(Double.MAX_VALUE);
		right.setMaxWidth(Double.MAX_VALUE);
		right.setMaxHeight(Double.MAX_VALUE);
		Separator s0 = new Separator(Orientation.VERTICAL);
		s0.setPrefWidth(6);
		
		HBox item = new HBox();
		HBox.setHgrow(right, Priority.ALWAYS);
		HBox.setHgrow(left, Priority.ALWAYS);
		item.getChildren().addAll(left, s0, right);
		
		item.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount()==2)selectedRListData.remove(item);				
			}
		});
		
		selectedRListData.add(item);
	}
	
	@FXML private void onMsgSearch()
	{
		String searchKey = msgRecieverSearchField.getText();
		ArrayList<String> newList = new ArrayList<String>();

		for(String s : rcList)
		{
			if(s.contains(searchKey))
			{
				newList.add(s);
			}
		}
		
		createReciverList(newList);
		whole_reciever_list.refresh();		
	}
	
	public void sendProtocol(JSONObject protocol)
	{
		try
		{
			toServer.writeObject(protocol);
			toServer.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}