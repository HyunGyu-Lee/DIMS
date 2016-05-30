package servers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;

import clients.customcontrols.CalendarObject;
import clients.customcontrols.CalendarObject.CalendarDataManager;
import databases.DatabaseHandler;
import tools.NetworkProtocols;
import tools.Toolbox;

public class DIMS_Server {
	
	private ServerSocket server;
	private static boolean SERVER_RUN = false;
	private boolean PRINT_LOG = true;
	private DatabaseHandler handler;
	private ArrayList<ConnectedClient> clients;
	
	
	public DIMS_Server()
	{
		
		clients = new ArrayList<ConnectedClient>();
		//new Controller().start();
		//serverOpen();
		
	}
	
	public void serverOpen()
	{
		try
		{
			server = new ServerSocket(8080);
			if(PRINT_LOG)
			{
				System.out.println("[Server] 서버 오픈");
			}
			handler = new DatabaseHandler();
			if(PRINT_LOG) System.out.println("[Server] 데이터베이스와 연결 시도...");
			
			int result = handler.connect();
			
			switch(result)
			{
			case DatabaseHandler.DRIVER_INIT_ERROR : if(PRINT_LOG)System.out.println("[Server] JDBC드라이버 설정이 잘못됐습니다."); return;
			case DatabaseHandler.LOGIN_FAIL_ERROR : if(PRINT_LOG)System.out.println("[Server] 데이터베이스에 로그인하지 못했습니다. 아이디 또는 비밀번호를 확인하세요"); return;
			case DatabaseHandler.COMPLETE : if(PRINT_LOG)System.out.println("[Server] 연결 성공");
											SERVER_RUN = true;
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if(PRINT_LOG)System.out.println("[Server] Waiter 스레드 시작");
		new Waiter().start();
	}
	
	class Waiter extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				while(SERVER_RUN)
				{
					if(PRINT_LOG)System.out.println("\t[Waiter] 클라이언트를 기다리는 중입니다...");
					Socket newClient = server.accept();
					if(PRINT_LOG)System.out.println("\t[Waiter] 새로운 클라이언트 접속, Connector 스레드 실행");
					new Connector(newClient).start();

				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally {
				SERVER_RUN = false;
			}
		}
	}
	
	class Connector extends Thread
	{
		Socket client;
		String userName = "unknown";
		String userIdentify = "";
		ObjectInputStream fromClient;
		ObjectOutputStream toClient;
		
		Connector(Socket client)
		{
			this.client = client;
			try
			{
				fromClient = new ObjectInputStream(client.getInputStream());
				toClient = new ObjectOutputStream(client.getOutputStream());
				if(PRINT_LOG)System.out.println("\t\t[Connector-"+userName+"] 스트림 연결 완료");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void run()
		{
			try
			{
				while(SERVER_RUN)
				{
					JSONObject request = null;
					try
					{
						request = (JSONObject)fromClient.readObject();
						if(request==null)
						{
							if(PRINT_LOG)System.out.println("\t\t["+userName+"] 사용자 종료, 스레드 종료");
							break;
						}
						
					}
					catch(ClassNotFoundException e)
					{
						JSONObject respond = new JSONObject();
						respond.put("type", NetworkProtocols.INVALID_REQUEST_ERROR);
						sendProtocol(respond);
						continue;
					}
					
					String type = request.get("type").toString();
					if(PRINT_LOG)System.out.println("\t\t["+userName+"] request type : "+type);
					
					if(type.equals(NetworkProtocols.LOGIN_REQUEST))
					{
						String reqID = request.get("id").toString();
						String reqPassword = request.get("password").toString();
						
						if(PRINT_LOG)System.out.println("\t\t\t[request-"+userName+"] LOGIN_REQUEST, ID : "+reqID+", Password : "+reqPassword);
						ResultSet s = handler.excuteQuery("select * from 사용자 where 학번='"+reqID+"'");
						
						if(s.next())
						{
							String realPassword = s.getString("비밀번호");
							if(realPassword.equals(reqPassword))
							{
								userName = s.getString("이름");
								userIdentify = reqID;
								if(PRINT_LOG)System.out.println("\t\t\t[Server] LOGIN_ACCEPT");
								
								JSONObject respond = new JSONObject();
								respond.put("type", NetworkProtocols.LOGIN_ACCEPT);
								respond.put("user_level", s.getString("사용자등급"));
								sendProtocol(respond);
								/* 이 시점에서  HashMap에 넣어줘야함 */
								clients.add(new ConnectedClient(userIdentify, userName, s.getString("사용자등급").toString(), toClient));
							}
							else
							{
								if(PRINT_LOG)System.out.println("\t\t\t[Server] LOGIN_DENY, Incorrect Password, requset : "+reqPassword+", in database : "+realPassword);
								JSONObject respond = new JSONObject();
								respond.put("type", NetworkProtocols.LOGIN_DENY);
								
								sendProtocol(respond);
							}
						}
						else
						{
							if(PRINT_LOG)System.out.println("\t\t\t[Server] LOGIN_DENY, Not Exist User requset : "+reqID);
							JSONObject respond = new JSONObject();
							respond.put("type", NetworkProtocols.LOGIN_DENY);
							
							sendProtocol(respond);
						}
						s.close();
					}
					else if(type.equals(NetworkProtocols.ID_DUP_CHECK_REQUEST))
					{
						String reqID = request.get("id").toString();
						if(PRINT_LOG)System.out.println("\t\t\t[request-"+userName+"] ID_DUP_CHECK_REQUEST, "+reqID);
						
						ResultSet s = handler.excuteQuery("select * from 사용자 where 학번='"+reqID+"'");
						
						
						if(s.next())
						{
							if(PRINT_LOG)System.out.println("\t\t\t[Server] ID_DUP_RESPOND_DENY");
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ID_DUP_RESPOND_DENY));
						}
						else
						{
							if(PRINT_LOG)System.out.println("\t\t\t[Server] ID_DUP_RESPOND_OK");
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ID_DUP_RESPOND_OK));
						}
						
						s.close();
					}
					else if(type.equals(NetworkProtocols.EXIT_REQUEST))
					{
						sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.EXIT_RESPOND));
						
						while(true)
						{
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.RECIEVE_READY));
							JSONObject r = null;
							try
							{
								r = (JSONObject)fromClient.readObject();
							}
							catch (ClassNotFoundException e)
							{
								e.printStackTrace();
							}
							
							if(r.get("type").equals(NetworkProtocols.RECIEVE_READY_OK))
							{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.PLZ_REQUEST));
								break;
							}
						}
					}
					else if(type.equals(NetworkProtocols.WINDOW_INIT_PROPERTY))
					{
						if(PRINT_LOG)System.out.println("\t\t["+userName+"] window_init_request");
						
						JSONObject respond = new JSONObject();
						respond.put("type", NetworkProtocols.WINDOW_INIT_PROPERTY);
						respond.put("uID", userIdentify);
						respond.put("uName", userName);
						sendProtocol(respond);
						
						if(PRINT_LOG)System.out.println("\t\t\t[Server] send property");
					}
					else if(type.equals(NetworkProtocols.ENROLL_BOARD_REQUEST))
					{
						if(PRINT_LOG)System.out.println(request.toJSONString());
						
						String creator = (String)request.get("작성자");
						String title = (String)request.get("게시글제목");
						String content = (String)request.get("게시글본문");
						String category = (String)request.get("카테고리");
						
						if(creator==null||title.length()==0||content.length()==0||category==null)
						{
							toClient.writeObject(Toolbox.createJSONProtocol(NetworkProtocols.ENROLL_BOARD_ERROR));
							toClient.flush();
							return;
						}
						
						String qry = "insert into 게시글(작성자,게시글제목,게시글본문,카테고리,작성일자) values('"+creator+"','"+title+"','"+content+"','"+category+"',now())";
						
						handler.excuteUpdate(qry);
						if(PRINT_LOG)System.out.println("\t\t\t["+userName+"] "+request.toJSONString());
						
						sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ENROLL_BOARD_RESPOND));
					}
					else if(type.equals(NetworkProtocols.BOARD_LIST_REQUEST))
					{
						ResultSet rs = handler.excuteQuery("select G.게시글번호, S.이름, G.게시글제목, G.작성일자 from 사용자 S, 게시글 G "
								                         + "where S.학번=G.작성자 and G.카테고리='"+request.get("category").toString()+"';");
						JSONArray arr = new JSONArray();
						String[] keys = {"No","이름", "게시글제목", "작성일자"};
						while(rs.next())
						{
							Object[] o = {rs.getInt("게시글번호"),
										  rs.getString("이름"),
									      rs.getString("게시글제목"),
									      rs.getDate("작성일자")}; 
							
							JSONObject n = Toolbox.createJSONProtocol(keys, o);
							arr.add(n);								
						}
						JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_LIST_RESPOND);
						res.put("board_list", arr);
						sendProtocol(res);
						
					}
					else if(type.equals(NetworkProtocols.BOARD_CONTENT_REQUEST))
					{
						int reqno = (int)request.get("No");
						String qry = "select S.이름, G.게시글제목, G.게시글본문, G.카테고리, G.작성일자 from 사용자 S, 게시글 G"
								+    " where S.학번=G.작성자 and G.게시글번호="+reqno+";";
						ResultSet rs = handler.excuteQuery(qry);
						while(rs.next())
						{
							String[] keys = {"이름","게시글제목","게시글본문","카테고리","작성일자"};
							Object[] values = {rs.getString("이름"),rs.getString("게시글제목"),rs.getString("게시글본문"),rs.getString("카테고리"),rs.getDate("작성일자")};
							
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.BOARD_CONTENT_RESPOND, keys, values));
						}
					}
					else if(type.equals(NetworkProtocols.BOARD_SEARCH_REQUEST))
					{
						String category = request.get("category").toString();
						
						ResultSet rs = null;
						if(category=="전체")
						{
							rs = handler.excuteQuery("select G.게시글번호, S.이름, G.게시글제목, G.작성일자 from 사용자 S, 게시글 G "
			                         + "where S.학번=G.작성자 and G.게시글제목 like '%"+request.get("search_key").toString()+"%';");	
						}
						else
						{
							rs = handler.excuteQuery("select G.게시글번호, S.이름, G.게시글제목, G.작성일자 from 사용자 S, 게시글 G "
			                         + "where S.학번=G.작성자 and G.게시글제목 like '%"+request.get("search_key").toString()+"%' and G.카테고리 = '"+category+"';");
						}
						
						if(Toolbox.getResultSetSize(rs)==0)
						{
							sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.BOARD_NO_SEARCH_RESULT));
							continue;
						}
						
						JSONArray arr = new JSONArray();
						String[] keys = {"No","이름", "게시글제목", "작성일자"};
						while(rs.next())
						{
							Object[] o = {rs.getInt("게시글번호"),
										  rs.getString("이름"),
									      rs.getString("게시글제목"),
									      rs.getDate("작성일자")}; 
							
							JSONObject n = Toolbox.createJSONProtocol(keys, o);
							arr.add(n);								
						}
						JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_SEARCH_RESPOND);
						res.put("boardlist", arr);
						sendProtocol(res);
					}
					else if(type.equals(NetworkProtocols.MESSAGE_USER_LIST_REQUEST))
					{
						String qry = "select 학번, 이름 from 사용자;";
						ResultSet rs = handler.excuteQuery(qry);
						
						ArrayList<String> rcList = new ArrayList<String>();
						
						while(rs.next())
						{
							rcList.add(rs.getString("학번")+","+rs.getString("이름"));
						}
						
						JSONObject o = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_USER_LIST_RESPOND);
						o.put("rcList", rcList);
						
						sendProtocol(o);
						
					}
					else if(type.equals(NetworkProtocols.MESSAGE_SEND_REQUEST))
		            {
		                String sender = request.get("sender").toString();
		                ArrayList<String> reciever = (ArrayList<String>) request.get("reciever");
		                String msgTitle = request.get("msgTitle").toString();
		                String msgContent = request.get("msgContent").toString();
		                  
		                for(int i=0; reciever.size()>i ; i++)
		                {
		                	String send_qurey = "insert into 메세지 (발신자,수신자,메세지제목,메세지본문,발신시각) values("+"'"+sender+"'"+","+"'"+reciever.get(i).split(",")[0]+"'"+","+"'"+msgTitle+"'"+","+
		                           "'"+msgContent+"'"+","+"now())";
		                    handler.excuteUpdate(send_qurey);
		                    
		                   
		                    for(ConnectedClient c : clients)
		                    {
		                    	if(c.getClientID().equals(reciever.get(i).split(",")[0]))
		                    	{
		                    		JSONObject obj = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_MESSAGE_DIALOG);
		                    		obj.put("msg", "새로운 메세지가 도착했습니다.");
		                    		c.send(obj);
		                    	}
		                    }
						}
		                
						sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_RESPOND));
		            }
					else if(type.equals(NetworkProtocols.BOARD_MAIN_REQUEST))
					{
						ResultSet rs = handler.excuteQuery("select G.게시글번호, S.이름, G.게시글제목, G.작성일자 from 사용자 S, 게시글 G "
		                         + "where S.학번=G.작성자 and G.카테고리='공지사항';");
						JSONArray arr = new JSONArray();
						
						String[] keys = {"No","이름","게시글제목","작성일자"};
						Object[] values = new Object[4];
						
						while(rs.next())
						{
							values[0] = rs.getString("게시글번호").toString();
							values[1] = rs.getString("이름").toString();
							values[2] = rs.getString("게시글제목").toString();
							values[3] = rs.getDate("작성일자");
							arr.add(Toolbox.createJSONProtocol(keys, values));
						}
						
						JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_MAIN_RESPOND);
						res.put("board_list", arr);
						sendProtocol(res);
					}
					else if(type.equals(NetworkProtocols.MESSAGE_RECIEVE_LIST_REQUEST))
					{
						String qry = "select M.메세지번호, S.이름, M.메세지제목, M.발신시각 from 메세지 M, 사용자 S where M.수신자='"+userIdentify+"' and M.발신자=S.학번";
						
						ResultSet rs = handler.excuteQuery(qry);
						JSONArray mArr = new JSONArray();
						
						String[] keys = {"No","발신자","메세지제목","발신시각"};
						
						while(rs.next())
						{
							Object[] values = {
									rs.getString("메세지번호"),
									rs.getString("이름"),
									rs.getString("메세지제목"),
									rs.getDate("발신시각")
							};
							mArr.add(Toolbox.createJSONProtocol(keys, values));
						}
						
						JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RECIEVE_LIST_RESPOND);
						res.put("message_list", mArr);
						sendProtocol(res);
					}
					else if(type.equals(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST))
					{
						String qry = "select M.메세지번호, S.이름, M.메세지제목, M.발신시각 from 메세지 M, 사용자 S where M.수신자='"+userIdentify+"' and M.발신자=S.학번";
						
						ResultSet rs = handler.excuteQuery(qry);
						JSONArray mArr = new JSONArray();
						
						String[] keys = {"No","발신자","메세지제목","발신시각"};
							
						while(rs.next())
						{
							Object[] values = {
									rs.getString("메세지번호"),
									rs.getString("이름"),
									rs.getString("메세지제목"),
									rs.getDate("발신시각")
							};
							mArr.add(Toolbox.createJSONProtocol(keys, values));
						}
						
						JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_RESPOND);
						res.put("message_list", mArr);
						sendProtocol(res);
					}
					else if(type.equals(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST))
					{
						String qry = "select M.메세지번호, S.이름, M.메세지제목, M.발신시각 from 메세지 M, 사용자 S where M.발신자='"+userIdentify+"' and M.수신자=S.학번";
						
						ResultSet rs = handler.excuteQuery(qry);
						JSONArray mArr = new JSONArray();
						String[] keys = {"No","발신자","메세지제목","발신시각"};
						
						while(rs.next())
						{
							Object[] values = {
									rs.getString("메세지번호"),
									rs.getString("이름"),
									rs.getString("메세지제목"),
									rs.getDate("발신시각")
							};
							mArr.add(Toolbox.createJSONProtocol(keys, values));
						}
						
						JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_LIST_RESPOND);
						res.put("message_list", mArr);
						sendProtocol(res);
					}
					else if(type.equals(NetworkProtocols.VIDIO_REQUEST))
					{
						byte[] arr = Files.readAllBytes(Paths.get("c:\\movie\\독.mp4"));
						 
						JSONObject j = Toolbox.createJSONProtocol(NetworkProtocols.VIDIO_RESPOND);
						j.put("vdata", arr);
						sendProtocol(j);
					}
					else if(type.equals(NetworkProtocols.MESSAGE_CONTENT_REQUEST))
					{
						int reqNo = (int)request.get("No");
						if(PRINT_LOG)System.out.println("메세지 본문 요청번호 : "+reqNo);
						JSONObject send_json = new JSONObject();
						String qry="";
						 
						if(request.get("content_type").toString().equals("send"))
						{
                            send_json.put("content_type", "send");
                            qry = "select M.메세지번호, S.이름, M.메세지제목, M.메세지본문, M.발신시각 from 메세지 M, 사용자 S where M.수신자=S.학번 and M.메세지번호= "+reqNo;									
						}
						else
						{
							send_json.put("content_type", "recieve");
							qry = "select M.메세지번호, S.이름, M.메세지제목, M.메세지본문, M.발신시각 from 메세지 M, 사용자 S where M.발신자=S.학번 and M.메세지번호= "+reqNo;
						}
						
						ResultSet rs = handler.excuteQuery(qry);
						
						while(rs.next())
						{
							String sender_m = rs.getString("이름");
                            String msgTitle_m = rs.getString("메세지제목");
                            String msgContent_m = rs.getString("메세지본문");
                            String sendTime_m = rs.getString("발신시각");

                            send_json.put("type",NetworkProtocols.MESSAGE_CONTENT_RESPOND);
                            send_json.put("발신자", sender_m);
                            send_json.put("메세지제목", msgTitle_m);
                            send_json.put("메세지본문", msgContent_m);
                            send_json.put("발신시각", sendTime_m);
                             
                            sendProtocol(send_json);
						}
					 }
					 else if(type.equals(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_REQUEST))
					 {
						 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_RESPOND);
						 String qry = "";
						 if(request.get("viewtype").equals("달력모드"))
						 {
							 CalendarDataManager c = new CalendarObject().getDataInstance();
							 qry = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(c.getStartDateOfMonth(), c.getEndDateOfMonth())+" order by 일시 asc;";
						 }
						 else
						 {
							 java.util.Date today = new java.util.Date(System.currentTimeMillis());
							 String todayString = new SimpleDateFormat("yyyy-MM-dd").format(today);
							 qry = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(todayString, todayString)+" order by 일시 asc;";							 
						 }
						 System.out.println(qry);
						 JSONArray sList = new JSONArray();
						 try
						 {
							 ResultSet rs = handler.excuteQuery(qry);
							 String[] keys = {"일정번호","일정이름","일시","분류","내용"};
							 while(rs.next())
							 {
								 Object[] values = {rs.getInt("일정번호"),
										 			rs.getString("일정이름"), 
										 			rs.getTimestamp("일시"),
										 			rs.getString("분류"),
										 			rs.getString("내용")};
								 sList.add(Toolbox.createJSONProtocol(keys, values));
							 }
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
						 
						 respond.put("todays", sList);
						 sendProtocol(respond);
					 }
					 else if(type.equals(NetworkProtocols.MODIFY_SCHEDULE_REQUEST))
					 {
						 String qry = "update 일정 set 일정이름='"+request.get("일정이름")+"', "
						 		+ "일시=date_format('"+request.get("일시")+"','%Y-%c-%d %H:%i:%s'), "
						 		+ "분류='"+request.get("분류")+"', "
						 		+ "내용='"+request.get("내용")+"'"
						 		+ "where 일정번호="+request.get("일정번호");
						 handler.excuteUpdate(qry);
						 
						 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_RESPOND);
						 String startDate = request.get("시작일").toString();
						 String endDate = request.get("종료일").toString();
						 
						 java.util.Date today = new java.util.Date(System.currentTimeMillis());
						 String todayString = new SimpleDateFormat("yyyy-MM-dd").format(today);
						 
						 if(startDate.length()==0)
						 {
							 startDate = todayString;
						 }
						 
						 if(endDate.length()==0)
						 {
							 endDate = todayString;							 
						 }
						 
						 String qry2 = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(startDate, endDate)+" order by 일시 asc;";
						 
						 if(request.get("viewtype").equals("달력모드"))
						 {
							 CalendarDataManager c = new CalendarObject().getDataInstance();
							 try
							 {
								 c.getCalander().setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(request.get("일시").toString()));
							 }
							 catch(ParseException e)
							 {
								 e.printStackTrace();
							 }
							 qry2 = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(c.getStartDateOfMonth(), c.getEndDateOfMonth())+" order by 일시 asc;";
						 }
						 System.out.println(qry);
						 JSONArray sList = new JSONArray();
						 try
						 {
							 ResultSet rs = handler.excuteQuery(qry2);
							 String[] keys = {"일정번호","일정이름","일시","분류","내용"};
							 while(rs.next())
							 {
								 Object[] values = {rs.getInt("일정번호"),
										 			rs.getString("일정이름"), 
										 			rs.getTimestamp("일시"),
										 			rs.getString("분류"),
										 			rs.getString("내용")};
								 sList.add(Toolbox.createJSONProtocol(keys, values));
							 }
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
						 
						 respond.put("todays", sList);
						 sendProtocol(respond);
						 
					 }
					 else if(type.equals(NetworkProtocols.ADD_SCHEDULE_REQUEST))
					 {
						 String qry = "insert into 일정(일정이름,일시,분류,내용) values('"+request.get("제목")+"', date_format('"+request.get("일시")+"','%Y-%c-%d %H:%i:%s'), '"+request.get("분류")+"', '"+request.get("내용")+"');";
						 
						 handler.excuteUpdate(qry);
						 
						 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_RESPOND);
						 String startDate = request.get("시작일").toString();
						 String endDate = request.get("종료일").toString();
						 
						 java.util.Date today = new java.util.Date(System.currentTimeMillis());
						 String todayString = new SimpleDateFormat("yyyy-MM-dd").format(today);
						 
						 if(startDate.length()==0)
						 {
							 startDate = todayString;
						 }
						 
						 if(endDate.length()==0)
						 {
							 endDate = todayString;							 
						 }
						 
						 String qry2 = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(startDate, endDate)+" order by 일시 asc;";
						 
						 if(request.get("viewtype").equals("달력모드"))
						 {
							 CalendarDataManager c = new CalendarObject().getDataInstance();
							 try
							 {
								 c.getCalander().setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(request.get("일시").toString()));
							 }
							 catch(ParseException e)
							 {
								 e.printStackTrace();
							 }
							 qry2 = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(c.getStartDateOfMonth(), c.getEndDateOfMonth())+" order by 일시 asc;";
						 }
						 
						 JSONArray sList = new JSONArray();
						 try
						 {
							 ResultSet rs = handler.excuteQuery(qry2);
							 String[] keys = {"일정번호","일정이름","일시","분류","내용"};
							 while(rs.next())
							 {
								 Object[] values = {rs.getInt("일정번호"),
										 			rs.getString("일정이름"), 
										 			rs.getTimestamp("일시"),
										 			rs.getString("분류"),
										 			rs.getString("내용")};
								 sList.add(Toolbox.createJSONProtocol(keys, values));
							 }
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
						 
						 respond.put("todays", sList);
						 sendProtocol(respond);
					 }
					 else if(type.equals(NetworkProtocols.DELETE_SCHEDULE_REQUEST))
					 {
						 String startDate = request.get("시작일").toString();
						 String endDate = request.get("종료일").toString();
						 String qry = "";

						 
						 qry = "delete from 일정 where 일정번호 = "+request.get("일정번호");
						 handler.excuteUpdate(qry);
						 
						 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_RESPOND);
						 
						 
						 java.util.Date today = new java.util.Date(System.currentTimeMillis());
						 String todayString = new SimpleDateFormat("yyyy-MM-dd").format(today);
						 
						 if(startDate.length()==0)
						 {
							 startDate = todayString;
						 }
						 
						 if(endDate.length()==0)
						 {
							 endDate = todayString;							 
						 }
						 
						 String qry2 = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(startDate, endDate)+" order by 일시 asc;";
						 
						 if(request.get("viewtype").equals("달력모드"))
						 {
							 CalendarDataManager c = new CalendarObject().getDataInstance();
							 try
							 {
								 c.getCalander().setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(request.get("일시").toString()));
							 }
							 catch(ParseException e)
							 {
								 e.printStackTrace();
							 }
							 qry2 = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(c.getStartDateOfMonth(), c.getEndDateOfMonth())+" order by 일시 asc;";
						 }
						 
						 JSONArray sList = new JSONArray();
						 try
						 {
							 ResultSet rs = handler.excuteQuery(qry2);
							 String[] keys = {"일정번호","일정이름","일시","분류","내용"};
							 while(rs.next())
							 {
								 Object[] values = {rs.getInt("일정번호"),
										 			rs.getString("일정이름"), 
										 			rs.getTimestamp("일시"),
										 			rs.getString("분류"),
										 			rs.getString("내용")};
								 sList.add(Toolbox.createJSONProtocol(keys, values));
							 }
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
						 
						 respond.put("todays", sList);
						 sendProtocol(respond);
					 }
					 else if(type.equals(NetworkProtocols.SCHEDULE_PROFESSIONAL_SEARCH_REQUEST))
					 {
						 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_SCHEDULE_MANAGER_TAB_RESPOND);
						 String sp = request.get("분류").toString();
						 String qry;
						 if(sp.equals("전체"))
						 {
							 qry = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(request.get("시작일").toString(), request.get("종료일").toString())+" order by 일시 asc;";							 
						 }
						 else
						 {
							 qry = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(request.get("시작일").toString(), request.get("종료일").toString())+"and 분류='"+sp+"' order by 일시 asc;";
						 }
						 
						 JSONArray sList = new JSONArray();
						 try
						 {
							 ResultSet rs = handler.excuteQuery(qry);
							 String[] keys = {"일정번호","일정이름","일시","분류","내용"};
							 while(rs.next())
							 {
								 Object[] values = {rs.getInt("일정번호"),
										 			rs.getString("일정이름"), 
										 			rs.getTimestamp("일시"),
										 			rs.getString("분류"),
										 			rs.getString("내용")};
								 sList.add(Toolbox.createJSONProtocol(keys, values));
							 }
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
						 
						 respond.put("todays", sList);
						 sendProtocol(respond);
						 
					 }
					 else if(type.equals(NetworkProtocols.MONTHLY_SCHEDULE_VIEW_REQUEST))
					 {
						 String qry = "select * from 일정 where "+Toolbox.getWhereStringBetweenDate(request.get("startDate").toString(), request.get("endDate").toString())+" order by 일시 asc;";
						 System.out.println(qry);
						 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.MONTHLY_SCHEDULE_VIEW_RESPOND);
						 
						 JSONArray sList = new JSONArray();
						 try
						 {
							 ResultSet rs = handler.excuteQuery(qry);
							 String[] keys = {"일정번호","일정이름","일시","분류","내용"};
							 while(rs.next())
							 {
								 Object[] values = {rs.getInt("일정번호"),
										 			rs.getString("일정이름"), 
										 			rs.getTimestamp("일시"),
										 			rs.getString("분류"),
										 			rs.getString("내용")};
								 sList.add(Toolbox.createJSONProtocol(keys, values));
							 }
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
						 
						 respond.put("todays", sList);
						 respond.put("viewableDate", request.get("startDate").toString());
						 sendProtocol(respond);
					 }
					 else if(type.equals(NetworkProtocols.SHOW_USER_INFO_TAP_REQUEST))
	                {
	                   String qry = "select S.학번,S.이름 from 사용자 S";
	                   
	                   ResultSet rs = handler.excuteQuery(qry);
	                   
	                   JSONArray mArr = new JSONArray();
	                     
	                   String[] keys = {"학번","이름"};
	                      while(rs.next())
	                     {
	                        Object[] values = {
	                              rs.getString("학번"),
	                              rs.getString("이름")
	                        };
	                        mArr.add(Toolbox.createJSONProtocol(keys, values));
	                     }
	                     // 새로운 제이슨에 프로토콜 넣음
	                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.SHOW_USER_INFO_TAP_RESPOND);
	                     // 제이슨 어레이 넣음 
	                     res.put("user_list", mArr);
	                     sendProtocol(res);
	                }
	                else if(type.equals(NetworkProtocols.USER_CONTENT_REQUEST))
	                {
	                   String name = request.get("이름").toString();
	                   String num = request.get("학번").toString();
	                   
	                   String qry = "select * from 사용자 where 이름='"+name+"'"+"and 학번 = '"+num+"'";
	                   ResultSet rs = handler.excuteQuery(qry);
	                   
	                   JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.USER_CONTENT_RESPOND);
	                   
	                   while(rs.next())
	                   {
	                      String studnet_name = rs.getString("이름");
	                      String student_num = rs.getString("학번");
	                      
	                      json.put("학번", student_num);
	                      json.put("이름", studnet_name);
	                   }
	                   sendProtocol(json);
	                }
	                else if(type.equals(NetworkProtocols.WEABAK_INFO_TAP_REQUEST))
	                {
	                   
	                   String category = request.get("category").toString();
	               
	                   String qry;
	                   ResultSet rs = null;
	                   
	                  
	                   if(category == "main")
	                   {
	                      qry = "select W.외박번호,S.학번 ,S.이름, W.사유 ,W.신청일자 ,W.외박일자 ,W.목적지 ,W.승인여부 from 외박 W, 사용자 S where S.학번 = W.신청자 and W.승인여부 =0 ";
	                      rs = handler.excuteQuery(qry);
	                   }
	                   else if(category.equals("대기중"))
	                   {
	                      qry = "select W.외박번호,S.학번 ,S.이름, W.사유 ,W.신청일자 ,W.외박일자 ,W.목적지 ,W.승인여부 from 외박 W, 사용자 S where S.학번 = W.신청자 and W.승인여부 =0 ";
	                      rs = handler.excuteQuery(qry);
	                   }
	                   else if(category.equals("승인외박"))
	                   {
	                      qry = "select W.외박번호,S.학번 ,S.이름, W.사유 ,W.신청일자 ,W.외박일자 ,W.목적지 ,W.승인여부 from 외박 W, 사용자 S where S.학번 = W.신청자 and W.승인여부 =1 ";
	                      rs = handler.excuteQuery(qry);
	                   }
	                   else
	                   {
	                      qry = "select W.외박번호,S.학번 ,S.이름, W.사유 ,W.신청일자 ,W.외박일자 ,W.목적지 ,W.승인여부 from 외박 W, 사용자 S where S.학번 = W.신청자 and W.승인여부 =2 ";
	                      rs = handler.excuteQuery(qry);
	                   }
	                   
	                   JSONArray jarr = new JSONArray();
	                   String keys[] = {"외박번호","이름","학번","사유","신청일자","외박일자","목적지","승인여부"};
	                   while(rs.next())
	                   {
	                      Object[] values ={
	                            rs.getString("외박번호"),
	                            rs.getString("이름"),
	                            rs.getString("학번"),
	                            rs.getString("사유"),
	                            rs.getString("신청일자"),
	                            rs.getString("외박일자"),
	                            rs.getString("목적지"),
	                            rs.getString("승인여부")
	                      };
	                      
	                      jarr.add(Toolbox.createJSONProtocol(keys, values)); 
	                   }
	                   JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_INFO_TAP_RESPOND);
	                   json.put("weabak_list", jarr);
	                   sendProtocol(json);
	                }
	                else if(type.equals(NetworkProtocols.PLUS_MINUS_TAP_REQUEST))
	                {
	                   String qry = "select * from 사용자";
	                   String plus = null;
	                   String minus = null;
	                   int sum;
	                   ResultSet rs = handler.excuteQuery(qry);
	                   
	                   JSONArray jarr = new JSONArray();
	                   String keys[] = {"학번","이름","상점","벌점","합계"};
	                   while(rs.next())
	                   {
	                      String num = rs.getString("학번");
	                      System.out.println("학번 찍기 : "+num);
	                      
	                      String plusqry = "select sum(점수) from 상벌점부여목록 where 학번 ="+"'"+num+"'"+"and "+"상벌점타입 = '상점'";
	                      String minusqry = "select sum(점수) from 상벌점부여목록 where 학번 ="+"'"+num+"'"+"and "+"상벌점타입 = '벌점'";
	                      
	                      ResultSet rs1 = handler.excuteQuery(plusqry);
	                      ResultSet rs2 = handler.excuteQuery(minusqry);
	                      
	                      while(rs1.next())
	                      {
	                         plus = rs1.getString("sum(점수)");
	                      }
	                      while(rs2.next())
	                      {
	                         minus = rs2.getString("sum(점수)");
	                      }
	                      
	                      if(plus == null)
	                      {
	                         plus = "0";
	                      }
	                      if(minus == null)
	                      {
	                         minus = "0";
	                      }
	                      
	                      System.out.println("상점 : "+plus);
	                      System.out.println("벌점 : "+minus);
	                      
	                      
	                      sum = Integer.parseInt(plus)+Integer.parseInt(minus);
	                      
	                      Object[] values ={
	                            rs.getString("학번"),
	                            rs.getString("이름"),
	                            plus,
	                            minus,
	                            sum
	                      };
	                      
	                      jarr.add(Toolbox.createJSONProtocol(keys, values)); 
	                   }
	                   
	                   JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_TAP_RESPOND);
	                   json.put("plusminus_list", jarr);
	                   sendProtocol(json);
	                }
	                //추가
	                else if(type.equals(NetworkProtocols.WEABAK_CONTENT_REQUEST))
	                {
	                   
	                   int No = (int) request.get("No");
	                   String qry = "select * from 외박 where 외박번호 = "+"'"+No+"'";
	                   String name = request.get("이름").toString();
	                   
	                      ResultSet rs = handler.excuteQuery(qry);
	                      
	                      JSONObject arr = null;
	                      
	                      String keys[] = {"외박번호","학번","사유","신청일자","외박일자","목적지","승인여부"};
	                      while(rs.next())
	                      {
	                         Object values[] = {rs.getInt("외박번호"),rs.getString("신청자"),rs.getString("사유"),rs.getDate("신청일자"),rs.getDate("외박일자"),rs.getString("목적지"),rs.getInt("승인여부")};
	                         arr = Toolbox.createJSONProtocol(keys, values); 
	                      }
	                      arr.put("이름", name);
	                      
	                      JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.WEQBAK_CONTENT_RESPOND);
	                      json.put("weabak_content_list", arr);
	                      sendProtocol(json);
	                  
	                }
	                else if(type.equals(NetworkProtocols.PLUS_MINUS_TAP_INFO_REQUEST))
	                {
	                   String qry = "select P.상벌점부여번호,P.날짜,S.학번 ,S.이름, P.내용 ,P.점수 ,P.상벌점타입  from 상벌점부여목록 P, 사용자 S where S.학번 = P.학번";
	                   
	                   ResultSet rs = handler.excuteQuery(qry);
	                   JSONArray jarray = new JSONArray();
	                   
	                   String keys[] = {"No","날짜","학번","이름","내용","점수","상벌점타입"};
	                   while(rs.next())
	                   {
	                      Object values[] = {
	                            rs.getInt("상벌점부여번호"),
	                            rs.getDate("날짜"),
	                            rs.getString("학번"),
	                            rs.getString("이름"),
	                            rs.getString("내용"),
	                            rs.getInt("점수"),
	                            rs.getString("상벌점타입")       
	                      };
	                      jarray.add(Toolbox.createJSONProtocol(keys, values));
	                   }
	                   JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_TAP_INFO_RESPOND);
	                   json.put("plus_minus_check_list", jarray);
	                   
	                   sendProtocol(json);
	                   
	                }
					 else if(type.equals(NetworkProtocols.MY_OVERNIGHT_LIST_REQUEST))
					 {
						 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.MY_OVERNIGHT_LIST_RESPOND);
						 
						 String qry = "select * from 외박 where 신청자='"+request.get("uID").toString()+"' order by 신청일자 asc;";
						 
						 try
						 {
							 ResultSet rs = handler.excuteQuery(qry);

							 String[] keys = {"신청일자", "외박일자", "목적지", "사유", "승인여부"};
							 JSONArray jArr = new JSONArray();
							 while(rs.next())
							 {
								 Object[] values = {rs.getTimestamp("신청일자"), rs.getDate("외박일자"), rs.getString("목적지"), rs.getString("사유"), rs.getInt("승인여부")};
								 jArr.add(Toolbox.createJSONProtocol(keys, values));
							 }
							 respond.put("data", jArr);
							 sendProtocol(respond);
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
					 }
					 else if(type.equals(NetworkProtocols.ENROLL_OVERNIGHT_REQUEST))
					 {
						 JSONObject obj = Toolbox.createJSONProtocol(NetworkProtocols.ENROLL_OVERNIGHT_RESPOND);
						 // 검사 수행 후 외박 신청 못하는애면 result을 그에맞게지정
						 
						 String qry = "insert into 외박(신청자,사유,신청일자,외박일자,목적지) values('"+userIdentify+"','"+request.get("사유")+"',now(),'"+request.get("외박일자")+"','"+request.get("목적지")+"');";
						 
						 handler.excuteUpdate(qry);
						 
						 obj.put("result", "OK");
						 
						 sendProtocol(obj);
						 
						 for(ConnectedClient c : clients)
						 {
							 if(c.getClientGrade().equals("관리자"))
							 {
				                   String qry2;
				                   ResultSet rs = null;
				                   
				                  
				                   qry2 = "select W.외박번호,S.학번 ,S.이름, W.사유 ,W.신청일자 ,W.외박일자 ,W.목적지 ,W.승인여부 from 외박 W, 사용자 S where S.학번 = W.신청자 and W.승인여부 = 0 ";
				                   rs = handler.excuteQuery(qry2);
				                   
				                   JSONArray jarr = new JSONArray();
				                   String keys[] = {"외박번호","이름","학번","사유","신청일자","외박일자","목적지","승인여부"};
				                   while(rs.next())
				                   {
				                      Object[] values ={
				                            rs.getString("외박번호"),
				                            rs.getString("이름"),
				                            rs.getString("학번"),
				                            rs.getString("사유"),
				                            rs.getString("신청일자"),
				                            rs.getString("외박일자"),
				                            rs.getString("목적지"),
				                            rs.getString("승인여부")
				                      };
				                      
				                      jarr.add(Toolbox.createJSONProtocol(keys, values)); 
				                   }
				                   JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_INFO_TAP_RESPOND);
				                   json.put("weabak_list", jarr);
				                   sendProtocol(json);
							 }
						 }
					 }
					 else if(type.equals(NetworkProtocols.WEABAK_PROCESS_REQUEST))
					 {
						 
						 String action = request.get("action").toString();
						 String reqNo = request.get("reqNo").toString();
						 
						 String qry = "update 외박 set 승인여부 = "+action+" where 외박번호 = "+reqNo+";";
						 
						 handler.excuteUpdate(qry);
						 
						 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.WEABAK_PROCESS_RESPOND));
					 }
					 else if(type.equals(NetworkProtocols.STUDENT_RECIEVE_MESSAGE_REQUEST))
					 {
						 String qry = "select S.이름, M.메세지제목, M.발신시각, M.메세지본문 from 메세지 M, 사용자 S where M.수신자='"+userIdentify+"' and M.발신자=S.학번 order by 발신시각 asc";
							
						ResultSet rs = handler.excuteQuery(qry);
						JSONArray mArr = new JSONArray();
						
						String[] keys = {"발신자","메세지제목","발신시각", "메세지본문"};
						
						while(rs.next())
						{
							Object[] values = {
									rs.getString("이름"),
									rs.getString("메세지제목"),
									rs.getDate("발신시각"),
									rs.getString("메세지본문")
							};
							mArr.add(Toolbox.createJSONProtocol(keys, values));
						}
						
						JSONObject o = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_RECIEVE_MESSAGE_RESPOND);
						o.put("data", mArr);
						sendProtocol(o);
						
					 }
					 else if(type.equals(NetworkProtocols.STUDENT_SEND_MESSAGE_REQUEST))
					 {
						 String qry = "select S.이름, M.메세지제목, M.발신시각, M.메세지본문 from 메세지 M, 사용자 S where M.발신자='"+userIdentify+"' and M.수신자=S.학번 order by 발신시각 asc";
							
						ResultSet rs = handler.excuteQuery(qry);
						JSONArray mArr = new JSONArray();
						
						String[] keys = {"수신자","메세지제목","발신시각", "메세지본문"};
						
						while(rs.next())
						{
							Object[] values = {
									rs.getString("이름"),
									rs.getString("메세지제목"),
									rs.getDate("발신시각"),
									rs.getString("메세지본문")
							};
							mArr.add(Toolbox.createJSONProtocol(keys, values));
						}
						
						JSONObject o = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SEND_MESSAGE_RESPOND);
						o.put("data", mArr);
						sendProtocol(o);
						
					 }
					 else if(type.equals(NetworkProtocols.PLUS_MINUS_ASSIGN_REQUEST))
					 {
						 JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_ASSGIN_RESPOND);
						 json.put("학번", request.get("학번").toString());
						 json.put("이름", request.get("이름").toString());
						 
						 sendProtocol(json);
					 }
					 else if(type.equals(NetworkProtocols.PLUS_MINUS_OVER_REQUEST))
					 {
						 String name = request.get("이름").toString();
						 String num = request.get("학번").toString();
						 String content = request.get("내용").toString();
						 String choice = request.get("상벌점타입").toString();
						 String score = request.get("점수").toString();
						 
						 if(Toolbox.isNumber(score))
						 {
							 if(choice.equals("벌점"))
							 {
								 score = "-"+score;
							 }
						 }
						 String qry = "insert into 상벌점부여목록(학번,상벌점타입,점수,내용,날짜) values('"+num+"' , '"+choice+"' ,"+score+", '"
								 +content+"',now());";
						
						 handler.excuteUpdate(qry);
						 
						 JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.PLUS_MINUS_OVER_RESPOND);
						 sendProtocol(json);
					 }
					 else
					 {
						 if(PRINT_LOG)System.out.println("\t\t\t잘못된 요청");
					 }
				}
			}
			catch (IOException|SQLException e)
			{
				/* 사용자가 종료한거임 */
				if(PRINT_LOG)System.out.println("\t\t[Connector-"+userName+"] 사용자 접속 종료, 스레드 종료");
			}
			
		}
		
		public void sendProtocol(JSONObject protocol) throws IOException
		{
			toClient.writeObject(protocol);
			toClient.flush();
		}
		
	}

	class Controller extends Thread
	{
		@Override
		public void run() {			
			@SuppressWarnings("resource")
			java.util.Scanner sc = new java.util.Scanner(System.in);
			String command = "";
			
			System.out.println("DIMS Server Controller");
			System.out.println("/? or help - show command list, start server - Run server, exit - exit program");
			while(true)
			{
				System.out.print("Input >> ");
				command = sc.nextLine();
				
				if(command.equals("exit"))
				{
					System.exit(0);
				}
				else if(command.equals("/?")||command.equals("help"))
				{
					System.out.println("print help list");
				}
				else if(command.equals("set PRINT_LOG = true"))
				{
					PRINT_LOG = true;
				}
				else if(command.equals("set PRINT_LOG = false"))
				{
					PRINT_LOG = false;
				}
				else if(command.equals("show clients"))
				{
					System.out.println("Current connected clients");
					System.out.println("Clients count : "+ clients.size());
					for(ConnectedClient c : clients)
					{
						System.out.println("ClientID   : "+c.getClientID());
						System.out.println("ClientName : "+c.getClientName());
					}
				}
				else if(command.equals("start server"))
				{
					serverOpen();
				}
				else
				{
					System.out.println("Invalid Command");
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		
		DIMS_Server s = new DIMS_Server();
		
		s.serverOpen();
	
	}
	
}