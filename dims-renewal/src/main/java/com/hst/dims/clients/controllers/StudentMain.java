package com.hst.dims.clients.controllers;

import com.hst.dims.clients.SceneManager;
import com.hst.dims.clients.customcontrols.CustomDialog;
import com.hst.dims.files.FileReciever;
import com.hst.dims.tools.NetworkProtocols;
import com.hst.dims.tools.Statics;
import com.hst.dims.tools.Toolbox;
import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

public class StudentMain implements Initializable {

	private SceneManager sManager;
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private String uName, uID;

	// 최상단
	@FXML ImageView image;
	@FXML Label dateText, dateTime, userName;
	@FXML StackPane OVERNIGHT_MAIN_VIEW;
	@FXML AnchorPane OVERNIGHT_INFO_VIEW;
	@FXML AnchorPane OVERNIGHT_REQUEST_VIEW;


	// 외박관리 - 내역
	@FXML ListView<HBox> overnight_list_view;	// 외박 내역 리스트뷰
	private ObservableList<HBox> overnight_list_view_data;	// 외박 내역 리스트뷰 데이터

	// 외박관리 - 신청
	@FXML DatePicker overnight_date_pciker;	// 희망날짜
	@FXML TextField destination;			// 목적지
	@FXML TextArea reason;					// 사유

	// 메세지
	@FXML StackPane MESSAGE_VIEW;			// 메세지 스택 페인
	@FXML AnchorPane message_re_view;		// 받은 메세지함
	@FXML AnchorPane message_se_view;		// 보낸 메세지함
	@FXML AnchorPane message_write_view;	// 메세지 작성탭

	// 메세지 내역
	@FXML ListView<HBox> recieve_message_view;	// 받은 메세지 리스트
	@FXML ListView<HBox> send_message_view;		// 보낸 메세지 리스트
	private ObservableList<HBox> recieve_message_view_data;	// 받은 메세지 리스트 데이터
	private ObservableList<HBox> send_message_view_data;	// 보낸 메세지 리스트 데이터

	// 메세지 보내기
	@FXML TextField msgTitle, msgRecieverSearchField;	// 제목, 이름검색
	@FXML ListView<HBox> whole_reciever_list, selected_reciever_list;	// 전체 받을사람 리스트, 선택된 사람 리스트
	@FXML TextArea msgContentArea;	// 메세지 본문
	private ObservableList<HBox> wholeRListData, selectedRListData;		// 위 두 개 리스트 데이터
	private ArrayList<String> rcList;	// 전체 받을사람 데이터

	// 게시판
	@FXML StackPane BOARD_VIEW;							// 메인
	@FXML AnchorPane BOARD_SEE_VIEW, BOARD_WRITE_VIEW, BOARD_CONTENT_VIEW;	// 글 확인, 글 작성
	@FXML ListView<HBox> board_list_view;				// 게시글 리스트
	@FXML AnchorPane category_list;						// 아코디언 내 카테고리 목록
	private ObservableList<HBox> boardListData;			// 게시글 리스트 데이터
	private boolean isMainFlow = true;

	@FXML TextField board_title;						// 작성 - 제목
	@FXML ComboBox<String> board_category;				// 작성 - 카테고리선택
	@FXML TextArea board_content;						// 작성 - 본문
	int s_category = 0;
	int current_view_category = 0;

	@FXML TextField board_c_title;
	@FXML Label board_c_time, board_c_creator, board_c_category;
	@FXML TextArea board_c_content;

	@FXML ComboBox<String> qry_type;
	@FXML TextField qry_keyword;

	// 신상정보 조회
	@FXML AnchorPane USER_INFO_VIEW;
	@FXML TextField ui_name, ui_ident, ui_phoneNum, ui_homeNum, ui_addr, ui_sId, ui_major, ui_roomNum, ui_point, ui_newPass, ui_newPassConfirm, ui_answer;
	@FXML ComboBox<String> ui_sex, ui_grade, ui_question;
	@FXML ImageView ui_profile_view;
	@FXML VBox passSetupBox;
	@FXML Label isAuthed;
	private boolean reAuth = false;

	// 시청각교육
	@FXML AnchorPane MEDIA_VIEW;
	MediaPlayer mvPlayer;
	@FXML MediaView mvView;
	@FXML ListView<HBox> mvListView;
	ObservableList<HBox> mvListViewData;
	@FXML Slider timeBar;
	@FXML Label curT, maxT;
	CustomDialog loadingDialog;
	LoadingDialogController loadingController;

	// 메인화면
	@FXML AnchorPane MAIN_VIEW;
	@FXML ListView<HBox> summary_rm_view, summary_board_view;
	@FXML Label mainString;
	private ObservableList<HBox> rm_data, board_data;
	Animation animation;
	@FXML Label processArea, submitTitle, submitDeadLine, submitExist, submitTime;
	@FXML VBox submitInfoBox;
	@FXML Button submitBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final String content = "한국교통대학교 기숙사 정보 관리 시스템";
		animation = new Transition() {
			{
				setCycleDuration(Duration.millis(8000));
			}
			@Override
			protected void interpolate(double frac) {
				final int length = content.length();
				final int n = Math.round(length * (float)frac);
				mainString.setText(content.substring(0, n));
			}
		};

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

		selectedRListData = FXCollections.observableArrayList();
		selected_reciever_list.setItems(selectedRListData);

		qry_type.getItems().addAll("작성자","제목","내용");

		ui_sex.setItems(FXCollections.observableArrayList());
		ui_sex.getItems().addAll("남","여");

		ui_grade.setItems(FXCollections.observableArrayList());
		ui_grade.getItems().addAll("1","2","3","4");

		ui_roomNum.setEditable(false);
		ui_point.setEditable(false);

		recieve_message_view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		send_message_view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		shutdown();

		animation.play();

		MAIN_VIEW.setVisible(true);
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
			System.out.println("StudentMainController 리스너 스레드 시작");

			try
			{
				while(true)
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
							JSONArray data = (JSONArray)line.get("board-data");
							JSONArray data2 = (JSONArray)line.get("message-data");
							JSONObject data3 = (JSONObject)line.get("submit-data");
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									userName.setText(uName);
									createMainBoard(data);
									createMainMessage(data2);
									if(data3==null)
									{
										createNoSubmitUI();
									}
									else
									{
										createExistSubmitUI(data3);
									}
									shutdown();
									MAIN_VIEW.setVisible(true);

									if(new File(Statics.DEFAULT_DOWNLOAD_DIRECTORY).exists()==false)
									{
										CustomDialog.showMessageDialog("다운로드 디렉토리가 자동 생성되었습니다.", sManager.getStage());
										new File(Statics.DEFAULT_DOWNLOAD_DIRECTORY).mkdir();
									}
								}
							});
						}
						else if(type.equals(NetworkProtocols.PLZ_REQUEST))
						{
							JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.WINDOW_INIT_PROPERTY);
							request.put("client-type", "student");
							sendProtocol(request);
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
										CustomDialog.showMessageDialog("외박이 성공적으로 신청됐습니다.", sManager.getStage());
									}
									else
									{
										CustomDialog.showMessageDialog("실패", sManager.getStage());
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
									shutdown();
									msgTitle.setText("");
									msgContentArea.setText("");
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
									shutdown();
									msgTitle.setText("");
									msgContentArea.setText("");
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
									shutdown();
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
									msgTitle.setText("");
									msgContentArea.setText("");
									selected_reciever_list.getItems().clear();
									CustomDialog.showMessageDialog("메세지 발송 성공!", sManager.getStage());
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
						else if(type.equals(NetworkProtocols.BOARD_LIST_RESPOND))
						{
							JSONObject prc = line;
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									createBoardList((JSONArray)prc.get("board_list"));
								}
							});
						}
						else if(type.equals(NetworkProtocols.ENROLL_BOARD_RESPOND))
						{
							BOARD_WRITE_VIEW.setVisible(false);
							BOARD_SEE_VIEW.setVisible(true);

							String[] keys = {"type","category"};
							Object[] values = {NetworkProtocols.BOARD_LIST_REQUEST, s_category};

							sendProtocol(Toolbox.createJSONProtocol(keys, values));

							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									CustomDialog.showMessageDialog("게시글이 등록됐습니다.", sManager.getStage());
								}
							});

						}
						else if(type.equals(NetworkProtocols.STUDENT_USER_INFO_RESPOND))
						{
							JSONObject data = line;

							Platform.runLater(new Runnable() {

								@Override
								public void run() {

									for(Object o : (JSONArray)data.get("질문목록"))
									{
										ui_question.getItems().add(o.toString());
									}


									shutdown();

									ui_name.setText(data.get("이름").toString());
									ui_sex.getSelectionModel().select(data.get("성별").toString());
									ui_ident.setText(data.get("주민등록번호").toString());
									ui_phoneNum.setText(data.get("휴대폰번호").toString());
									ui_homeNum.setText(data.get("자택전화번호").toString());
									ui_addr.setText(data.get("주소").toString());
									ui_sId.setText(data.get("학번").toString());
									ui_grade.getSelectionModel().select(data.get("학년").toString());
									ui_major.setText(data.get("소속학과").toString());
									ui_roomNum.setText(data.get("방번호").toString());
									ui_point.setText(data.get("상벌점").toString());

									ui_question.getSelectionModel().select(data.get("질문내용").toString());
									ui_answer.setText(data.get("비밀번호찾기_답변").toString());

									if(!data.get("이미지데이터").equals("imageX"))
									{
										ui_profile_view.setImage(Toolbox.getWritableByArray((byte[])data.get("이미지데이터")));
									}

									if(reAuth)
									{
										isAuthed.setTextFill(Paint.valueOf("green"));
										isAuthed.setText("인증성공 ");
										passSetupBox.setDisable(false);
									}
									else
									{
										isAuthed.setTextFill(Paint.valueOf("red"));
										isAuthed.setText("비밀번호 설정을 위한 인증이 필요합니다. ");
										passSetupBox.setDisable(true);
									}


									setUINodesEditable(false);

									USER_INFO_VIEW.setVisible(true);
								}
							});
						}
						else if(type.equals(NetworkProtocols.STUDENT_UPLOAD_PROFILE_IMAGE_RESPOND))
						{
							byte[] data = (byte[])line.get("Image-data");

							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									ui_profile_view.setImage(Toolbox.getWritableByArray(data));
								}
							});
						}
						else if(type.equals(NetworkProtocols.STUDENT_MODIFY_USER_INFO_RESPOND))
						{
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									userName.setText(ui_name.getText());
									CustomDialog.showMessageDialog("신상정보가 수정됐습니다.", sManager.getStage());
								}
							});
						}
						else if(type.equals(NetworkProtocols.STUDENT_REAUTH_RESPOND))
						{
							if(line.get("accept").equals("Y"))
							{
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										CustomDialog.showMessageDialog("인증에 성공하셨습니다.", sManager.getStage());
										reAuth = true;
										isAuthed.setTextFill(Paint.valueOf("green"));
										isAuthed.setText("인증성공 ");
										passSetupBox.setDisable(false);
									}
								});
							}
							else
							{
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										CustomDialog.showMessageDialog("비밀번호가 달라 인증에 실패했습니다.", sManager.getStage());
									}
								});
							}
						}
						else if(type.equals(NetworkProtocols.STUDENT_PASSWORD_SETUP_RESPOND))
						{
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									CustomDialog.showMessageDialog("변경사항이 적용되었습니다.", sManager.getStage());
								}
							});
						}
						else if(type.equals(NetworkProtocols.STUDENT_MEDIA_LIST_RESPOND))
						{
							JSONObject respond = line;
							Platform.runLater(new Runnable() {

								@Override
								public void run() {

									createMVList((JSONArray)respond.get("data"));
									shutdown();
									MEDIA_VIEW.setVisible(true);
								}
							});
						}
						else if(type.equals(NetworkProtocols.STUDENT_MEDIA_CONTENT_RESPOND))
						{


						}
						else if(type.equals(NetworkProtocols.STUDENT_SEND_MEDIA_NOTIFICATION))
						{
							JSONObject notification = line;

							if(loadingDialog==null)
							{
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										loadingDialog = new CustomDialog(Statics.LOADING_DIALOG_FXML, Statics.LOADING_DIALOG_TITLE, sManager.getStage(), Modality.NONE);
										loadingController = (LoadingDialogController)loadingDialog.getController();
										loadingController.setMessage("서버로부터 교육영상 내려받는중...");
										loadingController.setProperty(notification.get("name").toString(), (int)notification.get("size"));
										loadingDialog.show();
										String savePathVariable = "";
										FileReciever re = new FileReciever(Statics.DIMS_FILE_SERVER_IP_ADDRESS, Statics.DIMS_FILE_SERVER_PORT_NUMBER);
										re.setSavePathVariable(savePathVariable);
										re.setUI(mvView, mvPlayer, curT, maxT, timeBar, loadingDialog, sManager.getStage());



										re.addDownloadFinishEventHandler(new Runnable() {
											@Override
											public void run() {
												Platform.runLater(new Runnable() {
													@Override
													public void run() {
														System.out.println(savePathVariable);
														if(mvPlayer!=null)
														{
															System.out.println("현재 비디오재생중");
															mvPlayer.dispose();
														}
														mvPlayer = new MediaPlayer(new Media(new File(re.getSavePath()).toURI().toString()));
														mvView.setMediaPlayer(mvPlayer);

														mvPlayer.setOnReady(new Runnable() {

															@Override
															public void run() {
																maxT.setText(Toolbox.getFormattedDuration(mvPlayer.getTotalDuration()));
																loadingDialog.close();
																CustomDialog.showMessageDialog("교육영상 다운로드 성공!", sManager.getStage());
																System.out.println("미디어셋팅 완료");
															}
														});

														curT.setText("00:00");
														timeBar.setValue(0);
														timeBar.valueProperty().addListener(new InvalidationListener() {
															@Override
															public void invalidated(Observable observable) {
																mvPlayer.seek(mvPlayer.getTotalDuration().multiply(timeBar.getValue() / 100.0));
															}
														});
														mvPlayer.currentTimeProperty().addListener(new InvalidationListener() {

															@Override
															public void invalidated(Observable observable) {
																curT.setText(Toolbox.getFormattedDuration(mvPlayer.getCurrentTime()));
															}
														});
													}
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
										String savePathVariable = "";
										FileReciever re = new FileReciever("localhost", 9090);
										re.setSavePathVariable(savePathVariable);
										re.setUI(mvView, mvPlayer, curT, maxT, timeBar, loadingDialog, sManager.getStage());

										re.addDownloadFinishEventHandler(()->{
													if(mvPlayer!=null)
													{
														System.out.println("현재 비디오재생중");
														mvPlayer.dispose();
													}
													System.out.println(savePathVariable);
													mvPlayer = new MediaPlayer(new Media(new File(re.getSavePath()).toURI().toString()));
													mvView.setMediaPlayer(mvPlayer);

													mvPlayer.setOnReady(new Runnable() {

														@Override
														public void run() {
															maxT.setText(Toolbox.getFormattedDuration(mvPlayer.getTotalDuration()));
															loadingDialog.close();
															CustomDialog.showMessageDialog("교육영상 다운로드 성공!", sManager.getStage());
															System.out.println("미디어셋팅 완료");
														}
													});

													curT.setText("00:00");
													timeBar.setValue(0);
													timeBar.valueProperty().addListener(new InvalidationListener() {
														@Override
														public void invalidated(Observable observable) {
															mvPlayer.seek(mvPlayer.getTotalDuration().multiply(timeBar.getValue() / 100.0));
														}
													});
													mvPlayer.currentTimeProperty().addListener(new InvalidationListener() {

														@Override
														public void invalidated(Observable observable) {
															curT.setText(Toolbox.getFormattedDuration(mvPlayer.getCurrentTime()));
														}
													});

												}
										);
										re.openConnection();
										loadingDialog.show();
									}
								});
							}
						}
						else if(type.equals(NetworkProtocols.MESSAGE_RICIEVER_ALL_DELETE_RESPOND))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									CustomDialog.showMessageDialog("전체삭제되었습니다.", sManager.getStage());
								}
							});
							if(line.get("resType").equals("R"))
							{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_RECIEVE_MESSAGE_REQUEST));
							}
							else
							{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SEND_MESSAGE_REQUEST));
							}
						}
						else if(type.equals(NetworkProtocols.MESSAGE_RICIEVER_SELECT_DELETE_RESPOND))
						{
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									CustomDialog.showMessageDialog("선택한 메세지들이 삭제되었습니다.", sManager.getStage());
								}
							});
							if(line.get("resType").equals("R"))
							{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_RECIEVE_MESSAGE_REQUEST));
							}
							else
							{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SEND_MESSAGE_REQUEST));
							}
						}
						else if(type.equals(NetworkProtocols.MESSAGE_REPLY_RESPOND))
						{
							Label left = new Label(line.get("학번").toString());
							Label right = new Label(line.get("이름").toString());

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

							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_USER_LIST_REQUEST));
						}
						else if(type.equals(NetworkProtocols.STUDENT_CATEGORY_LIST_RESPOND))
						{
							JSONArray data = (JSONArray)line.get("category-list");
							Platform.runLater(()->{
								createCategoryList(data);
							});
						}
						else if(type.equals(NetworkProtocols.STUDENT_BOARD_SEARCH_RESPOND))
						{
							JSONArray data = (JSONArray)line.get("data");
							if(data==null)
							{
								Platform.runLater(()->{
									CustomDialog.showMessageDialog("검색결과가 없습니다.", sManager.getStage());
								});
							}
							else
							{
								Platform.runLater(()->{
									createBoardList(data);
								});
							}
						}
						else if(type.equals(NetworkProtocols.BOARD_NO_SEARCH_RESULT))
						{
							Platform.runLater(()->{
								CustomDialog.showMessageDialog("검색결과가 없습니다.", sManager.getStage());
							});
						}
						else if(type.equals(NetworkProtocols.STUDENT_SUBMIT_RESPOND))
						{
							Platform.runLater(()->{
								CustomDialog.showMessageDialog("제출이 완료 됐습니다.", sManager.getStage());
								JSONObject reqeust = Toolbox.createJSONProtocol(NetworkProtocols.WINDOW_INIT_PROPERTY);
								reqeust.put("client-type", "student");
								sendProtocol(reqeust);
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
			}
			catch(IOException e)
			{
				e.printStackTrace();
				Platform.runLater(()->{
					CustomDialog.showConfirmDialog("서버와 연결이 끊어져 프로그램을 강제로 종료합니다.", sManager.getStage());
					System.exit(-1);
				});

			}
		}
	}

	public void setUINodesEditable(boolean value)
	{
		if(value)
		{
			ui_name.setEditable(true);
			ui_sex.setDisable(false);
			ui_ident.setEditable(true);
			ui_phoneNum.setEditable(true);
			ui_homeNum.setEditable(true);
			ui_addr.setEditable(true);
			ui_sId.setEditable(true);
			ui_grade.setDisable(false);
			ui_major.setEditable(true);
		}
		else
		{
			ui_name.setEditable(false);
			ui_sex.setDisable(true);
			ui_ident.setEditable(false);
			ui_phoneNum.setEditable(false);
			ui_homeNum.setEditable(false);
			ui_addr.setEditable(false);
			ui_sId.setEditable(false);
			ui_grade.setDisable(true);
			ui_major.setEditable(false);
		}
	}

	public void shutdown()
	{
		reAuth = false;
		if(mvPlayer!=null)mvPlayer.pause();
		OVERNIGHT_MAIN_VIEW.setVisible(false);
		MESSAGE_VIEW.setVisible(false);
		BOARD_VIEW.setVisible(false);
		USER_INFO_VIEW.setVisible(false);
		MEDIA_VIEW.setVisible(false);
		MAIN_VIEW.setVisible(false);
	}

	public void createMessageList(String string, JSONArray arr)
	{
		String key = string;

		if(key.equals("r"))
		{
			if(recieve_message_view_data==null)
			{
				recieve_message_view_data = FXCollections.observableArrayList();
			}

			recieve_message_view_data.clear();

			for(Object o : arr)
			{
				HBox item = new HBox();
				JSONObject target = (JSONObject)o;

				Label refferKey = new Label(target.get("메세지번호").toString());
				refferKey.setPrefWidth(0);
				refferKey.setVisible(false);

				Label refferContent = new Label(target.get("메세지본문").toString());
				refferContent.setPrefWidth(0);
				refferContent.setVisible(false);

				Label refferUID = new Label(target.get("학번").toString());
				refferUID.setPrefWidth(0);
				refferUID.setVisible(false);


				Label rDate = new Label(Toolbox.getSQLDateToFormat((java.sql.Date)target.get("발신시각"), "yyyy년 MM월 dd일"));
				Label sender = new Label(target.get("발신자").toString());
				Label title = new Label(target.get("메세지제목").toString());

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
				//5,6,7
				item.getChildren().addAll(rDate, s0, sender, s1, title, refferKey, refferContent, refferUID);

				item.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event)
					{
						if(event.getClickCount()==2)
						{
							CustomDialog dlg = new CustomDialog(Statics.CHECK_MESSAGE_FXML, Statics.CHECK_MESSAGE_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
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
			if(send_message_view_data==null)
			{
				send_message_view_data = FXCollections.observableArrayList();
			}

			send_message_view_data.clear();

			for(Object o : arr)
			{
				HBox item = new HBox();
				JSONObject target = (JSONObject)o;

				Label refferKey = new Label(target.get("메세지번호").toString());
				refferKey.setPrefWidth(0);
				refferKey.setVisible(false);

				Label refferContent = new Label(target.get("메세지본문").toString());
				refferContent.setPrefWidth(0);
				refferContent.setVisible(false);

				Label refferUID = new Label(target.get("학번").toString());
				refferUID.setPrefWidth(0);
				refferUID.setVisible(false);

				Label rDate = new Label(Toolbox.getSQLDateToFormat((java.sql.Date)target.get("발신시각"), "yyyy년 MM월 dd일"));
				Label sender = new Label(target.get("수신자").toString());
				Label title = new Label(target.get("메세지제목").toString());

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

				item.getChildren().addAll(rDate, s0, sender, s1, title,refferKey, refferContent, refferUID);
				item.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event)
					{
						if(event.getClickCount()==2)
						{
							CustomDialog dlg = new CustomDialog(Statics.CHECK_MESSAGE_FXML, Statics.CHECK_MESSAGE_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
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
		if(overnight_list_view_data==null)
		{
			overnight_list_view_data = FXCollections.observableArrayList();
		}

		overnight_list_view_data.clear();
		for(Object obj : data)
		{
			JSONObject target = (JSONObject)obj;

			// 신청일자 외박일자 목적지 사유 승인여부
			HBox item = new HBox();

			Label reqDate = new Label(Toolbox.getCurrentTimeFormat((java.sql.Timestamp)target.get("신청일자"), "yyyy년 MM월 dd일 HH:mm:ss"));
			Label reqOverDate = new Label(Toolbox.getSQLDateToFormat((java.sql.Date)target.get("외박일자"), "yyyy-MM-dd"));
			Label destination = new Label(target.get("목적지").toString());
			Label reason = new Label(target.get("사유").toString());

			String iG = "";

			switch(Integer.parseInt(target.get("승인여부").toString()))
			{
				case 0 : iG = "대기중"; break;
				case 1 : iG = "승   인"; break;
				case 2 : iG = "거   절"; break;
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

			// 스타일 입혀야함
			item.getChildren().addAll(reqDate, s0,reqOverDate, s1,destination, s2,reason, s3, isGrant);

			overnight_list_view_data.add(item);
		}

		overnight_list_view.setItems(overnight_list_view_data);
		shutdown();
		OVERNIGHT_MAIN_VIEW.setVisible(true);
		OVERNIGHT_REQUEST_VIEW.setVisible(false);
		OVERNIGHT_INFO_VIEW.setVisible(true);
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void onOvernightListRequest()
	{
		// 외박요청
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
		String[] keys = {"외박일자","목적지","사유"};

		LocalDate reqDate = overnight_date_pciker.getValue();

		if(reqDate==null||destination.getText().length()==0||reason.getText().length()==0)
		{
			CustomDialog.showMessageDialog("필요 정보를 정확하게 입력하세요", sManager.getStage());
			return;
		}

		Object[] values = {reqDate.getYear()+"-"+reqDate.getMonthValue()+"-"+reqDate.getDayOfMonth(),destination.getText(),reason.getText()};

		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ENROLL_OVERNIGHT_REQUEST, keys, values));
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void onMainLogoClicked()
	{
		isMainFlow = true;
		JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.WINDOW_INIT_PROPERTY);
		request.put("client-type", "student");
		sendProtocol(request);
	}

	@FXML
	public void onLogOut()
	{
		sManager.doFullscreen(false);
		sManager.changeListenController("STUDENT_MAIN");
		sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
		//sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.LOGOUT_REQUEST));
	}

	@SuppressWarnings("unchecked")
	@FXML public void onRMessage()
	{
		// 서버로 요청
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
		if(wholeRListData==null)
		{
			wholeRListData = FXCollections.observableArrayList();
		}
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

	public void createBoardList(JSONArray data)
	{
		if(boardListData==null)
		{
			boardListData = FXCollections.observableArrayList();
		}

		boardListData.removeAll(boardListData);

		JSONArray arr = data;

		for(Object o : arr)
		{
			JSONObject target = (JSONObject)o;

			String name = target.get("이름").toString();
			String title = target.get("게시글제목").toString();
			Date create_at = (Date) target.get("작성일자");

			HBox item = new HBox();
			Label ccLabel = new Label(target.get("카테고리").toString());
			Label nLabel = new Label(name);
			Label tLabel = new Label(title);
			Label cLabel = new Label(create_at.toString());
			Label hiddenContent = new Label(target.get("게시글본문").toString());

			ccLabel.setAlignment(Pos.CENTER);
			ccLabel.setFont(Font.font("HYwulM",18));
			ccLabel.setPrefWidth(192);

			nLabel.setAlignment(Pos.CENTER);
			nLabel.setPrefWidth(186);
			nLabel.setFont(Font.font("HYwulM",18));

			tLabel.setPrefWidth(697);
			tLabel.setAlignment(Pos.CENTER);
			tLabel.setFont(Font.font("HYwulM",18));

			cLabel.setFont(Font.font("HYwulM",18));
			cLabel.setAlignment(Pos.CENTER);
			cLabel.setPrefWidth(203);
			hiddenContent.setVisible(false);

			Separator s0 = new Separator(Orientation.VERTICAL);
			s0.setPrefWidth(6);
			Separator s1 = new Separator(Orientation.VERTICAL);
			s1.setPrefWidth(6);
			Separator s2 = new Separator(Orientation.VERTICAL);
			s2.setPrefWidth(6);

			item.getChildren().addAll(ccLabel, s2, nLabel, s0, tLabel, s1, cLabel, hiddenContent);
			item.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if(event.getClickCount()==2)
					{
						board_c_title.setText(tLabel.getText());
						board_c_creator.setText(nLabel.getText());
						board_c_time.setText(cLabel.getText());
						board_c_category.setText(ccLabel.getText());
						board_c_content.setText(hiddenContent.getText());

						board_c_title.setEditable(false);
						board_c_content.setEditable(false);
						BOARD_SEE_VIEW.setVisible(false);
						BOARD_CONTENT_VIEW.setVisible(true);
					}
				}
			});

			boardListData.add(item);
		}
		board_list_view.setItems(boardListData);
		shutdown();
		BOARD_VIEW.setVisible(true);
		BOARD_CONTENT_VIEW.setVisible(false);
		BOARD_WRITE_VIEW.setVisible(false);
		BOARD_SEE_VIEW.setVisible(true);
	}

	@FXML private void onWriteBoard()
	{
		BOARD_CONTENT_VIEW.setVisible(false);
		BOARD_SEE_VIEW.setVisible(false);
		BOARD_WRITE_VIEW.setVisible(true);
	}

	@FXML private void onWriteBoard_toServer()
	{
		s_category = board_category.getSelectionModel().getSelectedIndex()+1;

		String[] ks = {"작성자","게시글제목","게시글본문","카테고리"};
		Object[] vs = {uID, board_title.getText(), board_content.getText(), s_category};
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ENROLL_BOARD_REQUEST, ks, vs));

		board_category.getSelectionModel().select(0);
		board_title.setText("");
		board_content.setText("");
	}

	@FXML private void onBackBoard()
	{
		board_title.setText("");
		board_category.getSelectionModel().select(0);
		board_content.setText("");
		BOARD_WRITE_VIEW.setVisible(false);
		BOARD_SEE_VIEW.setVisible(true);
	}

	@FXML private void onBoardOK()
	{
		if(isMainFlow==true)
		{
			JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_LIST_REQUEST);
			request.put("category", 1);
			sendProtocol(request);
		}
		else
		{
			BOARD_CONTENT_VIEW.setVisible(false);
			BOARD_WRITE_VIEW.setVisible(false);
			BOARD_SEE_VIEW.setVisible(true);
		}
	}

	@FXML private void onUserInfoView()
	{
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_USER_INFO_REQUEST));
	}

	@FXML private void onModifyUI()
	{
		setUINodesEditable(true);
	}

	@FXML private void onSaveUI()
	{
		setUINodesEditable(false);
		String[] keys = {"이름","성별","주민등록번호","휴대폰번호","자택전화번호","주소","학번","학년","소속학과"};
		Object[] values = {ui_name.getText(), ui_sex.getValue(), ui_ident.getText(), ui_phoneNum.getText(), ui_homeNum.getText(), ui_addr.getText(), ui_sId.getText(), ui_grade.getValue(), ui_major.getText()};

		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_MODIFY_USER_INFO_REQUEST, keys, values));

	}

	@FXML private void onReAuth()
	{
		CustomDialog cDlg = new CustomDialog(Statics.STUDENT_REAUTH_DIALOG_FXML, Statics.STUDENT_REAUTH_DIALOG_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
		ReAuthDialogController con = (ReAuthDialogController) cDlg.getController();
		con.setProperty(uID);
		con.setWindow(cDlg);
		cDlg.showAndWait();

		JSONObject request = (JSONObject)cDlg.getUserData();

		if(request!=null)
		{
			sendProtocol(request);
		}

	}

	@FXML private void onPassApp()
	{
		if(!ui_newPass.getText().equals(ui_newPassConfirm.getText()))
		{
			CustomDialog.showMessageDialog("입력한 새 비밀번호를 확인하세요", sManager.getStage());
			return;
		}

		String[] keys = {"새비밀번호","질문","답변"};
		Object[] values = {ui_newPass.getText(), ui_question.getSelectionModel().getSelectedIndex()+1, ui_answer.getText()};

		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_PASSWORD_SETUP_REQUEST, keys, values));

	}

	@FXML private void changeProfileImage()
	{
		FileChooser fc = new FileChooser();
		File selectedImage = fc.showOpenDialog(sManager.getStage());


		if(selectedImage!=null)
		{
			String[] keys = {"fileName", "content"};
			byte[] data = null;
			try
			{
				data = Files.readAllBytes(selectedImage.toPath());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			Object[] values = {selectedImage.getName(), data};

			JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_UPLOAD_PROFILE_IMAGE_REQUEST, keys, values);
			sendProtocol(request);
		}

	}

	@FXML private void onOpenMediaViewer()
	{
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_MEDIA_LIST_REQUEST));
	}

	@FXML private void mvPlay()
	{
		System.out.println(mvPlayer);
		if(mvPlayer!=null)
		{
			mvPlayer.play();
		}
	}

	@FXML private void mvPause()
	{
		if(mvPlayer!=null)
		{
			mvPlayer.pause();
		}
	}

	@FXML private void mvStop()
	{
		if(mvPlayer!=null)
		{
			mvPlayer.stop();
		}
	}

	private void createMVList(JSONArray data)
	{
		if(mvListViewData==null)
		{
			mvListViewData = FXCollections.observableArrayList();
		}

		mvListViewData.clear();

		for(Object o : data)
		{
			JSONObject target = (JSONObject)o;

			Label mvKey = new Label(target.get("비디오번호").toString());
			mvKey.setVisible(false);
			mvKey.setPrefWidth(0);
			Label mvName = new Label(target.get("비디오이름").toString());
			mvName.setPrefSize(400, 35);
			mvName.setFont(Font.font("HYWulM", 20));
			mvName.setStyle("-fx-background-color : linear-gradient(to bottom, rgba(238,238,238,1) 0%,rgba(204,204,204,1) 100%);");

			HBox item = new HBox();

			item.getChildren().addAll(mvName, mvKey);

			item.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@SuppressWarnings("unchecked")
				@Override
				public void handle(MouseEvent event) {
					if(event.getClickCount()==2)
					{
						JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_MEDIA_CONTENT_REQUEST);
						request.put("비디오번호", mvKey.getText());
						sendProtocol(request);
					}
				}
			});

			mvListViewData.add(item);
		}
		mvListView.setItems(mvListViewData);
	}

	@SuppressWarnings("unchecked")
	@FXML private void onReply()
	{
		if(recieve_message_view.getSelectionModel().getSelectedItem()==null)
		{
			CustomDialog.showMessageDialog("답장할 메세지를 먼저 선택하세요!", sManager.getStage());
			return;
		}

		if(recieve_message_view.getSelectionModel().getSelectedItems().size()!=1)
		{
			CustomDialog.showMessageDialog("답장할 메세지를 한 개만 선택하세요!", sManager.getStage());
			return;
		}
		String reUID = ((Label)recieve_message_view.getSelectionModel().getSelectedItem().getChildren().get(7)).getText();

		JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_REPLY_REQUEST);
		request.put("reqID", reUID);

		sendProtocol(request);
	}

	@FXML private void onToss()
	{
		if(recieve_message_view.getSelectionModel().getSelectedItem()==null)
		{
			CustomDialog.showMessageDialog("전달할 메세지를 먼저 선택하세요!", sManager.getStage());
			return;
		}

		if(recieve_message_view.getSelectionModel().getSelectedItems().size()!=1)
		{
			CustomDialog.showMessageDialog("전달할 메세지를 한 개만 선택하세요!", sManager.getStage());
			return;
		}

		msgTitle.setText(((Label)recieve_message_view.getSelectionModel().getSelectedItem().getChildren().get(4)).getText());
		msgContentArea.setText(((Label)recieve_message_view.getSelectionModel().getSelectedItem().getChildren().get(6)).getText());

		message_re_view.setVisible(false);
		message_write_view.setVisible(true);
	}

	@SuppressWarnings("unchecked")
	@FXML private void onSelectedDeleteMessage_R()
	{
		if(recieve_message_view.getSelectionModel().getSelectedItems()==null)
		{
			CustomDialog.showMessageDialog("삭제할 메세지를 먼저 선택하세요!", sManager.getStage());
			return;
		}

		JSONArray delete = new JSONArray();

		for(HBox target : recieve_message_view.getSelectionModel().getSelectedItems())
		{
			JSONObject addV = new JSONObject();
			addV.put("No", Integer.parseInt(((Label)target.getChildren().get(5)).getText()));
			delete.add(addV);
		}

		JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RICIEVER_SELECT_DELETE_REQUEST);
		request.put("reqType", "R");
		request.put("delete", delete);
		sendProtocol(request);
	}

	@SuppressWarnings("unchecked")
	@FXML private void onAllDeleteMessage_R()
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

	@SuppressWarnings("unchecked")
	@FXML private void onSelectedDeleteMessage_S()
	{
		if(send_message_view.getSelectionModel().getSelectedItems()==null)
		{
			CustomDialog.showMessageDialog("삭제할 메세지를 먼저 선택하세요!", sManager.getStage());
			return;
		}

		JSONArray delete = new JSONArray();

		for(HBox target : send_message_view.getSelectionModel().getSelectedItems())
		{
			JSONObject addV = new JSONObject();
			addV.put("No", Integer.parseInt(((Label)target.getChildren().get(5)).getText()));
			delete.add(addV);
		}

		JSONObject request = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RICIEVER_SELECT_DELETE_REQUEST);
		request.put("reqType", "S");
		request.put("delete", delete);
		sendProtocol(request);
	}

	@SuppressWarnings("unchecked")
	@FXML private void onAllDeleteMessage_S()
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

	@FXML private void openDocument(ActionEvent e)
	{
		Hyperlink target = (Hyperlink)e.getSource();
		sManager.getHost().getHostServices().showDocument(target.getText());
	}

	private void createCategoryList(JSONArray data)
	{
		category_list.getChildren().clear();
		board_category.getItems().clear();
		ObservableList<String> category = FXCollections.observableArrayList();

		double yPosi = 23;

		for(int i=0;i<data.size();i++)
		{
			String target = data.get(i).toString();
			Button add = new Button(target);
			add.getStyleClass().add("Button_subItem");
			add.setLayoutX(45);
			add.setLayoutY(yPosi);
			yPosi+=60;
			category.add(target);
			category_list.getChildren().add(add);
			int cateIdx = i+1;
			add.setOnAction((e)->{
				isMainFlow = false;
				String[] keys = {"type","category"};
				Object[] values = {NetworkProtocols.BOARD_LIST_REQUEST , cateIdx};
				current_view_category = cateIdx;
				sendProtocol(Toolbox.createJSONProtocol(keys, values));
			});
		}

		board_category.setItems(category);
	}

	@FXML private void onClickBoard()
	{
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CATEGORY_LIST_REQUEST));
	}

	@FXML private void onSearchBoard()
	{
		if(qry_type.getValue()==null)
		{
			CustomDialog.showMessageDialog("검색 조건을 선택하세요.", sManager.getStage());
			return;
		}

		if(qry_keyword.getText().length()==0)
		{
			CustomDialog.showMessageDialog("검색어를 한 글자 이상 입력하세요.", sManager.getStage());
			return;
		}
		// 작성자 제목 내용
		String[] keys = {"카테고리","검색조건","검색어"};
		Object[] values = {current_view_category,qry_type.getValue(),qry_keyword.getText()};
		sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_BOARD_SEARCH_REQUEST, keys, values));
	}

	private void createMainBoard(JSONArray data)
	{
		if(board_data==null)
		{
			board_data = FXCollections.observableArrayList();
		}

		board_data.removeAll(board_data);

		for(Object o : data)
		{
			JSONObject target = (JSONObject)o;

			String name = target.get("이름").toString();
			String title = target.get("게시글제목").toString();
			Date create_at = (Date) target.get("작성일자");

			HBox item = new HBox();
			Label ccLabel = new Label(target.get("카테고리").toString());
			ccLabel.setVisible(false);
			ccLabel.setPrefWidth(0);
			Label nLabel = new Label(name);
			Label tLabel = new Label(title);
			Label cLabel = new Label(create_at.toString());
			cLabel.setVisible(false);
			cLabel.setPrefWidth(0);
			Label hiddenContent = new Label(target.get("게시글본문").toString());

			ccLabel.setAlignment(Pos.CENTER);
			ccLabel.setFont(Font.font("HYwulM",18));

			nLabel.setAlignment(Pos.CENTER);
			nLabel.setMinWidth(0);
			nLabel.setPrefSize(129,30);
			nLabel.setFont(Font.font("HYwulM",18));

			tLabel.setPrefSize(470,30);
			tLabel.setAlignment(Pos.CENTER);
			tLabel.setFont(Font.font("HYwulM",18));

			cLabel.setFont(Font.font("HYwulM",18));
			cLabel.setAlignment(Pos.CENTER);
			hiddenContent.setVisible(false);
			hiddenContent.setPrefWidth(0);

			Separator s0 = new Separator(Orientation.VERTICAL);
			s0.setPrefWidth(6);
			Separator s1 = new Separator(Orientation.VERTICAL);
			s1.setPrefWidth(6);

			item.getChildren().addAll(nLabel, s0, tLabel, s1, cLabel, hiddenContent, ccLabel);
			item.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if(event.getClickCount()==2)
					{
						board_c_title.setText(tLabel.getText());
						board_c_creator.setText(nLabel.getText());
						board_c_time.setText(cLabel.getText());
						board_c_category.setText(ccLabel.getText());
						board_c_content.setText(hiddenContent.getText());

						board_c_title.setEditable(false);
						board_c_content.setEditable(false);
						shutdown();
						BOARD_VIEW.setVisible(true);
						BOARD_SEE_VIEW.setVisible(false);
						BOARD_CONTENT_VIEW.setVisible(true);
					}
				}
			});

			board_data.add(item);
		}
		summary_board_view.setItems(board_data);
	}

	private void createMainMessage(JSONArray data)
	{
		if(rm_data==null)
		{
			rm_data = FXCollections.observableArrayList();
		}

		rm_data.clear();

		for(Object o : data)
		{
			HBox item = new HBox();
			JSONObject target = (JSONObject)o;

			Label refferKey = new Label(target.get("메세지번호").toString());
			refferKey.setPrefWidth(0);
			refferKey.setVisible(false);

			Label refferContent = new Label(target.get("메세지본문").toString());
			refferContent.setPrefWidth(0);
			refferContent.setVisible(false);

			Label refferUID = new Label(target.get("학번").toString());
			refferUID.setPrefWidth(0);
			refferUID.setVisible(false);

			Label rDate = new Label(Toolbox.getSQLDateToFormat((java.sql.Date)target.get("발신시각"), "yyyy년 MM월 dd일"));
			rDate.setVisible(false);
			rDate.setPrefWidth(0);
			Label sender = new Label(target.get("발신자").toString());
			sender.setMinWidth(0);
			Label title = new Label(target.get("메세지제목").toString());

			Separator s0 = new Separator(Orientation.VERTICAL);
			s0.setPrefWidth(6);
			Separator s1 = new Separator(Orientation.VERTICAL);
			s1.setPrefWidth(6);

			sender.setPrefSize(128, 30);
			sender.setFont(Font.font("HYwulM",15));
			sender.setAlignment(Pos.CENTER);

			title.setPrefSize(470, 30);
			title.setFont(Font.font("HYwulM",15));
			title.setAlignment(Pos.CENTER);

			item.getChildren().addAll(sender, s1, title);

			item.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(event.getClickCount()==2)
					{
						CustomDialog dlg = new CustomDialog(Statics.CHECK_MESSAGE_FXML, Statics.CHECK_MESSAGE_TITLE, sManager.getStage(), Modality.WINDOW_MODAL);
						CheckMessageDialog_Controller con = (CheckMessageDialog_Controller)dlg.getController();
						con.setWindow(dlg);
						con.setProperty(target);
						dlg.show();
					}
				};
			});

			rm_data.add(item);
		}
		summary_rm_view.setItems(rm_data);
	}

	@FXML private void onSubmitStudent()
	{
		if(CustomDialog.showConfirmDialog("여러개의 경우 압축하여 제출하세요.", sManager.getStage())==CustomDialog.OK_OPTION)
		{
			FileChooser s = new FileChooser();
			File selectedFile = s.showOpenDialog(sManager.getStage());

			if(selectedFile!=null)
			{
				String fileName = selectedFile.getName();
				int dotCount = 0;
				for(int i=0;i<fileName.length();i++)
				{
					if(fileName.charAt(i)=='.')
					{
						dotCount++;
					}
				}

				fileName = fileName.split("\\.")[dotCount];

				byte[] sendable = null;
				try
				{
					sendable = Files.readAllBytes(selectedFile.toPath());
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				System.out.println("파일명               : "+fileName);
				System.out.println("보낼 데이터 길이 : "+sendable.length);
				// 여기서 JSON만들어서 데이터 담아서 요청
				String[] keys = {"확장자","데이터"};
				Object[] values = {fileName, sendable};
				sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SUBMIT_REQUEST, keys, values));
			}

		}
	}

	private void createNoSubmitUI()
	{
		processArea.setText("진행중인 서류제출 없음");
		submitTitle.setText("");
		submitDeadLine.setText("");
		submitExist.setText("");
		submitTime.setText("");
		submitBtn.setDisable(true);
		submitInfoBox.setDisable(true);
	}

	private void createExistSubmitUI(JSONObject data)
	{
		processArea.setText("진행중");
		submitBtn.setDisable(false);
		submitInfoBox.setDisable(false);

		submitTitle.setText(data.get("제출분류명").toString());
		submitDeadLine.setText(Toolbox.getSQLDateToFormat((java.sql.Date)data.get("마감시간"), "yyyy-MM-dd")+"까지");
		if(data.get("제출여부").equals("미제출"))
		{
			submitExist.setText(data.get("제출여부").toString());
			submitTime.setText(data.get("제출시각").toString());
		}
		else
		{
			submitExist.setText(data.get("제출여부").toString());
			submitExist.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					byte[] file = (byte[])data.get("데이터");
					try
					{
						String saveFile = Statics.DEFAULT_DOWNLOAD_DIRECTORY+System.currentTimeMillis()+"."+data.get("확장자");
						Files.write(new File(saveFile).toPath(), file);
						Runtime.getRuntime().exec("explorer.exe /select,"+saveFile);
						System.out.println(saveFile);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			});
			submitTime.setText(data.get("제출시각").toString().split("\\.")[0]);
		}
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