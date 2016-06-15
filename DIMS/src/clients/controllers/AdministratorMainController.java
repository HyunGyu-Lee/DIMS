package clients.controllers;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.util.Duration;
import clients.SceneManager;
import clients.customcontrols.CalendarObject;
import clients.customcontrols.CustomDialog;
import clients.customcontrols.ScheduleObject;
import files.FileReciever;
import tools.NetworkProtocols;
import tools.Statics;
import tools.Toolbox;

public class AdministratorMainController implements Initializable {

	
	@FXML Tab MessageTabTest;
	@FXML Button answer_btn;
	
	boolean isDraging = false;
	boolean classcheck = false;
	boolean levelcheck = false;
	int check_class;
	int check_level;
	
	AdministratorMainController me;
	SceneManager sManager;
	CustomDialog loadingDialog;
	LoadingDialogController loadingController;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private ArrayList<String> rList;
	private JSONArray jarray;
	private String uID, uName;
	
	private JSONArray checkjarray;
	
	@FXML TextField MsgSenderField;
	
	/* 화면 최상단 */
	@FXML StackPane stack;
	@FXML Label idField;
	@FXML Label dateText, dateTime;
	@FXML BorderPane MAINPANE;
	
	/* 제출서류 관리 */
	@FXML AnchorPane SUBMIT_VIEW;
	@FXML Label submitTitle, submitDeadLine, processArea;
	@FXML ListView<HBox> submitListView;
	private ObservableList<HBox> submitListViewData;
	@FXML Button addSubmitBtn, disSubmitBtn, emailSubmitBtn, saveSubmitBtn;
	@FXML AnchorPane processBox;
	@FXML Label currentProcess, maxProcess;
	@FXML ProgressIndicator indicator;
	
	/* 일정 관리 메뉴 */
	@FXML BorderPane Schedule_Main;
	@FXML AnchorPane schedule_board;
	@FXML ComboBox<String> schedule_view_mode;
	private ArrayList<ScheduleObject> sObjList = new ArrayList<ScheduleObject>();
	private String SCHEDULE_DISPLAY_MODE = "달력모드";
	private JSONArray SCHEDULE_DISPLAY_DATA;
	private String startDateString="", endDateString = "";
		/* 모아보기 메뉴 */
		@FXML DatePicker startDate, endDate;
		@FXML CheckBox search_daily, search_weekly, search_monthly;
		
		/* 일정객체 수정메뉴 */
		@FXML AnchorPane modifyPanel;
		private Label scheduleID;
		@FXML TextField modiTitle;
		@FXML DatePicker modiDate;
		@FXML ComboBox<String> hourPicker;
		@FXML ComboBox<String> minitePicker;		
		@FXML CheckBox cate_important, cate_event, cate_normal;
		@FXML TextArea contentArea;
		
		
	/* 미디어 플레이어 메뉴 */
	@FXML BorderPane MEDIA;							// 미디어 탭
	@FXML MediaView MEDIA_VIEW;						// 미디어 출력 영역
	@FXML Slider TIME_SLIDER;						// 동영상 타임 슬라이더
	@FXML Label TIME_TEXT;							// 동영상 시간 출력 - 현재 / 총
	@FXML Label MEDIA_TITLE;						// 동영상 제목 출력
	private MediaPlayer player;
	// playTest()	: 동영상 올리기 테스트 버튼
	// onPlay()		: 재생하기
	// onPause()	: 일시정지하기
	// onStop()		: 중지하기
	
	
	/* 게시판 메뉴 */
	@FXML BorderPane BOARD;							// 게시판 탭
	@FXML VBox BOARD_LIST_VIEW;						// 게시판 - 게시글 리스트 확인 뷰
	@FXML ListView<HBox> BOARD_LIST;				// 게시판 - 리스트
	@FXML VBox BOARD_CONTENT_VIEW;					// 게시판 - 게시글 본문 확인 뷰
		@FXML Label BOARD_CONTENT_TITILE;			// 게시판 - 게시글 본문 제목
		@FXML Label BOARD_CONTENT_CATEGORY;			// 게시판 - 게시글 카테고리
		@FXML TextArea BOARD_CONTENT_AREA;			// 게시판 - 게시글 본문
		@FXML Label BOARD_CONTENT_CREATOR;			// 게시판 - 게시글 작성자
		@FXML Label BOARD_CONTENT_CREATE_AT;		// 게시판 - 게시글 작성일자
	private ObservableList<HBox> boardListData;		// 게시판 - 리스트 데이터
	@FXML VBox BOARD_WRITE;							// 게시판 - 게시글 작성
	@FXML ComboBox<String> BOARD_CATEGORY_SELECTOR;	// 게시판 - 검색 시 카테고리 선택할 콤보박스
	@FXML TextField BOARD_TITLE_FIELD;				// 게시판 - 검색 키워드를 입력하는곳
	@FXML TextField BOARD_TITLE;					// 게시판 - 작성글 제목
	@FXML TextArea	BOARD_CONTENT;					// 게시판 - 작성글 본문
	@FXML ComboBox<String> BOARD_W_CSELECTOR;		// 게시판 - 작성 시 카테고리 선택할 콤보박스
	@FXML HBox board_tab_btn_box;					// 게시판 - 카테고리 리스트
	
	// onNotice()	 : 공지사항 버튼 클릭
	// onRequest()   : 건의사항 버튼 클릭
	// onFree()		 : 자유게시판 버튼 클릭
	// onBoardSearch : 게시판 글 검색 버튼 클릭
	
	 /* 메세지 메뉴 */
	 @FXML TabPane MESSAGE;                     // 메세지 탭
	 @FXML BorderPane USERINFO;              // 신상정보조회 페인
	 @FXML TabPane STUDNETMANAGER;           // 학생 상벌점 관리 페인
	 @FXML TextField searchField;               // 메세지 수신자 검색 필드
	 @FXML ListView<HBox> recieverList;            // 메세지 수신자 목록
	 @FXML ListView<HBox> messageList;         // 메세지 수신 목록
	 @FXML ListView<HBox> StudentList;         // 학생 조회 목록
	 @FXML ListView<HBox> sendmessageList;
	 private ObservableList<HBox> recieverListData;   // 수신자 목록 데이터
	 private ObservableList<HBox> recievermessageListData; // 메시지 수신 데이터
	 private ObservableList<HBox> StudentListData; // 학생 조회 목록 데이터
	 private ObservableList<HBox> StudentManagerListData; // 상벌점 조회 목록 데이터
	 private ObservableList<HBox> sendmessageListData;
	 @FXML TextArea display_reciever;            // 선택된 메세지 수신자 목록 (학번, 이름)
	 @FXML TextField msgTitle;                  // 메세지 제목
	 @FXML TextArea msgContent;                  // 메세지 본문
	 @FXML TextField MsgRecieverField;          // 받은 메세지함 텍스트필드
	 private int saved = -1;
	 // onAdd()       : recieverList에서 선택한 수신자를 display_reciever에 추가 
	 // onSendMsg()  : 메세지 전송
	 // onSearch()   : searchField의 내용 변화에 따라 recieverList의 내용을 바꿈

	 /* 신상정보 조회 메뉴 */
	 @FXML TextField studenttextField;           // 학생 신상정보조회 검색 필드
	 ObservableList<String> alloption = FXCollections.observableArrayList("소속학과","컴퓨터정보공학과","철도경영물류학과 ","철도시설공학과 ","철도운전시스템공학과","철도전기전자공학과","철도차량시스템공학과");
	 ObservableList<String> roomoption = FXCollections.observableArrayList("층   별","백인관 1층","백인관 2층","백인관 3층","백인관 4층"); 
	 @FXML ComboBox<String> select_find_mode;			//신상정보 모아보기 콤보 박스
	 @FXML ComboBox<String> select_room_mode;			//신상정보 방번호 기준으로 정렬하는 콤보박스
	 
	 /* 외박 관리 메뉴 */
	 private ObservableList<HBox> StudentMenagerListCheckData; // 상벌점 부여 리스트 데이터
	 @FXML TextField WeabakField;				//외박학생조회 텍스트필드
	 @FXML ListView<HBox> StudentWeabakList;     // 학생 외박 관리 조회 목록
	 private ObservableList<HBox> StudentWeabakListData; //외박 목록 데이터
	 
	 /* 상벌점 관리 메뉴 */
	 @FXML TextField PMField;                    //학생 개인 상벌점조회 텍스트필드
	 @FXML TextField PMListField;                //학생 상벌점 부여 조회 목록 필드 
	 @FXML ListView<HBox> StudentMenagerListCheck; // 학생 상벌점 부여 목록 리스트;
	 @FXML ListView<HBox> StudentManagerList;    // 학생 상벌점 관리 조회 목록
	 
	 @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		/* 일정관리탭 초기화 */
		
		startDate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				startDateString = startDate.getValue().toString();
			}
		});
		
		endDate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				endDateString = endDate.getValue().toString();
			}
		});
		
		cate_important.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cate_event.setSelected(false);
				cate_normal.setSelected(false);
			}
		});
		
		cate_event.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cate_important.setSelected(false);
				cate_normal.setSelected(false);
			}
		});
		
		cate_normal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cate_important.setSelected(false);
				cate_event.setSelected(false);
			}
		});
		
		search_daily.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				search_weekly.setSelected(false);
				search_monthly.setSelected(false);
			}
		});
		search_weekly.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				search_daily.setSelected(false);
				search_monthly.setSelected(false);
			}
		});
		search_monthly.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				search_daily.setSelected(false);
				search_weekly.setSelected(false);
			}
		});
		
		scheduleID = new Label();
		scheduleID.setVisible(false);
		modifyPanel.getChildren().add(scheduleID);
		schedule_view_mode.getItems().addAll("그리드모드", "리스트모드", "달력모드");
		
		hourPicker.getItems().addAll("00","01","02","03","04","05","06","07","08","09","10"
									,"11","12","13","14","15","16","17","18","19","20","21"
									,"22","23");
		
		minitePicker.getItems().addAll("00","01","02","03","04","05","06","07","08","09"
									,"10","11","12","13","14","15","16","17","18","19"
									,"20","21","22","23","24","25","26","27","28","29"
									,"30","31","32","33","34","35","36","37","38","39"
									,"40","41","42","43","44","45","46","47","48","49"
									,"50","51","52","53","54","55","56","57","58","59");
		
		modifyPanel.setDisable(true);
		/* 시간 초기화 */
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
		
		/* 메세지탭 초기화 */
		display_reciever.setEditable(false);
		recieverListData = FXCollections.observableArrayList();
		recievermessageListData = FXCollections.observableArrayList();
		boardListData = FXCollections.observableArrayList();
		StudentListData = FXCollections.observableArrayList();
	    StudentManagerListData = FXCollections.observableArrayList();
	    sendmessageListData = FXCollections.observableArrayList();

	    StudentWeabakListData = FXCollections.observableArrayList();
	    StudentMenagerListCheckData = FXCollections.observableArrayList();
	    
		
		/* 게시판탭 초기화 */
		BOARD_WRITE.setVisible(false);
		BOARD_LIST_VIEW.setVisible(true);
		BOARD_CONTENT_VIEW.setVisible(false);
	
		checkjarray = new JSONArray();
		
		shutdown();
		MAINPANE.setVisible(true);
	}
	
	public void INIT_CONTROLLER(SceneManager manager, ObjectInputStream fromServer, ObjectOutputStream toServer)
	{
		this.sManager = manager;
		this.fromServer = fromServer;
		this.toServer = toServer;
	}

	public void shutdown()
	{
		SUBMIT_VIEW.setVisible(false);
		BOARD.setVisible(false);
		MESSAGE.setVisible(false);
		MEDIA.setVisible(false);
		STUDNETMANAGER.setVisible(false);
		USERINFO.setVisible(false);
		Schedule_Main.setVisible(false);
		MAINPANE.setVisible(false);
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
			synchronized (this)
			{
				try
				{
					// 이건 해도 되고 안해도되는데.. 가끔 로그인 죽기전에 얘가 먼저시작될때가 있어서 1초 기다리게함
					System.out.println("LoginController 리스너 종료될때까지 대기");
					System.out.println("여기에 로딩을 구현해볼까나");
					this.wait(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("AdministratorMainController 스레드 시작");
			JSONObject o = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_LIST_REQUEST);
			o.put("category", 1);
			
			try
			{
				while(true)
				{
					try
					{
						JSONObject line = (JSONObject)fromServer.readObject();
						System.out.println(line.toJSONString());
						String type = line.get("type").toString();
						
						if(type.equals(NetworkProtocols.EXIT_RESPOND))
						{
							break;
						}
						else if(type.equals(NetworkProtocols.RECIEVE_READY))
						{
							JSONObject protocol = new JSONObject();
							protocol.put("type", NetworkProtocols.RECIEVE_READY_OK);
							sendProtocol(protocol);
						}
						else if(type.equals(NetworkProtocols.SHOW_MESSAGE_DIALOG))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									CustomDialog.showMessageDialog(line.get("msg").toString(), sManager.getStage());
								}
							});
						}
						else if(type.equals(NetworkProtocols.PLZ_REQUEST))
						{
							JSONObject protocol = new JSONObject();
							protocol.put("type", NetworkProtocols.WINDOW_INIT_PROPERTY);
							protocol.put("category", "공지사항");
							sendProtocol(protocol);							
						}
						else if(type.equals(NetworkProtocols.WINDOW_INIT_PROPERTY))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									uName = line.get("uName").toString();
									uID = line.get("uID").toString();
									idField.setText(uName+"님");
								}
							});
						}
						else if(type.equals(NetworkProtocols.MESSAGE_USER_LIST_RESPOND))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									createReciverList((ArrayList<String>)line.get("rcList"));
								}
							});
						}
						else if(type.equals(NetworkProtocols.ENROLL_BOARD_RESPOND))
						{
							BOARD_WRITE.setVisible(false);
							BOARD_LIST_VIEW.setVisible(true);

							toServer.writeObject(o);
							toServer.flush();
						}
						else if(type.equals(NetworkProtocols.BOARD_LIST_RESPOND))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									
									BOARD_W_CSELECTOR.getItems().clear();
									BOARD_CATEGORY_SELECTOR.getItems().clear();
									board_tab_btn_box.getChildren().clear();
									
									createBoardCategoryList((JSONArray)line.get("category_list"));
									createBoardList((JSONArray)line.get("board_list"));
								}
							});
						}
						else if(type.equals(NetworkProtocols.MESSAGE_SEND_RESPOND))
		                {
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									CustomDialog.showMessageDialog("메세지 발송 성공!", sManager.getStage());
									JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST);
			                        sendProtocol(json);
			                           
			                        display_reciever.setText("");
			                        msgTitle.setText("");
			                        msgContent.setText("");
								}
							});
						}
						else if(type.equals(NetworkProtocols.MESSAGE_RECIEVE_LIST_RESPOND))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									jarray = (JSONArray)line.get("message_list");
									createRecivedmessage(jarray);
									shutdown();
									MESSAGE.setVisible(true);
								}
							});
						}
						else if(type.equals(NetworkProtocols.MESSAGE_SEND_LIST_RESPOND))
						{
								Platform.runLater(new Runnable() {
								@Override
								public void run() {
									createSendMassageList((JSONArray)line.get("message_list"));
								}
							});
						}
						else if(type.equals(NetworkProtocols.BOARD_CONTENT_RESPOND))
						{
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									BOARD_CONTENT_AREA.setEditable(true);
									BOARD_CONTENT_TITILE.setText(line.get("게시글제목").toString());
									BOARD_CONTENT_CATEGORY.setText(line.get("카테고리").toString());
									BOARD_CONTENT_CREATOR.setText(line.get("이름").toString());
									BOARD_CONTENT_AREA.setText(line.get("게시글본문").toString());
									BOARD_CONTENT_CREATE_AT.setText(line.get("작성일자").toString());
									BOARD_CONTENT_AREA.setEditable(false);
									BOARD_LIST_VIEW.setVisible(false);
									BOARD_WRITE.setVisible(false);
									BOARD_CONTENT_VIEW.setVisible(true);									
								}
							});
						}
						else if(type.equals(NetworkProtocols.BOARD_SEARCH_RESPOND))
						{
							JSONArray arr = (JSONArray)line.get("boardlist");
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									createBoardList(arr);									
								}
							});

						}
						else if(type.equals(NetworkProtocols.BOARD_NO_SEARCH_RESULT))
						{
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									CustomDialog.showMessageDialog("게시물이 존재하지 않습니다.", sManager.getStage());
								}
							});
						}
						else if(type.equals(NetworkProtocols.VIDIO_RESPOND))
						{
							byte[] videoData = (byte[])line.get("vdata");
							Files.write(Paths.get("./tmp.mp4"), videoData);
							
							File file = new File("./tmp.mp4");
							
							MEDIA_TITLE.setText(file.getName());
							
							Media me = new Media(file.toURI().toString());
							player = new MediaPlayer(me);

							player.currentTimeProperty().addListener(new InvalidationListener() {
								
								@Override
								public void invalidated(Observable observable) {
									TIME_TEXT.setText(Toolbox.formatTime(player.getCurrentTime(), player.getTotalDuration()));
								}
							});	
							
							TIME_SLIDER.valueProperty().addListener(new InvalidationListener() {
								
								@Override
								public void invalidated(Observable observable) {
									player.seek(player.getTotalDuration().multiply(TIME_SLIDER.getValue() / 100.0));
									
								}
							});
							
							double w=1024,h=768;
							
							MEDIA_VIEW.setFitWidth(w);
							MEDIA_VIEW.setFitHeight(h);
							MEDIA_VIEW.setMediaPlayer(player);
						}
						else if(type.equals(NetworkProtocols.MESSAGE_CONTENT_RESPOND))
						{
							System.out.println("응답");
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									CustomDialog cd = new CustomDialog(Statics.CHECK_MESSAGE_FXML, Statics.CHECK_MESSAGE_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
									CheckMessageDialog_Controller con = (CheckMessageDialog_Controller) cd.getController();
									con.setWindow(cd);
									con.setProperty(line);
									
									cd.showAndWait();
									
									String v = (String)cd.getUserData();
									System.out.println(v);									
								}
							});

						}
						else if(type.equals(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_RESPOND))
						{
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									SCHEDULE_DISPLAY_DATA = (JSONArray)line.get("todays");
									System.out.println(SCHEDULE_DISPLAY_DATA.toJSONString());
									if(SCHEDULE_DISPLAY_MODE.equals("그리드모드"))
									{
										createScheduleBoard_GRID(SCHEDULE_DISPLAY_DATA);										
									}
									else if(SCHEDULE_DISPLAY_MODE.equals("리스트모드"))
									{
										createScheduleBoard_LIST(SCHEDULE_DISPLAY_DATA);
									}
									else if(SCHEDULE_DISPLAY_MODE.equals("달력모드"))
									{
										createScheduleBoard_CELL(SCHEDULE_DISPLAY_DATA);
									}
									shutdown();
									Schedule_Main.setVisible(true);
								}
							});
						}
						else if(type.equals(NetworkProtocols.BOARD_MAIN_RESPOND))
						{
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									BOARD_W_CSELECTOR.getItems().clear();
									BOARD_CATEGORY_SELECTOR.getItems().clear();
									board_tab_btn_box.getChildren().clear();
									
									createBoardCategoryList((JSONArray)line.get("category_list"));
									
									createBoardList((JSONArray)line.get("board_list"));
									shutdown();
									BOARD.setVisible(true);
								}
							});
						}
						else if(type.equals(NetworkProtocols.ADMIN_BOARD_DELETE_RESPOND))
						{
							Platform.runLater(()->{
								CustomDialog.showMessageDialog("게시글이 삭제되었습니다.", sManager.getStage());
							});
							JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_LIST_REQUEST);
							json.put("category", line.get("show-category"));
							sendProtocol(json);	

						}
						else if(type.equals(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_RESPOND))
		                {
		                     Platform.runLater(new Runnable() {
		                     
		                     @Override
		                     public void run() {
		                        jarray = (JSONArray)line.get("message_list");
		                        createRecivedmessage(jarray);
		                        shutdown();
		                        MESSAGE.setVisible(true);
		                     }
		                     });
		                }
						else if(type.equals(NetworkProtocols.MESSAGE_RICIEVER_SELECT_DELETE_RESPOND))
		                {
							Platform.runLater(new Runnable() {
		                        public void run() {
		                           CustomDialog.showMessageDialog("선택한 메세지들이 삭제되었습니다.", sManager.getStage());
		                           JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST);
		                           sendProtocol(json);
		                        }
		                     });
		                 }
						else if(type.equals(NetworkProtocols.MONTHLY_SCHEDULE_VIEW_RESPOND))
						{
							SCHEDULE_DISPLAY_DATA = (JSONArray)line.get("todays");
							createScheduleBoard_CELL(SCHEDULE_DISPLAY_DATA, line.get("viewableDate").toString());
						}
						//신상정보 조회  영훈
						else if(type.equals(NetworkProtocols.SHOW_USER_INFO_TAP_RESPOND))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									jarray = (JSONArray)line.get("user_list");
									createStudentSearch(jarray);
									shutdown();
									USERINFO.setVisible(true);
								}
							});
						}
						else if(type.equals(NetworkProtocols.USER_CONTENT_RESPOND))
						{
							System.out.println("응답");
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									CustomDialog cd = new CustomDialog(Statics.CHECK_STUDENT_FXML, Statics.STUDENT_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
									CheckStudentDialog_Controller con = (CheckStudentDialog_Controller) cd.getController();
									System.out.println(con);
									con.setWindow(cd);
									con.setProperty(line);
									
									cd.showAndWait();
									
									String v = (String)cd.getUserData();
									System.out.println(v);									
								}
							});
						}
						else if(type.equals(NetworkProtocols.WEABAK_INFO_TAP_RESPOND))
						{
							Platform.runLater(new Runnable() {
								public void run() {
									jarray = (JSONArray)line.get("weabak_list");
									createWeabakManager(jarray);
									shutdown();
									STUDNETMANAGER.setVisible(true);
									
								}
							});
						}
						else if(type.equals(NetworkProtocols.PLUS_MINUS_TAP_RESPOND))
						{
							Platform.runLater(new Runnable() {
								public void run() {
									jarray = (JSONArray)line.get("plusminus_list");
									createPlusMinusSearch(jarray);
								}
							});
						}
						else if(type.equals(NetworkProtocols.WEQBAK_CONTENT_RESPOND))
						{
									Platform.runLater(new Runnable() {
										public void run() {
											System.out.println(" //외박 컨텐트 실행");
											JSONObject json = (JSONObject)line.get("weabak_content_list");
											System.out.println(json.get("이름"));
											CustomDialog cd = new CustomDialog(Statics.CHECK_WEABAK_FXML,Statics.WEABAK_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
											
											CheckWeabakDialog_Controller cwdc = (CheckWeabakDialog_Controller)cd.getController();
											cwdc.setWindow(cd);
											cwdc.setProperty(json);
											
											cd.showAndWait();
											
											JSONObject action = (JSONObject)cd.getUserData();
											
											if(action.get("action").toString().equals("not"))
											{
												return;
											}
											else
											{
												action.put("type", NetworkProtocols.WEABAK_PROCESS_REQUEST);
												action.put("reqNo", json.get("외박번호"));
												System.out.println(action.toJSONString());
												sendProtocol(action);
											}
										}
									});
						}
						else if(type.equals(NetworkProtocols.PLUS_MINUS_TAP_INFO_RESPOND))
						{
							Platform.runLater(new Runnable() {
								public void run() {
									jarray = (JSONArray)line.get("plus_minus_check_list");
									StudentPlusMinusManagerList(jarray);
								}
							});
						}
						else if(type.equals(NetworkProtocols.WEABAK_PROCESS_RESPOND))
						{
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									CustomDialog.showMessageDialog("처리되었습니다.", sManager.getStage());								}
							});
							JSONObject obj = Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_INFO_TAP_REQUEST);
							obj.put("category", "대기중");
							sendProtocol(obj);
						}
						else if(type.equals(NetworkProtocols.PLUS_MINUS_ASSGIN_RESPOND))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									CustomDialog cd = new CustomDialog(Statics.PLUS_MINUS_ASSIGN_FXML, Statics.PASSWORD_FIND_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
									PlusMinusAssignDialog_Controller ps = (PlusMinusAssignDialog_Controller)cd.getController();
									ps.setProperty(line);
									ps.setWindow(cd);
									
									cd.showAndWait();
									
									JSONObject json = (JSONObject)cd.getUserData();
									if(json!=null)
									{
										sendProtocol(json);
									}
									
								}
							});
						}
						else if(type.equals(NetworkProtocols.PLUS_MINUS_OVER_RESPOND))
						{
							JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_TAP_REQUEST);
							sendProtocol(json);
						}
						else if(type.equals(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND))
						{
							Platform.runLater(new Runnable() {
								public void run() {
									jarray = (JSONArray)line.get("user_list");
									createStudentSearch(jarray);
									StudentList.refresh();
								}
							});
						}
						else if(type.equals(NetworkProtocols.ADMIN_ADD_TAP_RESPOND))
						{
							Platform.runLater(()->{
								CustomDialog.showMessageDialog("카테고리가 추가되었습니다.", sManager.getStage());
							});
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.BOARD_MAIN_REQUEST));
						}
						else if(type.equals(NetworkProtocols.ADMIN_CATEGORY_DELETE_RESPOND))
						{
							Platform.runLater(()->{
								CustomDialog dlg = new CustomDialog(Statics.CATEGORY_DELETE_DIALOG_FXML, Statics.CATEGORY_DELETE_DIALOG_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
								CategoryDeleteDialogController con = (CategoryDeleteDialogController)dlg.getController();
								con.setWindow(dlg);
								con.setProperty((JSONArray)line.get("category-list"));
								dlg.showAndWait();
								JSONObject action = (JSONObject)dlg.getUserData();
								
								if(action!=null)
								{
									JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_DELETE_CATEGORY_COUNT_REQUEST);
									request.put("category", (int)action.get("selected"));
									sendProtocol(request);
								}
								
							});
						}
						else if(type.equals(NetworkProtocols.ADMIN_DELETE_CATEGORY_COUNT_RESPOND))
						{
							int cnt = (int)line.get("count");
							
							Platform.runLater(()->{
								if(cnt==0)
								{
									//삭제
									JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_DELETE_FINAL_REQUEST);
									request.put("category", line.get("category"));
									sendProtocol(request);
								}
								else
								{
									// 물어보기
									int option = CustomDialog.showConfirmDialog("선택한 카테고리를 삭제하면 해당 카테고리의 모든 글이 삭제됩니다.", sManager.getStage());
									if(option==CustomDialog.OK_OPTION)
									{
										//삭제
										JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_DELETE_FINAL_REQUEST);
										request.put("category", line.get("category"));
										sendProtocol(request);
									}
									else
									{
										CustomDialog.showMessageDialog("카테고리 삭제를 취소하셨습니다.", sManager.getStage());
									}
								}
							});
						}
						else if(type.equals(NetworkProtocols.ADMIN_DELETE_FINAL_RESPOND))
						{
							Platform.runLater(()->{
								CustomDialog.showConfirmDialog("카테고리가 성공적으로 삭제되었습니다.", sManager.getStage());
							});
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.BOARD_MAIN_REQUEST));
						}
						else if(type.equals(NetworkProtocols.MESSAGE_RICIEVER_ALL_DELETE_RESPOND))
						{
							Platform.runLater(()->{
								CustomDialog.showConfirmDialog("메세지가 삭제되었습니다.", sManager.getStage());
							});
							
							if(line.get("resType").equals("R"))
							{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RECIEVE_LIST_REQUEST));
							}
							else
							{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST));								
							}
						}
						else if(type.equals(NetworkProtocols.ADMIN_SUBMIN_MAIN_RESPOND))
						{
							// 없는 경우
							if(line.get("submit-process").equals("no-data"))
							{
								Platform.runLater(()->{
									createNoSubmitUI();
								});
							}
							// 있는 경우
							else
							{
								Platform.runLater(()->{
									createExistedSubmitUI(line);
								});
							}
						}
						else if(type.equals(NetworkProtocols.ADMIN_SUBMIT_ENROLL_RESPOND))
						{
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIN_MAIN_REQUEST));
						}
						else if(type.equals(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_ASK_RESPOND))
						{
							if(line.get("result").equals("re-ask"))
							{
								Platform.runLater(()->{
									int re = CustomDialog.showConfirmDialog("미제출서류가 있습니다. 마감하시겠습니까?", sManager.getStage());
									if(re==CustomDialog.OK_OPTION)
									{
										sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_REQEUST));
									}
									else
									{
										CustomDialog.showMessageDialog("마감을 취소하셨습니다.", sManager.getStage());
									}
								});
							}
							else
							{
								Platform.runLater(()->{
									sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_REQEUST));
								});
							}
						}
						else if(type.equals(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_RESPOND))
						{
							Platform.runLater(()->{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIN_MAIN_REQUEST));
								CustomDialog.showMessageDialog("마감처리 됐습니다.", sManager.getStage());
							});							
						}
						else if(type.equals(NetworkProtocols.ADMIN_LOCAL_SAVE_NOTIFICATION))
						{
							JSONObject notification = line;
							
							if(loadingDialog==null)
							{
								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										processBox.setVisible(false);
										currentProcess.setText("0");
										maxProcess.setText("0");
										indicator.setProgress(0);
										loadingDialog = new CustomDialog(Statics.LOADING_DIALOG_FXML, Statics.LOADING_DIALOG_TITLE, sManager.getStage(), Modality.NONE);
										loadingController = (LoadingDialogController)loadingDialog.getController();
										loadingController.setMessage("서버로부터 제출자료 내려받는중...");
										loadingController.setProperty(notification.get("name").toString(), (int)notification.get("size"));
										loadingDialog.show();

										FileReciever re = new FileReciever(Statics.DIMS_FILE_SERVER_IP_ADDRESS, Statics.DIMS_FILE_SERVER_PORT_NUMBER);
										re.setSavePathVariable(Statics.DEFAULT_DOWNLOAD_DIRECTORY+"submitted_data.zip");
										re.setUI(loadingDialog, sManager.getStage());
										
										
										
										re.addDownloadFinishEventHandler(new Runnable() {
											@Override
											public void run() {
												try {
													Runtime.getRuntime().exec("explorer.exe /select,"+Statics.DEFAULT_DOWNLOAD_DIRECTORY+"submitted_data.zip");
												} catch (IOException e) {
													e.printStackTrace();
												}
												Platform.runLater(()->{
													loadingDialog.close();
												});
											}
										});
										re.openConnection();
									}
								});
							}
							else
							{
								loadingController.setProperty(notification.get("name").toString(), (int)notification.get("size"));

								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										processBox.setVisible(false);
										currentProcess.setText("0");
										maxProcess.setText("0");
										indicator.setProgress(0);
										String savePathVariable = Statics.DEFAULT_DOWNLOAD_DIRECTORY+"submitted_data.zip";
										FileReciever re = new FileReciever("localhost", 9090);
										re.setSavePathVariable(savePathVariable);
										re.setUI(loadingDialog, sManager.getStage());
										
										re.addDownloadFinishEventHandler(()->{
											try {
												Runtime.getRuntime().exec("explorer.exe /select,"+Statics.DEFAULT_DOWNLOAD_DIRECTORY+"submitted_data.zip");
											} catch (IOException e) {
												e.printStackTrace();
											}
											Platform.runLater(()->{
												loadingDialog.close();
											});
											}
										);
										re.openConnection();
										loadingDialog.show();
									}
								});
							}
						}
						else if(type.equals(NetworkProtocols.ADMIN_LOCAL_SAVE_RESPOND))
						{
							Platform.runLater(()->{
								processBox.setVisible(true);
								int cur = (int)line.get("progress-current");
								int max = (int)line.get("progress-max");
								indicator.setProgress((double)cur/(double)max);
								currentProcess.setText(cur+"");
								maxProcess.setText(max+"");
							});
						}
						else if(type.equals(NetworkProtocols.EMAIL_SEND_RESPOND))
						{
							Platform.runLater(()->{
								CustomDialog.showMessageDialog("메일 발송 성공", sManager.getStage());
							});
						}
						else if(type.equals(NetworkProtocols.EMAIL_FILE_SIZE_LIMIT))
						{
							Platform.runLater(()->{
								CustomDialog.showMessageDialog("첨부되는 파일 용량은 10MB보다 작아야합니다.", sManager.getStage());
							});
						}
						else if(type.equals(NetworkProtocols.EMAIL_MAX_COUNT_EXCEED))
						{
							Platform.runLater(()->{
								CustomDialog.showMessageDialog("첨부파일의 갯수는 10개이하여야 합니다.", sManager.getStage());
							});
						}
						else if(type.equals(NetworkProtocols.MESSAGE_RICIEVER_ALL_DELETE_RESPOND))
		                  {
		                     Platform.runLater(new Runnable() {
		                        public void run() {
		                           CustomDialog.showMessageDialog("전체삭제되었습니다.", sManager.getStage());
		                           JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST);
		                           sendProtocol(json);
		                        }
		                     });
		                  }
		                  else if(type.equals(NetworkProtocols.MESSAGE_SEND_SELECT_DELETE_RESPOND))
		                  {
		                     Platform.runLater(new Runnable() {
		                        public void run() {
		                           CustomDialog.showMessageDialog("선택한 메세지들이 삭제되었습니다.", sManager.getStage());
		                           JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST);
		                           sendProtocol(json);
		                           
		                        
		                        }
		                     });
		                  }
		                  else if(type.equals(NetworkProtocols.MESSAGE_SEND_ALL_DELETE_RESPOND))
		                  {
		                     Platform.runLater(new Runnable() {
		                        public void run() {
		                           CustomDialog.showMessageDialog("전체삭제되었습니다.", sManager.getStage());
		                           JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST);
		                           sendProtocol(json);
		                        }
		                     });
		                  }
		                  else if(type.equals(NetworkProtocols.STUDENT_SORT_OVERLAP_RESPOND))
		                  {
		                     Platform.runLater(new Runnable() {
		                        public void run() {
		                           jarray = (JSONArray)line.get("user_list");
		                           createStudentSearch(jarray);
		                           StudentList.refresh();
		                        }
		                     });
		                  }
					}
					catch(ClassNotFoundException e)
					{
						System.out.println(e);
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			System.out.println("AdministratorMainController 스레드 종료");
		}
	}
	
	public void createBoardList(JSONArray data)
	{
		boardListData.removeAll(boardListData);
		
		HBox box = new HBox();
		Label l1,l2,l3,l4;
		
		l1 = new Label("번호");
		l2 = new Label("작성자");
		l3 = new Label("제 목");
		l4 = new Label("작성일자");
		
		l1.setMaxWidth(400);
		l2.setMaxWidth(400);
		l3.setMaxWidth(1200);
		l4.setMaxWidth(400);
		
		l1.setTextFill(Color.web("#ffffff"));
		l2.setTextFill(Color.web("#ffffff"));
		l3.setTextFill(Color.web("#ffffff"));
		l4.setTextFill(Color.web("#ffffff"));
		
		l1.setStyle("-fx-background-color : gray; -fx-border-color : black;");
		l2.setStyle("-fx-background-color : gray; -fx-border-color : black;");
		l3.setStyle("-fx-background-color : gray; -fx-border-color : black;");
		l4.setStyle("-fx-background-color : gray; -fx-border-color : black;");
		
		l1.setAlignment(Pos.CENTER);
		l2.setAlignment(Pos.CENTER);
		l3.setAlignment(Pos.CENTER);
		l4.setAlignment(Pos.CENTER);
		
		HBox.setHgrow(l3, Priority.ALWAYS);
		HBox.setHgrow(l2, Priority.ALWAYS);
		HBox.setHgrow(l4, Priority.ALWAYS);
		
		box.getChildren().addAll(l1,l2,l3,l4);
		
		boardListData.add(box);
		
		JSONArray arr = data;
		
		for(Object o : arr)
		{
			JSONObject target = (JSONObject)o;
			int no = Integer.parseInt(target.get("No").toString());
			String name = target.get("이름").toString();
			String title = target.get("게시글제목").toString();
			Date create_at = (Date) target.get("작성일자");
			
			HBox item = new HBox();
			Label numLabel = new Label("   "+no+"  ");
			numLabel.setStyle("-fx-border-color : black");
			numLabel.setAlignment(Pos.CENTER);
			numLabel.setMaxWidth(400);
			Label nLabel = new Label(name);
			nLabel.setStyle("-fx-border-color : black");
			nLabel.setMaxWidth(400);
			nLabel.setAlignment(Pos.CENTER);
			Label tLabel = new Label(title);
			tLabel.setMaxWidth(1200);
			tLabel.setStyle("-fx-border-color : black");
			tLabel.setMaxWidth(Double.MAX_VALUE);
			tLabel.setAlignment(Pos.CENTER);
			Label cLabel = new Label(create_at.toString());
			cLabel.setMaxWidth(400);
			cLabel.setStyle("-fx-border-color : black");
			cLabel.setAlignment(Pos.CENTER);
			HBox.setHgrow(tLabel, Priority.ALWAYS);
			HBox.setHgrow(nLabel, Priority.ALWAYS);
			HBox.setHgrow(cLabel, Priority.ALWAYS);
			item.getChildren().addAll(numLabel, nLabel, tLabel, cLabel);
			item.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@SuppressWarnings("unchecked")
				@Override
				public void handle(MouseEvent event) {
					if(event.getClickCount()==2)
					{
						JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_CONTENT_REQUEST);
						req.put("No", no);
						try
						{
							toServer.writeObject(req);
							toServer.flush();
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
					}
					
				}
			});
			
			boardListData.add(item);
		}
		
		BOARD_LIST.setItems(boardListData);
		BOARD_LIST.setVisible(true);
	}
	

	public void createReciverList(ArrayList<String> data)
	{
		recieverListData.removeAll(recieverListData);
		for(String t : data)
		{
			String[] info = t.split(",");
			Label left = new Label(info[0]);
			left.setAlignment(Pos.CENTER);
			left.setStyle("-fx-border-color : black");
			left.setMaxWidth(Double.MAX_VALUE);
			left.setMaxHeight(Double.MAX_VALUE);
			Label right = new Label(info[1]);
			right.setAlignment(Pos.CENTER);
			right.setMaxWidth(Double.MAX_VALUE);
			right.setMaxHeight(Double.MAX_VALUE);
			right.setStyle("-fx-border-color : black");
			HBox item = new HBox();
			//item.setMaxSize(Double.MAX_VALUE, maxHeight);
			item.getChildren().addAll(left, right);
			HBox.setHgrow(right, Priority.ALWAYS);
			HBox.setHgrow(left, Priority.ALWAYS);
			item.setAlignment(Pos.CENTER);
			recieverListData.add(item);
		}
		
		recieverList.setItems(recieverListData);
	}
	
	public void createRecivermessage(ArrayList<String> data)
	{
		  @SuppressWarnings("unused")
		  Boolean check ;
	      recievermessageListData.removeAll(recievermessageListData);
	      for(String t : data)
	      {
	    	 System.out.println(t);
	         String[] info = t.split(",");
	         CheckBox left = new CheckBox();
	         left.setAlignment(Pos.CENTER_LEFT);
	         left.setMaxHeight(Double.MIN_VALUE);
	         left.setMaxWidth(Double.MIN_VALUE);
	         check = left.isSelected();
	         Label Center = new Label(info[0]);
	         Center.setAlignment(Pos.CENTER_LEFT);
	         Center.setMaxHeight(30);
	         Center.setMaxWidth(150);
	         Center.setStyle("-fx-border-color : black");
	         Label right = new Label(info[1]);
	         right.setAlignment(Pos.CENTER);;
	         right.setMaxHeight(Double.MAX_VALUE);
	         right.setMaxWidth(Double.MAX_VALUE);
	         HBox item = new HBox();
	         item.getChildren().addAll(left, Center,right);
	         HBox.setHgrow(right, Priority.ALWAYS);
	         HBox.setHgrow(Center, Priority.ALWAYS);
	         HBox.setHgrow(left, Priority.ALWAYS);
	         item.setAlignment(Pos.CENTER);
	         recievermessageListData.add(item);
	      }
	      messageList.setItems(recievermessageListData);
	      messageList.setVisible(true);
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
	
	@FXML private void onMenu_1()
	{
		/* 1번 메뉴 클릭시 */
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.SHOW_USER_INFO_TAP_REQUEST));
	}
	
	@FXML private void onMenu_2()
	{
		/* 2번 메뉴 클릭시 */
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RECIEVE_LIST_REQUEST));
	}
	
	@FXML private void onMenu_3()
	{
		/* 3번 메뉴 클릭시 */
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.BOARD_MAIN_REQUEST));
	}
	
	@FXML private void onMenu_4()
	{
		/* 4번 메뉴 클릭시 */
		shutdown();
		STUDNETMANAGER.setVisible(true);
	}
	
	@FXML private void onMenu_5()
	{
		/* 5번 메뉴 클릭시 */
		shutdown();
		MEDIA.setVisible(true);
	}
	
	@SuppressWarnings("unchecked")
	@FXML private void onMenu_6()
	{
		JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_REQUEST);
		req.put("viewtype", SCHEDULE_DISPLAY_MODE);
		sendProtocol(req);
	}
	
	@FXML private void playTest()
	{

	}
	
	@FXML private void onPlay()
	{
		player.play();
	}
	
	@FXML private void onPause()
	{
		player.pause();
	}
	
	@FXML private void onStop()
	{
		TIME_SLIDER.setValue(0.0);
		player.stop();
	}
	
	@FXML private void onLogout()
	{
		sManager.doFullscreen(false);
		sManager.changeListenController("ADMIN_MAIN");
		sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
		//sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.LOGOUT_REQUEST));
	}
	
	@FXML private void onAdd()
	{
		// 학번,이름\t학번,이름\t...
		display_reciever.setEditable(true);
		String no = ((Label)recieverList.getSelectionModel().getSelectedItem().getChildren().get(0)).getText();
		String name = ((Label)recieverList.getSelectionModel().getSelectedItem().getChildren().get(1)).getText();
		display_reciever.appendText(no+","+name+"\t");
		display_reciever.setEditable(false);
	}
	
	@SuppressWarnings("unchecked")
	   @FXML private void onSendMsg()
	    {
	      long curTime = System.currentTimeMillis();
	      
	      String data = display_reciever.getText();
	      ArrayList<String> sList = Toolbox.arrToList(data.split("\t"));
	      
	      JSONObject msg = new JSONObject();
	      msg.put("type", NetworkProtocols.MESSAGE_SEND_REQUEST);
	      msg.put("sender", uID);
	      msg.put("reciever", sList);
	      msg.put("msgTitle", msgTitle.getText());
	      msg.put("msgContent", msgContent.getText());
	      msg.put("sendTime", curTime);
	 
	      sendProtocol(msg);	      
	   }

	//JSONObject o = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_LIST_REQUEST);
	//o.put("category", "공지사항");
	//sendProtocol(o);	

	@FXML private void onBoardSearch()
	{
		System.out.println("검색 카테고리 : "+BOARD_CATEGORY_SELECTOR.getSelectionModel().getSelectedIndex());
		System.out.println("검색 키워드    : "+BOARD_TITLE_FIELD.getText());
		
		if(BOARD_CATEGORY_SELECTOR.getValue()==null)return;
		
		String[] keys = {"category","search_key"};
		Object[] values = {BOARD_CATEGORY_SELECTOR.getSelectionModel().getSelectedIndex()+1,BOARD_TITLE_FIELD.getText()};
		
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.BOARD_SEARCH_REQUEST, keys, values));
	}
	
	@FXML private void onWriteCancel()
	{
		BOARD_WRITE.setVisible(false);
		BOARD_LIST_VIEW.setVisible(true);
	}
	
	@FXML private void onWrite()
	{
		String[] ks = {"작성자","게시글제목","게시글본문","카테고리"};
		Object[] vs = {uID, BOARD_TITLE.getText(), BOARD_CONTENT.getText(), BOARD_W_CSELECTOR.getSelectionModel().getSelectedIndex()+1};
		
		System.out.println(Toolbox.createJSONProtocol(NetworkProtocols.ENROLL_BOARD_REQUEST, ks, vs).toJSONString());
		
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ENROLL_BOARD_REQUEST, ks, vs));
	}
	
	@FXML private void onCreateBoard()
	{
		BOARD_LIST_VIEW.setVisible(false);
		BOARD_WRITE.setVisible(true);
		BOARD_CONTENT_VIEW.setVisible(false);
	}
	
	@FXML private void onEditBoard()
	{
		
	}
	
	@SuppressWarnings("unchecked")
	@FXML private void onDeleteBoard()
	{
		
		if(BOARD_LIST.getSelectionModel().getSelectedItem()==null)
		{
			CustomDialog.showMessageDialog("삭제할 게시글을 선택하세요!", sManager.getStage());
			return;
		}
		
		JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_BOARD_DELETE_REQUEST);
		request.put("게시글번호", ((Label)BOARD_LIST.getSelectionModel().getSelectedItem().getChildren().get(0)).getText());
		sendProtocol(request);
	}
	
	@FXML private void onBackToList()
	{
		BOARD_LIST_VIEW.setVisible(true);
		BOARD_WRITE.setVisible(false);
		BOARD_CONTENT_VIEW.setVisible(false);
	}
	
	//sendmessageList
	public void createdSendedMessageList(JSONArray arr)
	{
		
	}
	
	public void createRecivedmessage(JSONArray arr) // 양훈
   {
      recievermessageListData.removeAll(recievermessageListData);
      checkjarray = new JSONArray();
       for(Object t : arr)
       {
           JSONObject tt = (JSONObject)t;
           CheckBox left = new CheckBox();
           left.setAlignment(Pos.CENTER_LEFT);
           left.setMaxHeight(Double.MIN_VALUE);
           left.setMaxWidth(Double.MIN_VALUE);
           Label Date = new Label(tt.get("발신시각").toString());
           Date.setAlignment(Pos.CENTER_LEFT);
           Date.setMaxHeight(30);
           Date.setMaxWidth(150);
           Label Center = new Label(tt.get("발신자").toString());
           Center.setAlignment(Pos.CENTER_LEFT);
           Center.setMaxHeight(30);
           Center.setMaxWidth(150);
           Label right = new Label(tt.get("메세지제목").toString());
           right.setAlignment(Pos.CENTER);;
           right.setMaxHeight(Double.MAX_VALUE);
           right.setMaxWidth(Double.MAX_VALUE);
           int no = Integer.parseInt(tt.get("No").toString());
           Label left2 = new Label(no+"");
           left2.setAlignment(Pos.CENTER_LEFT);
           left2.setMaxHeight(30);
           left2.setMaxWidth(150);
           left2.setVisible(false);
           HBox item = new HBox();
           item.getChildren().addAll(left, left2, Date, Center,right);
           HBox.setHgrow(right, Priority.ALWAYS);
           HBox.setHgrow(Date, Priority.ALWAYS);
           HBox.setHgrow(Center, Priority.ALWAYS);
           HBox.setHgrow(left, Priority.ALWAYS);
           item.setAlignment(Pos.CENTER);
           item.setOnMouseClicked(new EventHandler<MouseEvent>() {

         @SuppressWarnings("unchecked")
         @Override
         public void handle(MouseEvent event) {
                  
             if(event.getClickCount()==2)
             {
                 JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_CONTENT_REQUEST);
                 req.put("No", no);
                 req.put("content_type", "recieve");
                 sendProtocol(req);
             }
          }}); 
            left.setOnAction(new EventHandler<ActionEvent>() {

              @SuppressWarnings("unchecked")
            @Override
            public void handle(ActionEvent event) {
                 checkjarray.removeAll(checkjarray);
                 ObservableList<HBox> a = messageList.getItems();
                 for(HBox box : a)
                 {
                    if(((CheckBox)box.getChildren().get(0)).isSelected())
                    {
                       checkjarray.add(tt);
                    }
                 }   
            }
         });
            
             recievermessageListData.add(item);
          }
        
          messageList.setItems(recievermessageListData);
    
      }
	   
	public void createStudentSearch(JSONArray arr) // 영훈
    {
       StudentListData.removeAll(StudentListData);
          select_find_mode.setItems(alloption);
          select_room_mode.setItems(roomoption);
          
             for(Object tt : arr)
             {
               JSONObject t = (JSONObject)tt;
               String rnum = t.get("방번호").toString();
                Label roomnum = new Label(rnum+"");
                roomnum.setAlignment(Pos.CENTER_LEFT);
                roomnum.setMaxWidth(120);
                roomnum.setMaxHeight(Double.MAX_VALUE);
                String name = t.get("소속학과").toString();
                Label classname = new Label(name);
                classname.setAlignment(Pos.CENTER_LEFT);
                classname.setMaxWidth(270);
                classname.setMaxHeight(Double.MAX_VALUE);
                int cnum = (int)t.get("학년");
                Label classnum = new Label(cnum+"");
                classnum.setAlignment(Pos.CENTER);
                classnum.setMaxWidth(190);
                classnum.setMaxHeight(Double.MAX_VALUE);
                String snum = t.get("학번").toString();
                Label studentnum = new Label(snum);
                studentnum.setAlignment(Pos.CENTER);
                studentnum.setMaxWidth(240);
                studentnum.setMaxHeight(Double.MAX_VALUE);
                String sname = t.get("이름").toString();
                Label studentname = new Label(sname);
                studentname.setAlignment(Pos.CENTER);
                studentname.setMaxWidth(350);
                studentname.setMaxHeight(Double.MAX_VALUE);
                String sex = t.get("성별").toString();
                Label studentsex = new Label(sex);
                studentsex.setAlignment(Pos.CENTER);
                studentsex.setMaxWidth(220);
                studentsex.setMaxHeight(Double.MAX_VALUE);
                String pnum = t.get("휴대폰번호").toString();
                Label phone = new Label(pnum);
                phone.setAlignment(Pos.CENTER);
                phone.setMaxWidth(400);
                phone.setMaxHeight(Double.MAX_VALUE);
                HBox item = new HBox();
                //item.setMaxSize(Double.MAX_VALUE, maxHeight);
                item.getChildren().addAll(roomnum, classname,classnum,studentnum,studentname,studentsex,phone);
                HBox.setHgrow(roomnum, Priority.ALWAYS);
                HBox.setHgrow(classname, Priority.ALWAYS);
                HBox.setHgrow(classnum, Priority.ALWAYS);
                HBox.setHgrow(studentnum, Priority.ALWAYS);
                HBox.setHgrow(studentname, Priority.ALWAYS);
                HBox.setHgrow(studentsex, Priority.ALWAYS);
                HBox.setHgrow(phone, Priority.ALWAYS);
                item.setAlignment(Pos.CENTER);
                EventHandler<MouseEvent> eh = new EventHandler<MouseEvent>() {

                @SuppressWarnings("unchecked")
				@Override
                public void handle(MouseEvent event) {

                   if(event.getClickCount() == 2)
                   {
                      System.out.println("신상정보 탭 더블 클릭 ");
                      JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.USER_CONTENT_REQUEST);
                      req.put("학번",snum);
                      req.put("이름",name);
                      sendProtocol(req);
                   }
                }
                   
             };                     
             item.setOnMouseClicked(eh);
                StudentListData.add(item);
             }
             
             StudentList.setItems(StudentListData);
             StudentList.setVisible(true);
    }
	   
	  public void createSendMassageList(JSONArray arr)
      {
         sendmessageListData.removeAll(sendmessageListData);
         for(Object t : arr)
         {
            JSONObject tt = (JSONObject)t;
            CheckBox left = new CheckBox();
            left.setAlignment(Pos.CENTER_LEFT);
            left.setMaxHeight(Double.MIN_VALUE);
            left.setMaxWidth(Double.MIN_VALUE);
            Label Date = new Label(tt.get("발신시각").toString());
            Date.setAlignment(Pos.CENTER_LEFT);
            Date.setMaxHeight(30);
            Date.setMaxWidth(150);
            Label Center = new Label(tt.get("수신자").toString());
            Center.setAlignment(Pos.CENTER_LEFT);
            Center.setMaxHeight(30);
            Center.setMaxWidth(150);
            Label right = new Label(tt.get("메세지제목").toString());
            right.setAlignment(Pos.CENTER);;
            right.setMaxHeight(Double.MAX_VALUE);
            right.setMaxWidth(Double.MAX_VALUE);
            HBox item = new HBox();
            int no = Integer.parseInt(tt.get("No").toString());
            Label left2 = new Label(no+"");
            left2.setVisible(false);
            left2.setAlignment(Pos.CENTER);
            left2.setMaxHeight(Double.MAX_VALUE);
            left2.setMaxWidth(Double.MAX_VALUE);
            item.getChildren().addAll(left, Date,Center,right);
            HBox.setHgrow(right, Priority.ALWAYS);
            HBox.setHgrow(Date, Priority.ALWAYS);
            HBox.setHgrow(Center, Priority.ALWAYS);
            HBox.setHgrow(left, Priority.ALWAYS);
            item.setAlignment(Pos.CENTER);
            
            item.setOnMouseClicked(new EventHandler<MouseEvent>() {

               @SuppressWarnings("unchecked")
               @Override
               public void handle(MouseEvent event) {
                  if(event.getClickCount()==2)
                  {
                     JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_CONTENT_REQUEST);
                     req.put("No", no);
                     req.put("content_type", "send");
                     sendProtocol(req);
                  }
               }
            });
              left.setOnAction(new EventHandler<ActionEvent>() {

                 @SuppressWarnings("unchecked")
               @Override
               public void handle(ActionEvent event) {
                    checkjarray.removeAll(checkjarray);
                    ObservableList<HBox> a = sendmessageList.getItems();
                    for(HBox box : a)
                    {
                       if(((CheckBox)box.getChildren().get(0)).isSelected())
                       {
                          checkjarray.add(tt);
                       }
                    }   
               }
            });
            
            sendmessageListData.add(item);
         }
         sendmessageList.setItems(sendmessageListData);
      }
	   
	   
	   @SuppressWarnings("unchecked")
	   @FXML public void onTimerClick()
	   {
		   JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_REQUEST);
		   req.put("viewtype", SCHEDULE_DISPLAY_MODE);
		   sendProtocol(req);
	   }
	   
	   
	   
	   private void createScheduleBoard_GRID(JSONArray data)
	   {
		   sObjList.clear();
		   schedule_board.getChildren().clear();
		   double startX = 30, startY = 30;
		   double labelStartY = 30;
		   double xGap = 400, yGap = 300;
		   int colCnt = 0;
		   
		   String currentDateTarget = "";
		   boolean changeDate = false;
		   
		   for(Object o : data)
		   {
			   JSONObject target = (JSONObject) o;

			   if(currentDateTarget.equals(Toolbox.getCurrentTimeFormat((java.sql.Timestamp)target.get("일시"), "yyyy년 MM월 dd일"))==false)
			   {
				   changeDate = true;
			   }
			   currentDateTarget = Toolbox.getCurrentTimeFormat((java.sql.Timestamp)target.get("일시"), "yyyy년 MM월 dd일");
			   
			   ScheduleObject sObj = new ScheduleObject(target.get("일정이름").toString(), (java.sql.Timestamp)target.get("일시"), target.get("분류").toString(), target.get("내용").toString(), ScheduleObject.VIEWTYPE_GRID);
			   sObj.setScheduleID(Integer.parseInt(target.get("일정번호").toString()));
			   
			   if(colCnt==4)	// 일정객체 개행 처리
			   {
				   startX = 30;
				   startY+=yGap;
				   labelStartY = startY + 30 + yGap;
				   colCnt=0;
			   }
			   
			   if(changeDate)	// 날짜 라벨 출력
			   {
				   Label l = new Label(currentDateTarget+"                  ");
				   l.setLayoutX(30);
				   l.setLayoutY(labelStartY);
				   startY = labelStartY + 90;
				   labelStartY = startY + 30 + yGap;
				   l.setPrefSize(900, 70);
				   l.setFont(Font.font("HYwulM",50));
				   l.setStyle("-fx-background-color: linear-gradient(to bottom, #4c4c4c 0%,#595959 12%,#666666 25%,#474747 39%,#2c2c2c 50%,#000000 51%,#111111 60%,#2b2b2b 76%,#1c1c1c 91%,#131313 100%);");
				   l.setTextFill(Paint.valueOf("white"));
				   l.setVisible(true);
				   schedule_board.getChildren().add(l);
				   changeDate = false;
				   startX = 30;
			   }
			   
			   sObj.setLayoutX(startX);
			   sObj.setLayoutY(startY);
			   sObj.setOnMouseClicked(new EventHandler<MouseEvent>() {
				   @Override
				   public void handle(MouseEvent event) {
					   modifyPanel.setDisable(false);
					   
					   String dateText = sObj.getDateText();
					   String date = dateText.split(" ")[0];
					   
					   modiTitle.setText(sObj.getTitle());
					   modiDate.setPromptText(date);
					   scheduleID.setText(sObj.getScheduleID()+"");
					   // 저 텍스트가 지금 1111-11-11 11:11:11 로 되어있음
					   
					   hourPicker.getSelectionModel().select(dateText.split(" ")[1].split(":")[0]);
					   minitePicker.getSelectionModel().select(dateText.split(" ")[1].split(":")[1]);
					   
					   switch(sObj.getCategory())
					   {
					   case "중요" : cate_important.setSelected(true);
					   				cate_event.setSelected(false); 
					   				cate_normal.setSelected(false);
					   				break;
					   case "행사" : cate_important.setSelected(false);
					   				cate_event.setSelected(true); 
					   				cate_normal.setSelected(false);
					   				break;
					   case "일반" : cate_important.setSelected(false);
			   						cate_event.setSelected(false); 
			   						cate_normal.setSelected(true);
			   						break;
					   }
					   
					   contentArea.setText(sObj.getContent().replaceAll("\n", ""));
					   sObj.toFront();
					   modifyPanel.setDisable(true);
					   for(ScheduleObject o : sObjList)
					   {
						   if(o.equals(sObj))
						   {
							   System.out.println("선택한 객체 : "+sObj.getTitle());
							   o.selected();
						   }
						   else
						   {
							   o.unSelect();
						   }
					   }
				   }
			   });
			   
			   sObjList.add(sObj);
			   schedule_board.getChildren().add(sObj);
			   startX+=xGap;
			   colCnt++;
		   }
			
	   }
	   
	   private void createScheduleBoard_LIST(JSONArray data)
	   {
		   sObjList.clear();
		   schedule_board.getChildren().clear();
		   
		   double startX = 30, startY = 30;
		   double yGap = 200;
		   boolean changeDate = false;
		   String currentDateTarget = "";
		   for(Object o : data)
		   {
			   JSONObject target = (JSONObject) o;
			   if(currentDateTarget.equals(Toolbox.getCurrentTimeFormat((java.sql.Timestamp)target.get("일시"), "yyyy년 MM월 dd일"))==false)
			   {
				   changeDate = true;
			   }
			   currentDateTarget = Toolbox.getCurrentTimeFormat((java.sql.Timestamp)target.get("일시"), "yyyy년 MM월 dd일");
			   ScheduleObject sObj = new ScheduleObject(target.get("일정이름").toString(), (java.sql.Timestamp)target.get("일시"), target.get("분류").toString(), target.get("내용").toString(),ScheduleObject.VIEWTYPE_LIST);
			   sObj.setScheduleID(Integer.parseInt(target.get("일정번호").toString()));
			   
			   if(changeDate)
			   {
				   System.out.println("일자변경 : "+currentDateTarget);
				   Label l = new Label(currentDateTarget+"                  ");
				   l.setLayoutX(30);
				   l.setLayoutY(startY);
				   startY+=90;
				   l.setPrefSize(900, 70);
				   l.setFont(Font.font("HYwulM",50));
				   l.setStyle("-fx-background-color: linear-gradient(to bottom, #4c4c4c 0%,#595959 12%,#666666 25%,#474747 39%,#2c2c2c 50%,#000000 51%,#111111 60%,#2b2b2b 76%,#1c1c1c 91%,#131313 100%);");
				   l.setTextFill(Paint.valueOf("white"));
				   l.setVisible(true);
				   schedule_board.getChildren().add(l);
				   changeDate = false;
			   }
			   
			   sObj.setLayoutX(startX);
			   sObj.setLayoutY(startY);
			   startY+=yGap;
			   sObj.setOnMouseClicked(new EventHandler<MouseEvent>() {
				   @Override
				   public void handle(MouseEvent event) {
					   modifyPanel.setDisable(false);
					   
					   String dateText = sObj.getDateText();
					   String date = dateText.split(" ")[0];
					   
					   modiTitle.setText(sObj.getTitle());
					   modiDate.setPromptText(date);
					   scheduleID.setText(sObj.getScheduleID()+"");
					   // 저 텍스트가 지금 1111-11-11 11:11:11 로 되어있음
					   
					   hourPicker.getSelectionModel().select(dateText.split(" ")[1].split(":")[0]);
					   minitePicker.getSelectionModel().select(dateText.split(" ")[1].split(":")[1]);
					   
					   switch(sObj.getCategory())
					   {
					   case "중요" : cate_important.setSelected(true);
					   				cate_event.setSelected(false); 
					   				cate_normal.setSelected(false);
					   				break;
					   case "행사" : cate_important.setSelected(false);
					   				cate_event.setSelected(true); 
					   				cate_normal.setSelected(false);
					   				break;
					   case "일반" : cate_important.setSelected(false);
			   						cate_event.setSelected(false); 
			   						cate_normal.setSelected(true);
			   						break;
					   }
					   
					   contentArea.setText(sObj.getContent().replaceAll("\n", ""));
					   sObj.toFront();
					   modifyPanel.setDisable(true);
					   for(ScheduleObject o : sObjList)
					   {
						   if(o.equals(sObj))
						   {
							   System.out.println("선택한 객체 : "+sObj.getTitle());
							   o.selected();
						   }
						   else
						   {
							   o.unSelect();
						   }
					   }
				   }
			   });
			   
			   sObjList.add(sObj);
			   schedule_board.getChildren().add(sObj);
			   
		   }
		   

	   }
	   
		public void createScheduleBoard_CELL(JSONArray data, String startDate)
		{
			sObjList.clear();
		    schedule_board.getChildren().clear();
		   
		   for(Object o : data)
		   {
			   JSONObject target = (JSONObject) o;
			   ScheduleObject sObj = new ScheduleObject(target.get("일정이름").toString(), (java.sql.Timestamp)target.get("일시"), target.get("분류").toString(), target.get("내용").toString(),ScheduleObject.VIEWTYPE_CELL);
			   sObj.setScheduleID(Integer.parseInt(target.get("일정번호").toString()));
			   sObj.setOnMouseClicked(new EventHandler<MouseEvent>() {
				   @Override
				   public void handle(MouseEvent event) {
					   modifyPanel.setDisable(false);
					   
					   String dateText = sObj.getDateText();
					   String date = dateText.split(" ")[0];
					   
					   modiTitle.setText(sObj.getTitle());
					   modiDate.setPromptText(date);
					   scheduleID.setText(sObj.getScheduleID()+"");
					   
					   hourPicker.getSelectionModel().select(dateText.split(" ")[1].split(":")[0]);
					   minitePicker.getSelectionModel().select(dateText.split(" ")[1].split(":")[1]);
					   
					   switch(sObj.getCategory())
					   {
					   case "중요" : cate_important.setSelected(true);
					   				cate_event.setSelected(false); 
					   				cate_normal.setSelected(false);
					   				break;
					   case "행사" : cate_important.setSelected(false);
					   				cate_event.setSelected(true); 
					   				cate_normal.setSelected(false);
					   				break;
					   case "일반" : cate_important.setSelected(false);
			   						cate_event.setSelected(false); 
			   						cate_normal.setSelected(true);
			   						break;
					   }
					   
					   contentArea.setText(sObj.getContent().replaceAll("\n", ""));
					   sObj.toFront();
					   modifyPanel.setDisable(true);
					   for(ScheduleObject o : sObjList)
					   {
						   if(o.equals(sObj))
						   {
							   System.out.println("선택한 객체 : "+sObj.getTitle());
							   o.selected();
						   }
						   else
						   {
							   o.unSelect();
						   }
					   }
				   }
			   });
			   sObjList.add(sObj);
		   }
		   
		   CalendarObject calendar = new CalendarObject(sObjList);
		   DatePicker pick = calendar.pick;
		   pick.setOnAction(new EventHandler<ActionEvent>() {
				@SuppressWarnings("unchecked")
				@Override
				public void handle(ActionEvent event) {
					calendar.getDataInstance().getCalander().setTime(Toolbox.StringToDate(pick.getValue().getYear()+""+pick.getValue().getMonthValue()+""+pick.getValue().getDayOfMonth(), "yyyyMd"));
					calendar.DateHeader.setText(new SimpleDateFormat("yyyy년 M월").format(Toolbox.StringToDate(pick.getValue().getYear()+""+pick.getValue().getMonthValue()+""+pick.getValue().getDayOfMonth(), "yyyyMd")));
					JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.MONTHLY_SCHEDULE_VIEW_REQUEST);
					req.put("startDate", calendar.getDataInstance().getStartDateOfMonth());
					req.put("endDate", calendar.getDataInstance().getEndDateOfMonth());
//						calendar.initDate();
					sendProtocol(req);
				}
			});
		   calendar.setLayoutX(100);
		   calendar.setLayoutY(30);
		   schedule_board.getChildren().add(calendar);
		}

	   
	   private void createScheduleBoard_CELL(JSONArray data)
	   {
		   sObjList.clear();
		   schedule_board.getChildren().clear();
		   
		   for(Object o : data)
		   {
			   JSONObject target = (JSONObject) o;
			   ScheduleObject sObj = new ScheduleObject(target.get("일정이름").toString(), (java.sql.Timestamp)target.get("일시"), target.get("분류").toString(), target.get("내용").toString(),ScheduleObject.VIEWTYPE_CELL);
			   sObj.setScheduleID(Integer.parseInt(target.get("일정번호").toString()));
			   sObj.setOnMouseClicked(new EventHandler<MouseEvent>() {
				   @Override
				   public void handle(MouseEvent event) {
					   modifyPanel.setDisable(false);
					   
					   String dateText = sObj.getDateText();
					   String date = dateText.split(" ")[0];
					   
					   modiTitle.setText(sObj.getTitle());
					   modiDate.setPromptText(date);
					   scheduleID.setText(sObj.getScheduleID()+"");
					   // 저 텍스트가 지금 1111-11-11 11:11:11 로 되어있음
					   
					   hourPicker.getSelectionModel().select(dateText.split(" ")[1].split(":")[0]);
					   minitePicker.getSelectionModel().select(dateText.split(" ")[1].split(":")[1]);
					   
					   switch(sObj.getCategory())
					   {
					   case "중요" : cate_important.setSelected(true);
					   				cate_event.setSelected(false); 
					   				cate_normal.setSelected(false);
					   				break;
					   case "행사" : cate_important.setSelected(false);
					   				cate_event.setSelected(true); 
					   				cate_normal.setSelected(false);
					   				break;
					   case "일반" : cate_important.setSelected(false);
			   						cate_event.setSelected(false); 
			   						cate_normal.setSelected(true);
			   						break;
					   }
					   
					   contentArea.setText(sObj.getContent().replaceAll("\n", ""));
					   sObj.toFront();
					   modifyPanel.setDisable(true);
					   for(ScheduleObject o : sObjList)
					   {
						   if(o.equals(sObj))
						   {
							   System.out.println("선택한 객체 : "+sObj.getTitle());
							   o.selected();
						   }
						   else
						   {
							   o.unSelect();
						   }
					   }
				   }
			   });
			   sObjList.add(sObj);
		   }
		   
		   CalendarObject calendar = new CalendarObject(sObjList);
		   DatePicker pick = calendar.pick;
		   pick.setOnAction(new EventHandler<ActionEvent>() {
				@SuppressWarnings("unchecked")
				@Override
				public void handle(ActionEvent event) {
					calendar.getDataInstance().getCalander().setTime(Toolbox.StringToDate(pick.getValue().getYear()+""+pick.getValue().getMonthValue()+""+pick.getValue().getDayOfMonth(), "yyyyMd"));
					calendar.DateHeader.setText(new SimpleDateFormat("yyyy년 M월").format(Toolbox.StringToDate(pick.getValue().getYear()+""+pick.getValue().getMonthValue()+""+pick.getValue().getDayOfMonth(), "yyyyMd")));
					JSONObject req = Toolbox.createJSONProtocol(NetworkProtocols.MONTHLY_SCHEDULE_VIEW_REQUEST);
					req.put("startDate", calendar.getDataInstance().getStartDateOfMonth());
					req.put("endDate", calendar.getDataInstance().getEndDateOfMonth());
//					calendar.initDate();
					sendProtocol(req);
				}
			});
		   calendar.setLayoutX(100);
		   calendar.setLayoutY(30);
		   schedule_board.getChildren().add(calendar);
	   }
	   
	   @FXML public void onSchedulModify()
	   {
		   modifyPanel.setDisable(false);
	   }
	   
	   @FXML public void onModifySumbit()
	   {
		   String date = "";
		   if(modiDate.getValue()==null)
		   {
			   if(modiDate.getPromptText().length()==0)
			   {
				   CustomDialog.showMessageDialog("날짜를 선택해주세요!", sManager.getStage());
				   return;
			   }
			   else
			   {
				   date = modiDate.getPromptText();				   
			   }
		   }
		   else
		   {
			   date = modiDate.getValue().toString();
		   }
		   
		   if(hourPicker.getValue()==null||minitePicker.getValue()==null)
		   {
			   CustomDialog.showMessageDialog("시간을 선택해주세요!", sManager.getStage());
			   return;
		   }
		   
		   date = date + " " + hourPicker.getValue() + ":" + minitePicker.getValue();
		   
		   String cate = "";
	
		   if(cate_important.isSelected()==true)
		   {
			   cate = "중요";
		   }
		   
		   if(cate_event.isSelected()==true)
		   {
			   cate = "행사";
		   }
		   
		   if(cate_normal.isSelected()==true)
		   {
			   cate = "일반";
		   }
		   
		   String[] keys = {"일정번호","일정이름","일시","분류","내용","시작일","종료일","viewtype"};
		   Object[] values = {scheduleID.getText(), modiTitle.getText(), date, cate, contentArea.getText(), startDateString, endDateString, SCHEDULE_DISPLAY_MODE};
		   System.out.println(startDateString+", "+endDateString);
		   sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MODIFY_SCHEDULE_REQUEST, keys, values));
		   
	   }
	   
	   @SuppressWarnings("unchecked")
	   @FXML public void createNewSchedule()
	   {
		   CustomDialog dlg = new CustomDialog(Statics.SCHEDULE_CREATE_DIALOG, Statics.SCHEDULE_CREATE_DIALOG_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
		   ScheduleCreateDialogController con = (ScheduleCreateDialogController) dlg.getController();
		   con.setWindow(dlg);
		   dlg.showAndWait();
		   
		   if(dlg.getUserData().equals("cancel"))
		   {
			   return;
		   }
		   
		   JSONObject obj = (JSONObject)dlg.getUserData();
		   obj.put("type", NetworkProtocols.ADD_SCHEDULE_REQUEST);
		   obj.put("시작일", startDateString);
		   obj.put("종료일", endDateString);
		   obj.put("viewtype", SCHEDULE_DISPLAY_MODE);
		   sendProtocol(obj);
	   }
	   
	   @SuppressWarnings("unchecked")
	   @FXML public void deleteSchedule()
	   {
		   for(ScheduleObject o : sObjList)
		   {
			   if(o.isSelected()==true)
			   {
				   JSONObject obj = Toolbox.createJSONProtocol(NetworkProtocols.DELETE_SCHEDULE_REQUEST);
				   obj.put("일정번호", o.getScheduleID());
				   obj.put("일시", o.getDateText());
				   obj.put("시작일", startDateString);
				   obj.put("종료일", endDateString);
				   obj.put("viewtype", SCHEDULE_DISPLAY_MODE);
				   sendProtocol(obj);
				   return;
			   }
		   }
	   }
	   
	   @FXML public void onProfessionalSearch()
	   {
		   String[] keys = {"시작일", "종료일", "분류"};
		   
		   String duration = "";
		   
		   if(search_daily.isSelected()==true)
		   {
			   duration = "중요";
		   }
		   else if(search_weekly.isSelected()==true)
		   {
			   duration = "행사";			   
		   }
		   else if(search_monthly.isSelected()==true)
		   {
			   duration = "일반";   
		   }
		   else
		   {
			   duration = "전체";
		   }
		   
		   Object[] values = {startDate.getValue().toString(), endDate.getValue().toString(), duration};
		 
		   sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.SCHEDULE_PROFESSIONAL_SEARCH_REQUEST, keys, values));
		   
	   }
	   
	   @FXML public void onViewMode()
	   {
		   if(SCHEDULE_DISPLAY_DATA==null)return;
		   
		   SCHEDULE_DISPLAY_MODE = schedule_view_mode.getValue();
		   System.out.println("선택한 값 : "+SCHEDULE_DISPLAY_MODE);
		   if(SCHEDULE_DISPLAY_MODE.equals("그리드모드"))
		   {
				createScheduleBoard_GRID(SCHEDULE_DISPLAY_DATA);										
		   }
		   else if(SCHEDULE_DISPLAY_MODE.equals("리스트모드"))
		   {
			   createScheduleBoard_LIST(SCHEDULE_DISPLAY_DATA);
		   }
		   else if(SCHEDULE_DISPLAY_MODE.equals("달력모드"))
		   {
			   createScheduleBoard_CELL(SCHEDULE_DISPLAY_DATA);
		   }
	   }
	   
	   public void createWeabakManager(JSONArray arr)  // 영훈
	   {
	      StudentWeabakListData.removeAll(StudentWeabakListData);
	      for(Object a : arr)
	      {
	    	JSONObject json = (JSONObject)a;
	    	int No = Integer.parseInt(json.get("외박번호").toString());
	    	
	    	int check = Integer.parseInt(json.get("승인여부").toString());
	    	String acceptcheck;
	    	if(check == 0)
	    	{
	    		acceptcheck = "대기중";
	    	}
	    	else if(check == 1)
	    	{
	    		acceptcheck = "승인";
	    	}
	    	else
	    	{
	    		acceptcheck = "거절";
	    	}
	    	
	    	String date = json.get("신청일자").toString().split(" ")[0];
	        Label left1 = new Label(date);
	        left1.setAlignment(Pos.CENTER);
	        left1.setMaxHeight(30);
	        left1.setMaxWidth(200);
	        Label left2 = new Label(json.get("외박일자").toString());
	        left2.setAlignment(Pos.CENTER);
	        left2.setMaxHeight(30);
	        left2.setMaxWidth(200);
	        String num = json.get("학번").toString();
	        Label left3 = new Label(num);
	        left3.setAlignment(Pos.CENTER_RIGHT);
	        left3.setMaxHeight(30);
	        left3.setMaxWidth(150);
	        String name = json.get("이름").toString();
	        Label left4 = new Label(name);
	        left4.setAlignment(Pos.CENTER);
	        left4.setMaxHeight(30);
	        left4.setMaxWidth(220);
	        Label center = new Label(json.get("사유").toString());
	        center.setAlignment(Pos.CENTER);
	        center.setMaxHeight(Double.MAX_VALUE);
	        center.setMaxWidth(Double.MAX_VALUE);
	        Label right = new Label(json.get("목적지").toString());
	        right.setAlignment(Pos.CENTER);
	        right.setMaxHeight(30);
	        right.setMaxWidth(270);
	        Label right1 = new Label(acceptcheck);
	        right1.setAlignment(Pos.CENTER);
	        right1.setMaxHeight(30);
	        right1.setMaxWidth(170);
	        HBox item = new HBox();
	        item.getChildren().addAll(left1,left2,left3,left4,center,right,right1);
	        HBox.setHgrow(left1, Priority.ALWAYS);
	        HBox.setHgrow(left2, Priority.ALWAYS);
	        HBox.setHgrow(left3, Priority.ALWAYS);
	        HBox.setHgrow(left4, Priority.ALWAYS);
	        HBox.setHgrow(center, Priority.ALWAYS);
	        HBox.setHgrow(right, Priority.ALWAYS);
	        HBox.setHgrow(right1, Priority.ALWAYS);
	        item.setAlignment(Pos.CENTER);
	        StudentWeabakListData.add(item);
	        
	        EventHandler<MouseEvent> eh = new EventHandler<MouseEvent>() {

				@SuppressWarnings("unchecked")
				@Override
				public void handle(MouseEvent event) {
					if(event.getClickCount() == 2)
					{
						JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_CONTENT_REQUEST);
						json.put("No", No);
						json.put("학번", num);
						json.put("이름", name);
						sendProtocol(json);
					}
				}
			};
			item.setOnMouseClicked(eh);
	      }
	      
	      StudentWeabakList.setItems(StudentWeabakListData);
	      StudentWeabakList.setVisible(true);
	      
	   }
	   
	   public void createPlusMinusSearch(JSONArray arr)  // 영훈
	   {
	      StudentManagerListData.removeAll(StudentManagerListData);
	 
	      for(Object a : arr)
	      {
	    	JSONObject json = (JSONObject)a;
	    	String num = json.get("학번").toString();
	        Label left = new Label(num);
	        left.setAlignment(Pos.CENTER);
	        left.setMaxHeight(30);
	        left.setMaxWidth(300);
	        String name = json.get("이름").toString();
	        Label left1 = new Label(name);
	        left1.setAlignment(Pos.CENTER);
	        left1.setMaxHeight(30);
	        left1.setMaxWidth(310);
	        Label center = new Label(json.get("상점").toString());
	        center.setAlignment(Pos.CENTER);
	        center.setMaxHeight(Double.MAX_VALUE);
	        center.setMaxWidth(Double.MAX_VALUE);
	        Label center1 = new Label(json.get("벌점").toString());
	        center1.setAlignment(Pos.CENTER);
	        center1.setMaxHeight(Double.MAX_VALUE);
	        center1.setMaxWidth(Double.MAX_VALUE);
	        Label right = new Label(json.get("합계").toString());
	        right.setAlignment(Pos.CENTER);
	        right.setMaxHeight(Double.MAX_VALUE);
	        right.setMaxWidth(Double.MAX_VALUE);
	        HBox item = new HBox();
	        item.getChildren().addAll(left,left1,center,center1,right);
	        HBox.setHgrow(left, Priority.ALWAYS);
	        HBox.setHgrow(left1, Priority.ALWAYS);
	        HBox.setHgrow(center, Priority.ALWAYS);
	        HBox.setHgrow(center1, Priority.ALWAYS);
	        HBox.setHgrow(right, Priority.ALWAYS);
	        item.setAlignment(Pos.CENTER);
	        StudentManagerListData.add(item);
	        EventHandler<MouseEvent> eh = new EventHandler<MouseEvent>() {
				@SuppressWarnings("unchecked")
				@Override
				public void handle(MouseEvent event) {

					if(event.getClickCount()==2)
					{
						JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_ASSIGN_REQUEST);
						json.put("학번",num );
						json.put("이름", name);
						sendProtocol(json);
					}
				}
			};
	        item.setOnMouseClicked(eh);
	         
	      }
	      StudentManagerList.setItems(StudentManagerListData);
	     
	      StudentManagerList.setVisible(true);
	      
	   }
	   
	public void StudentPlusMinusManagerList(JSONArray arr)
	   {
		   StudentMenagerListCheckData.removeAll(StudentMenagerListCheckData);
		   System.out.println("메소드 시작");
		   for(Object a : arr)
		   {
		    JSONObject json = (JSONObject)a;
		    Label left = new Label(json.get("날짜").toString());
		    left.setAlignment(Pos.CENTER);
		    left.setMaxHeight(30);
		    left.setMaxWidth(260);
		    Label left1 = new Label(json.get("학번").toString());
		    left1.setAlignment(Pos.CENTER);
		    left1.setMaxHeight(30);
		    left1.setMaxWidth(233);
		    Label center = new Label(json.get("이름").toString());
		    center.setAlignment(Pos.CENTER);
		    center.setMaxHeight(30);
		    center.setMaxWidth(257);
		    Label center1 = new Label(json.get("상벌점타입").toString());
		    center1.setAlignment(Pos.CENTER);
		    center1.setMaxHeight(30);
		    center1.setMaxWidth(247);
		    Label right = new Label(json.get("점수").toString());
		    right.setAlignment(Pos.CENTER);
		    right.setMaxHeight(30);
		    right.setMaxWidth(241);
		    Label right1 = new Label(json.get("내용").toString());
		    right1.setAlignment(Pos.CENTER);
		    right1.setMaxHeight(30);
		    right1.setMaxWidth(581);
		    HBox item = new HBox();
		    item.getChildren().addAll(left,left1,center,center1,right,right1);
		    HBox.setHgrow(left, Priority.ALWAYS);
		    HBox.setHgrow(left1, Priority.ALWAYS);
		    HBox.setHgrow(center, Priority.ALWAYS);
		    HBox.setHgrow(center1, Priority.ALWAYS);
		    HBox.setHgrow(right, Priority.ALWAYS);
		    HBox.setHgrow(right1, Priority.ALWAYS);
		    item.setAlignment(Pos.CENTER);
		    StudentMenagerListCheckData.add(item);
		         
		    }
		    StudentMenagerListCheck.setItems(StudentMenagerListCheckData);
		    StudentMenagerListCheck.setVisible(true);
	   }
	
	@FXML private void onSearch()
	{
		String searchKey = searchField.getText();
		ArrayList<String> newList = new ArrayList<String>();

		for(String s : rList)
		{
			if(s.contains(searchKey))
			{
				newList.add(s);
			}
		}
		
		createReciverList(newList);
		recieverList.refresh();
		
	}
	
	
	@SuppressWarnings("unchecked")
	@FXML private void onSearch1()
	{
		String searchKey = studenttextField.getText();
		JSONArray array = new JSONArray();
		
		
		for(Object s : jarray)
		{
			JSONObject t = (JSONObject)s;
		    if(t.get("학번").toString().contains(searchKey) || t.get("이름").toString().contains(searchKey))
		    {
		    	array.add(t);
		    }
		}
		createStudentSearch(array);
		StudentList.refresh();
		
	}
	
	//영훈 추가
	@SuppressWarnings("unchecked")
	@FXML private void onSearch2()
	{
		String searchKey = WeabakField.getText();
		JSONArray jarr = new JSONArray();
		
		for(Object a : jarray)
		{
			JSONObject aa = (JSONObject)a;
			if((aa.get("학번").toString().contains(searchKey)) ||(aa.get("이름").toString().contains(searchKey)) || aa.get("신청일자").toString().contains(searchKey) 
					|| aa.get("외박일자").toString().contains(searchKey))
			{
				jarr.add(aa);
			}			
		}
		createWeabakManager(jarr);
		StudentWeabakList.refresh();
		
	}
	
	@SuppressWarnings("unchecked")
	@FXML private void onSearch3()
	{
		String searchKey = PMField.getText();
		JSONArray jarr = new JSONArray();
		for(Object a : jarray)
		{
			JSONObject aa = (JSONObject)a;
			if((aa.get("학번").toString().contains(searchKey)) ||(aa.get("이름").toString().contains(searchKey)))
			{
				jarr.add(aa);
			}			
		}
		createPlusMinusSearch(jarr);
		StudentManagerList.refresh();
	}
	
	@SuppressWarnings("unchecked")
	@FXML public void onSearch4()
	{
		String searchKey = PMListField.getText();
		JSONArray jarr = new JSONArray();
		for(Object a : jarray)
		{
			JSONObject aa = (JSONObject)a;
			if((aa.get("날짜").toString().contains(searchKey) || aa.get("학번").toString().contains(searchKey)) ||(aa.get("이름").toString().contains(searchKey)))
			{
				jarr.add(aa);
			}			
		}
		StudentPlusMinusManagerList(jarr);
		StudentMenagerListCheck.refresh();
	}
	
	@SuppressWarnings("unchecked")
	@FXML public void onRecieverMsgSearch()
	{
		String searchKey = MsgRecieverField.getText();
		System.out.println(jarray.toJSONString());
		JSONArray jarr = new JSONArray();
		for(Object a : jarray)
		{
			JSONObject aa = (JSONObject)a;
			System.out.println(aa.get("발신시각").toString());
			System.out.println(aa.get("발신자").toString());
			if((aa.get("발신시각").toString().contains(searchKey) || aa.get("발신자").toString().contains(searchKey)) ||(aa.get("메세지제목").toString().contains(searchKey)))
			{
				jarr.add(aa);
			}			
		}
		createRecivedmessage(jarr);
		messageList.refresh();
	}
	
	@FXML public void onMSGTabClick()
    {
       int current = MESSAGE.getSelectionModel().getSelectedIndex();

       if(saved==current)return;
       
       saved = current;
       
       switch(saved)
       {
       case 0 :
                sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST));
                display_reciever.setText("");
                msgTitle.setText("");
                msgContent.setText("");
          break;
       case 1 :
                sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST));
                display_reciever.setText("");
                msgTitle.setText("");
                msgContent.setText("");
          break;
       case 2 : 
             sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_USER_LIST_REQUEST));
             display_reciever.setText("");
             msgTitle.setText("");
             msgContent.setText("");
          break;
       }
       
    }
	
	   @SuppressWarnings("unchecked")
	   @FXML public void onWeabakTabClick()
	   {
		   int select = STUDNETMANAGER.getSelectionModel().getSelectedIndex();
		   
		   if(saved == select) return;
		   
		   saved = select;
		   
		   if(saved == 0)
		   {
			   JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_INFO_TAP_REQUEST);
			   json.put("category","대기중");
			   sendProtocol(json);
		   }
		   else if(saved == 1)
		   {
			   sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_TAP_REQUEST));
		   }
		   else
		   {
			   sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_TAP_INFO_REQUEST));
			   System.out.println("리퀘스트 시작");
		   }
	   }
	   
		@SuppressWarnings("unchecked")
		@FXML private void onWait()
		{
			JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_INFO_TAP_REQUEST);
			json.put("category", "대기중");
			sendProtocol(json);
		}
		
		@SuppressWarnings("unchecked")
		@FXML private void onAccept()
		{
			JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_INFO_TAP_REQUEST);
			json.put("category", "승인외박");
			sendProtocol(json);
		}
		
		@SuppressWarnings("unchecked")
		@FXML private void onFalse()
		{
			JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_INFO_TAP_REQUEST);
			json.put("category", "비승인외박");
			sendProtocol(json);
		}
		
		@SuppressWarnings("unchecked")
		@FXML private void onAddTab()
		{
			// 대화상자 띄워서 새로운 탭 추가
			String categoryName = CustomDialog.showInputDialog("추가할 카테고리를 입력하세요", sManager.getStage());
			
			if(categoryName.length()==0)
			{
				CustomDialog.showMessageDialog("카테고리명을 1글자 이상 입력하세요.", sManager.getStage());
				return;
			}
			
			if(categoryName!=null)
			{
				JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_ADD_TAP_REQUEST);
				request.put("name", categoryName);
				sendProtocol(request);
			}
		}
		
		@FXML private void onDeleteTab()
		{
			// 탭 삭제
			sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_CATEGORY_DELETE_REQUEST));
		}
		
		// 메세지 버튼 이벤트
		
      @FXML private void onAnswer()
      {
         display_reciever.setText("");
         msgTitle.setText("");
         msgContent.setText("");
         if(checkjarray.size() == 1)
         {
            MESSAGE.getSelectionModel().select(2);
            sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_USER_LIST_REQUEST));
            sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST));      
            for(Object a : checkjarray)
            {
               JSONObject aa = (JSONObject)a;
               String num = aa.get("학번").toString();
               String name = aa.get("발신자").toString();
            
               display_reciever.appendText(num+","+name+"\t");   
            }
         }
         else if(checkjarray.size() > 1)
         {
            CustomDialog.showMessageDialog("보내실분 한명만 선택하세요.", sManager.getStage());
            JSONObject json= Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST);
            
            sendProtocol(json);
         }
         else
         {
            CustomDialog.showMessageDialog("보낼사람을 선택하세요.", sManager.getStage());
         }
         checkjarray.clear();
      }
		
      @FXML private void onRDelivery()
      {
         display_reciever.setText("");
         msgTitle.setText("");
         msgContent.setText("");
         
         
         if(checkjarray.size() == 1)
         {
            MESSAGE.getSelectionModel().select(2);
            sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_USER_LIST_REQUEST));
            sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST));
            for(Object a : checkjarray)
            {
               JSONObject json = (JSONObject)a;
               String msgcontent = json.get("메세지본문").toString();
               String msgtitle = json.get("메세지제목").toString();
               
               msgTitle.setText(msgtitle);
               msgContent.appendText(msgcontent+"\n"+"====================== 전달 ===================== \n");    
            }
         }
         else if(checkjarray.size() > 1)
         {
            CustomDialog.showMessageDialog("전달 할 내용 한가지만 선택하세요.", sManager.getStage());
            JSONObject json= Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST);
            
            sendProtocol(json);
         }
         else
         {
            CustomDialog.showMessageDialog("전달 할 내용을 선택하세요.", sManager.getStage());
         }
         
         checkjarray.clear();
      }
		
      @SuppressWarnings("unchecked")
      @FXML public void onRdelite()
      {
         if(checkjarray.size() < 1)
         {
            CustomDialog.showMessageDialog("삭제 할 메세지를 선택해주세요.", sManager.getStage());
         }
         else
         {
            JSONArray array = new JSONArray();
            for(Object a : checkjarray)
            {
               JSONObject aa = (JSONObject)a;
               array.add(aa);
            }
            
            JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RICIEVER_SELECT_DELETE_REQUEST);
            json.put("delete", array);
            json.put("reqType", "R");
            sendProtocol(json);
         }
      }
		
      @SuppressWarnings("unchecked")
      @FXML public void onRallDelete()
      {
         int selection = CustomDialog.showConfirmDialog("정말 모든 메세지를 삭제하시겠습니까?", sManager.getStage());
         
         if(selection==CustomDialog.OK_OPTION)
         {
            JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RICIEVER_ALL_DELETE_REQUEST);
            request.put("reqType", "R");
            sendProtocol(request);
         }
         else if(selection==CustomDialog.CANCEL_OPTION)
         {
            CustomDialog.showMessageDialog("전체삭제를 취소하셨습니다.", sManager.getStage());
         }
      }
		
      
      @FXML public void onSDelivery()
      {
         display_reciever.setText("");
         msgTitle.setText("");
         msgContent.setText("");
         
         
         if(checkjarray.size() == 1)
         {
            MESSAGE.getSelectionModel().select(2);
            sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_USER_LIST_REQUEST));
            sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST));
            for(Object a : checkjarray)
            {
               JSONObject json = (JSONObject)a;
               String msgcontent = json.get("메세지본문").toString();
               String msgtitle = json.get("메세지제목").toString();
               
               msgTitle.setText(msgtitle);
               msgContent.appendText(msgcontent+"\n"+"====================== 전달 ===================== \n");    
            }
         }
         else if(checkjarray.size() > 1)
         {
            CustomDialog.showMessageDialog("전달 할 내용 한가지만 선택하세요.", sManager.getStage());
            JSONObject json= Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST);
            
            sendProtocol(json);
         }
         else
         {
            CustomDialog.showMessageDialog("전달 할 내용을 선택하세요.", sManager.getStage());
         }
         
         checkjarray.clear();
      }
      // 보낸함 삭제
      @SuppressWarnings("unchecked")
      @FXML public void onSelectDelete()
      {
         if(checkjarray.size() < 1)
         {
            CustomDialog.showMessageDialog("삭제 할 메세지를 선택해주세요.", sManager.getStage());
         }
         else
         {
            JSONArray array = new JSONArray();
            for(Object a : checkjarray)
            {
               JSONObject aa = (JSONObject)a;
               array.add(aa);
            }
            
            JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RICIEVER_SELECT_DELETE_REQUEST);
            json.put("delete", array);
            json.put("reqType", "S");
            sendProtocol(json);
         }
      }
      // 보낸함 전체삭제
      @SuppressWarnings("unchecked")
      @FXML public void onRAllDelete()
      {
         
         int selection = CustomDialog.showConfirmDialog("정말 모든 메세지를 삭제하시겠습니까?", sManager.getStage());
         
         if(selection==CustomDialog.OK_OPTION)
         {
            JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RICIEVER_ALL_DELETE_REQUEST);
            request.put("reqType", "S");
            sendProtocol(request);
         }
         else if(selection==CustomDialog.CANCEL_OPTION)
         {
            CustomDialog.showMessageDialog("전체삭제를 취소하셨습니다.", sManager.getStage());
         }
      }
      
      
       //복붙해야함 원래있떤거 지우고 밑에 메소드까지
      @FXML public void onFindCombo()
      {
         
          check_class =select_find_mode.getSelectionModel().getSelectedIndex()+1; //학생신상정보 소속학과 체크시
          classcheck = true;
          overlap();
      }
      @FXML public void onLevelCombo()
      {
          check_level =select_room_mode.getSelectionModel().getSelectedIndex()+1; //학생신상정보 층별 체크시
          levelcheck = true;
          overlap();
      }
      
      
      
      @FXML public void onUidDelete()
      {
         display_reciever.setText("");
      }
      
      @SuppressWarnings("unchecked")
      public void overlap()
      {
         JSONObject json;
         if(classcheck && levelcheck)
         {
            json = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SORT_OVERLAP_REQUEST);
            json.put("class", check_class);
            json.put("level", check_level);
            sendProtocol(json);
         }
         else if(classcheck == true && levelcheck == false)
         {
            json = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SORT_OVERLAP_REQUEST);
            json.put("class", check_class);
            json.put("level", 1);
            sendProtocol(json);
         }
         else
         {
            json = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SORT_OVERLAP_REQUEST);
            json.put("class", 1);
            json.put("level", check_level);
            sendProtocol(json);
         }
      }
      
		
		
		private void createBoardCategoryList(JSONArray data)
		{
			for(int i = 0; i < data.size() ; i++)
			{
				final int category = i;
				String target = data.get(i).toString();
				BOARD_W_CSELECTOR.getItems().add(target);
				BOARD_CATEGORY_SELECTOR.getItems().add(target);
				
				Button tab = new Button(target);
				
				tab.styleProperty().bind(Bindings.when(tab.hoverProperty())
						 .then(new SimpleStringProperty("-fx-background-color: gray; -fx-text-fill: -fx-text-base-color; -fx-font: 17pt \"HYwulM\";"))
						 .otherwise(new SimpleStringProperty("-fx-background-color: linear-gradient(to bottom, rgba(247,251,252,1) 0%,rgba(217,237,242,1) 40%,rgba(173,217,228,1) 100%); -fx-text-fill: -fx-text-base-color; -fx-font: 17pt \"HYwulM\";")));
				tab.setOnAction(new EventHandler<ActionEvent>() {
					
					@SuppressWarnings("unchecked")
					@Override
					public void handle(ActionEvent event) {
						JSONObject o = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_LIST_REQUEST);
						o.put("category", category+1);
						sendProtocol(o);
						
					}
				});

				board_tab_btn_box.getChildren().add(tab);
			}
		}

		@FXML
		private void onResend()
		{
			  display_reciever.setText("");
		         msgTitle.setText("");
		         msgContent.setText("");
		         
		         if(checkjarray.size() == 1)
		         {
		            MESSAGE.getSelectionModel().select(2);
		            sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_USER_LIST_REQUEST));
		            sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST));
		            for(Object a : checkjarray)
		            {
		               JSONObject aa = (JSONObject)a;
		               String num = aa.get("학번").toString();
		               String name = aa.get("수신자").toString();
		               String content = aa.get("메세지본문").toString();
		               String title = aa.get("메세지제목").toString();
		               
		               
		                msgTitle.setText(title); 
		               display_reciever.appendText(num+","+name+"\t");
		               msgContent.setText(content);
		            }
		         }   
		         else if(checkjarray.size() > 1)
		         {
		            CustomDialog.showMessageDialog("보내실분 한명만 선택하세요.", sManager.getStage());
		            JSONObject json= Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST);
		            
		            sendProtocol(json);
		         }
		         else
		         {
		            CustomDialog.showMessageDialog("보낼사람을 선택하세요.", sManager.getStage());
		         }
		         checkjarray.clear();

		}
		
		@FXML
		private void onMenuSubmin()
		{
			sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIN_MAIN_REQUEST));
		}
		
		@FXML private void onMainImage()
		{
			shutdown();
		    MAINPANE.setVisible(true);
		}
		
		@SuppressWarnings("unchecked")
		@FXML
		private void onAddSubmit()
		{
			CustomDialog dlg = new CustomDialog(Statics.ADD_SUBMIT_DIALOG_FXML, Statics.ADD_SUBMIT_DIALOG_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
			AddSubmitDialogController con = (AddSubmitDialogController)dlg.getController();
			con.setWindow(dlg);
			dlg.showAndWait();
			JSONObject intent = (JSONObject) dlg.getUserData();
			if(intent!=null)
			{
				JSONObject request= Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIT_ENROLL_REQUEST);
				request.put("data", intent);
				sendProtocol(request);
			}
		}
		
		private void createNoSubmitUI()
		{
			if(submitListViewData==null)
			{
				submitListViewData = FXCollections.observableArrayList();
			}
			submitListViewData.clear();
			submitTitle.setText("");
			submitDeadLine.setText("");
			processArea.setText("진행중인 제출이 없습니다.");
			addSubmitBtn.setDisable(false);
			disSubmitBtn.setDisable(true);
			emailSubmitBtn.setDisable(true);
			saveSubmitBtn.setDisable(true);
			processBox.setVisible(false);
			SUBMIT_VIEW.setVisible(true);
		}
		
		private void createExistedSubmitUI(JSONObject dataBundle)
		{
			if(submitListViewData==null)
			{
				submitListViewData = FXCollections.observableArrayList();
			}
			submitListViewData.clear();
			processBox.setVisible(false);
			// 있는 경우 UI 꾸미깅
			submitTitle.setText(dataBundle.get("제출분류명").toString());
			submitDeadLine.setText(Toolbox.getSQLDateToFormat((java.sql.Date)dataBundle.get("마감시간"), "yyyy-MM-dd HH:mm"));
			processArea.setText("진행중");
			
			for(Object o : (JSONArray)dataBundle.get("data-bundle"))
			{
				JSONObject target = (JSONObject)o;
				HBox item = new HBox();
				
				Label no = new Label(target.get("서류번호")+"");
				no.setPrefWidth(0);
				no.setVisible(false);
				
				Label id = new Label(target.get("학번").toString());
				id.setFont(Font.font("HYWulM", 20));
				id.setAlignment(Pos.CENTER);
				id.setPrefWidth(168);
				
				Label name = new Label(target.get("이름").toString());				
				name.setFont(Font.font("HYWulM", 20));
				name.setAlignment(Pos.CENTER);
				name.setPrefWidth(168);
				
				Label isSubmit = new Label(target.get("제출여부").toString());
				isSubmit.setFont(Font.font("HYWulM", 20));
				isSubmit.setAlignment(Pos.CENTER);
				isSubmit.setPrefWidth(168);
				
				Label submitTime = new Label(target.get("제출시간").toString().split("\\.")[0]);
				submitTime.setFont(Font.font("HYWulM", 20));
				submitTime.setAlignment(Pos.CENTER);				
				submitTime.setPrefWidth(303);
				
				Separator s0 = new Separator(Orientation.VERTICAL);
				Separator s1 = new Separator(Orientation.VERTICAL);
				Separator s2 = new Separator(Orientation.VERTICAL);
				
				item.getChildren().addAll(id,s0,name,s1,isSubmit,s2,submitTime, no);
				submitListViewData.add(item);
			}
			submitListView.setItems(submitListViewData);
			addSubmitBtn.setDisable(true);
			disSubmitBtn.setDisable(false);
			emailSubmitBtn.setDisable(false);
			saveSubmitBtn.setDisable(false);
			shutdown();
			SUBMIT_VIEW.setVisible(true);
		}
		
		@FXML
		private void onDisposeSubmit()
		{
			int re = CustomDialog.showConfirmDialog("[경 고] 데이터를 백업한 후 진행해주세요.", sManager.getStage());
			if(re==CustomDialog.OK_OPTION)
			{
				sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_ASK_REQEUST));
			}
			else
			{
				CustomDialog.showMessageDialog("마감이 취소됐습니다.", sManager.getStage());
			}
		}
		
		@FXML
		private void onEmailUpload()
		{
			CustomDialog dlg = new CustomDialog(Statics.EMAIL_SEND_FXML, Statics.EMAIL_SEND_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
			EmailSendDialogController con = (EmailSendDialogController)dlg.getController();
			con.setWindow(dlg);
			dlg.showAndWait();
			
			if(dlg.getUserData()!=null)
			{
				sendProtocol((JSONObject)dlg.getUserData());
			}
		}
		
		@FXML
		private void onSaveLocal()
		{
			//CustomDialog.showMessageDialog("압축이 끝난 후 다운로드가 시작됩니다.", sManager.getStage());
			processBox.setVisible(true);
			sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_LOCAL_SAVE_REQUEST));
		}
		
	   @SuppressWarnings("unchecked")
	   @FXML public void onSendListSearch()
	   {
	      String searchKey = MsgSenderField.getText();
	      JSONArray jarr = new JSONArray();
	      for(Object a : jarray)
	      {
	         JSONObject aa = (JSONObject)a;
	         if((aa.get("발신시각").toString().contains(searchKey) || aa.get("수신자").toString().contains(searchKey)) ||(aa.get("메세지제목").toString().contains(searchKey)))
	         {
	            jarr.add(aa);
	         }         
	      }
	      createSendMassageList(jarr);
	      sendmessageList.refresh();
	   }
		
}