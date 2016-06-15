package servers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import clients.customcontrols.CalendarObject;
import clients.customcontrols.CalendarObject.CalendarDataManager;
import databases.DatabaseHandler;
import files.DIMSFileServer;
import tools.MailProperties;
import tools.MailProperty;
import tools.MailingService;
import tools.NetworkProtocols;
import tools.Statics;
import tools.Toolbox;

public class DIMS_Server {
	
	private ServerSocket server;
	private static boolean SERVER_RUN = false;
	private boolean PRINT_LOG = true;
	private DatabaseHandler handler;
	private ArrayList<ConnectedClient> clients;
	DIMSFileServer fileServer;
	
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
		fileServer = new DIMSFileServer(9090);
		
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
		String clientIP = "";

		Connector(Socket client)
		{
			this.client = client;
			clientIP = client.getInetAddress().getHostAddress();
			System.out.println(clientIP);
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
						System.out.println("요청 : "+request);
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
								System.out.println(r);
							}
							catch (ClassNotFoundException e)
							{
								e.printStackTrace();
							}
							
							if(r.get("type").equals(NetworkProtocols.RECIEVE_READY_OK))
							{
								if(r.get("request-view")!=null)
								{
									JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.PASSWORD_FIND_QUESTION_LIST);
									ResultSet rs = handler.excuteQuery("select 질문내용 from 비밀번호찾기_질문목록");
									JSONArray data = new JSONArray();
									while(rs.next())
									{
										data.add(rs.getString("질문내용"));
									}
									respond.put("data", data);
									sendProtocol(respond);
								}
								else
								{
									sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.PLZ_REQUEST));
								}
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
						
						if(request.get("client-type")!=null)
						{
							ResultSet rs = handler.excuteQuery("select G.게시글번호, S.이름, G.게시글제목, G.작성일자, G.게시글본문, M.카테고리이름 from 사용자 S, 게시글 G, 게시글_카테고리목록 M "
			                         + "where S.학번=G.작성자 and G.카테고리=1 and M.카테고리번호=G.카테고리;");
							
							ResultSet rs2 = handler.excuteQuery("select M.메세지번호, S.학번, S.이름, M.메세지제목, M.발신시각, M.메세지본문  from 메세지 M, 사용자 S where M.수신자='"+userIdentify+"' and M.발신자=S.학번 and M.구분='R' order by 발신시각 asc");
							
							String[] keys = {"No","이름", "게시글제목", "작성일자", "게시글본문", "카테고리"};
							JSONArray data = new JSONArray();
							try
							{
								while(rs.next())
								{
									Object[] o = {rs.getInt("게시글번호"),
												  rs.getString("이름"),
											      rs.getString("게시글제목"),
											      rs.getDate("작성일자"),
											      rs.getString("게시글본문"),
											      rs.getString("카테고리이름")}; 
									
									JSONObject n = Toolbox.createJSONProtocol(keys, o);
									data.add(n);								
								}
								
								String[] keys2 = {"메세지번호","학번","발신자","메세지제목","발신시각", "메세지본문"};
								JSONArray mArr = new JSONArray();
								while(rs2.next())
								{
									Object[] values = {
											rs2.getInt("메세지번호"),
											rs2.getString("학번"),
											rs2.getString("이름"),
											rs2.getString("메세지제목"),
											rs2.getDate("발신시각"),
											rs2.getString("메세지본문")
									};
									mArr.add(Toolbox.createJSONProtocol(keys2, values));
								}
								
								ResultSet rs3 = handler.excuteQuery("select * from 제출서류목록");

								JSONObject dataBundle = null;
								if(Toolbox.getResultSetSize(rs3)!=0)
								{
									try
									{
										dataBundle = new JSONObject();
										rs3.next();
										dataBundle.put("제출분류명", rs3.getString("제출분류명"));
										dataBundle.put("마감시간", rs3.getDate("마감시간"));
										
										ResultSet rs4 = handler.excuteQuery("select M.서류번호,M.제출자,S.이름,M.제출서류저장URL,M.제출시간 from 제출서류함 M, 사용자 S where M.제출자=S.학번 and M.제출자 = '"+userIdentify+"';");
										rs4.next();
										dataBundle.put("서류번호", rs4.getInt("서류번호"));
										
										if(rs4.getString("제출서류저장URL")!=null)
										{
											dataBundle.put("제출여부", "제출완료");
											dataBundle.put("제출시각", rs4.getTimestamp("제출시간"));
											dataBundle.put("데이터", Files.readAllBytes(new File(rs4.getString("제출서류저장URL")).toPath()));
											dataBundle.put("확장자", rs4.getString("제출서류저장URL").split("\\.")[1]);
										}
										else
										{
											dataBundle.put("제출여부", "미제출");
											dataBundle.put("제출시각", "-----------");									
										}
										
									}
									catch(SQLException e)
									{
										e.printStackTrace();
									}
									
								}
								
								respond.put("board-data", data);
								respond.put("message-data", mArr);
								respond.put("submit-data", dataBundle);
							}
							catch(SQLException e)
							{
								e.printStackTrace();
							}
							
						}
						
						sendProtocol(respond);
						
						if(PRINT_LOG)System.out.println("\t\t\t[Server] send property");
					}
					else if(type.equals(NetworkProtocols.ENROLL_BOARD_REQUEST))
					{
						if(PRINT_LOG)System.out.println(request.toJSONString());
						
						String creator = (String)request.get("작성자");
						String title = (String)request.get("게시글제목");
						String content = (String)request.get("게시글본문");
						int category = (int)request.get("카테고리");
						
						if(creator==null||title.length()==0||content.length()==0||request.get("카테고리")==null)
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
					else if(type.equals(NetworkProtocols.ADMIN_ADD_TAP_REQUEST))
					{
						String qry = "select count(*) from 게시글_카테고리목록;";
						ResultSet rs = handler.excuteQuery(qry);
						rs.next();
						int cnt = rs.getInt("count(*)");
						
						qry = "insert into 게시글_카테고리목록(카테고리번호,카테고리이름) values("+(cnt+1)+",'"+request.get("name")+"')";
						
						handler.excuteUpdate(qry);
						
						sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_ADD_TAP_RESPOND));
						
					}
					else if(type.equals(NetworkProtocols.ADMIN_BOARD_DELETE_REQUEST))
					{
						JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_BOARD_DELETE_RESPOND);
						String qry2 = "select 카테고리 from 게시글 where 게시글번호="+request.get("게시글번호")+";";
						
						ResultSet rs = handler.excuteQuery(qry2);
						
						rs.next();
						respond.put("show-category", rs.getInt("카테고리"));
						
						String qry = "delete from 게시글 where 게시글번호="+request.get("게시글번호")+";";
						handler.excuteUpdate(qry);
						
						sendProtocol(respond);
					}
					else if(type.equals(NetworkProtocols.BOARD_LIST_REQUEST))
					{
						ResultSet rs = handler.excuteQuery("select G.게시글번호, S.이름, G.게시글제목, G.작성일자, G.게시글본문, M.카테고리이름 from 사용자 S, 게시글 G, 게시글_카테고리목록 M "
								                         + "where S.학번=G.작성자 and G.카테고리='"+request.get("category").toString()+"' and M.카테고리번호=G.카테고리;");
						JSONArray arr = new JSONArray();
						JSONArray arr2 = new JSONArray();
						
						String[] keys = {"No","이름", "게시글제목", "작성일자", "게시글본문", "카테고리"};
						try
						{
							while(rs.next())
							{
								Object[] o = {rs.getInt("게시글번호"),
											  rs.getString("이름"),
										      rs.getString("게시글제목"),
										      rs.getDate("작성일자"),
										      rs.getString("게시글본문"),
										      rs.getString("카테고리이름")}; 
								
								JSONObject n = Toolbox.createJSONProtocol(keys, o);
								arr.add(n);								
							}
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
						
						String qry2 = "select 카테고리이름 from 게시글_카테고리목록;";
						
						ResultSet rs2 = handler.excuteQuery(qry2);
						
						try
						{
							while(rs2.next())
							{
								arr2.add(rs2.getString(1));
							}
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
						
						JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_LIST_RESPOND);
						res.put("board_list", arr);
						res.put("category_list", arr2);
						sendProtocol(res);
						System.out.println("응답");
					}
					else if(type.equals(NetworkProtocols.BOARD_CONTENT_REQUEST))
					{
						int reqno = (int)request.get("No");
						String qry = "select S.이름, G.게시글제목, G.게시글본문, M.카테고리이름, G.작성일자 from 사용자 S, 게시글 G, 게시글_카테고리목록 M"
								+    " where S.학번=G.작성자 and G.게시글번호="+reqno+" and G.카테고리=M.카테고리번호; ";
						ResultSet rs = handler.excuteQuery(qry);
						while(rs.next())
						{
							String[] keys = {"이름","게시글제목","게시글본문","카테고리","작성일자"};
							Object[] values = {rs.getString("이름"),rs.getString("게시글제목"),rs.getString("게시글본문"),rs.getString("카테고리이름"),rs.getDate("작성일자")};
							
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
		                	String send_qurey = "insert into 메세지 (발신자,수신자,메세지제목,메세지본문,발신시각,구분) values("+"'"+sender+"'"+","+"'"+reciever.get(i).split(",")[0]+"'"+","+"'"+msgTitle+"'"+","+
		                           "'"+msgContent+"'"+","+"now(),'S')";
		                    handler.excuteUpdate(send_qurey);
		                   
		                    send_qurey = "insert into 메세지 (발신자,수신자,메세지제목,메세지본문,발신시각,구분) values("+"'"+sender+"'"+","+"'"+reciever.get(i).split(",")[0]+"'"+","+"'"+msgTitle+"'"+","+
			                           "'"+msgContent+"'"+","+"now(),'R')";
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
		                         + "where S.학번=G.작성자 and G.카테고리=1;");
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
						
						JSONArray arr2 = new JSONArray();
						String qry2 = "select 카테고리이름 from 게시글_카테고리목록;";
						
						ResultSet rs2 = handler.excuteQuery(qry2);
						
						try
						{
							while(rs2.next())
							{
								arr2.add(rs2.getString(1));
							}
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
						
						JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_MAIN_RESPOND);
						res.put("board_list", arr);
						res.put("category_list", arr2);
						sendProtocol(res);
					}
					else if(type.equals(NetworkProtocols.MESSAGE_RECIEVE_LIST_REQUEST))
		               {
		                 try
		                 {
		                     String qry = "select M.메세지번호, S.이름, S.학번 , M.메세지제목, M.메세지본문 ,M.발신시각 from 메세지 M, 사용자 S where M.수신자='"+userIdentify+"' and M.발신자=S.학번 and M.구분='R' order by 발신시각 asc" ;
		                     
		                     ResultSet rs = handler.excuteQuery(qry);
		                     JSONArray mArr = new JSONArray();
		                     
		                     String[] keys = {"No","발신자","학번","메세지제목","메세지본문","발신시각"};
		                        
		                     while(rs.next())
		                     {
		                        Object[] values = {
		                              rs.getString("메세지번호"),
		                              rs.getString("이름"),
		                              rs.getString("학번"),
		                              rs.getString("메세지제목"),
		                              rs.getString("메세지본문"),
		                              rs.getDate("발신시각")
		                            
		                        };
		                        mArr.add(Toolbox.createJSONProtocol(keys, values));
		                      }
		                     
		                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RECIEVE_LIST_RESPOND);
		                     res.put("message_list", mArr);
		                     sendProtocol(res);
		                 }
		                 catch(Exception e)
		                 {
		                    e.printStackTrace();
		                 }
		            }
					else if(type.equals(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_REQUEST))
		               {
		                 try
		                 {
		                     String qry = "select M.메세지번호, S.이름, S.학번 , M.메세지제목, M.메세지본문 ,M.발신시각 from 메세지 M, 사용자 S where M.수신자='"+userIdentify+"' and M.발신자=S.학번 and M.구분='R' order by 발신시각 asc" ;
		                     
		                     ResultSet rs = handler.excuteQuery(qry);
		                     JSONArray mArr = new JSONArray();
		                     
		                     String[] keys = {"No","발신자","학번","메세지제목","메세지본문","발신시각"};
		                        
		                     while(rs.next())
		                     {
		                        Object[] values = {
		                              rs.getString("메세지번호"),
		                              rs.getString("이름"),
		                              rs.getString("학번"),
		                              rs.getString("메세지제목"),
		                              rs.getString("메세지본문"),
		                              rs.getDate("발신시각")
		                            
		                        };
		                        mArr.add(Toolbox.createJSONProtocol(keys, values));
		                      }
		                     
		                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SHOW_MESSAGE_TAP_RESPOND);
		                     res.put("message_list", mArr);
		                     sendProtocol(res);
		                 }
		                 catch(Exception e)
		                 {
		                    e.printStackTrace();
		                 }
		                  }
		               else if(type.equals(NetworkProtocols.MESSAGE_SEND_LIST_REQUEST))
		               {
		                  String qry = "select M.메세지번호, S.이름, S.학번 , M.메세지제목, M.메세지본문 ,M.발신시각  from 메세지 M, 사용자 S where M.발신자='"+userIdentify+"' and M.수신자=S.학번 and M.구분='S' order by 발신시각 asc";
		                  
		                  ResultSet rs = handler.excuteQuery(qry);
		                  JSONArray mArr = new JSONArray();
		                  String[] keys = {"No","수신자","학번","메세지제목","메세지본문","발신시각"};
		                  
		                  while(rs.next())
		                      {
		                           Object[] values = {
		                                 rs.getString("메세지번호"),
		                                 rs.getString("이름"),
		                                 rs.getString("학번"),
		                                 rs.getString("메세지제목"),
		                                 rs.getString("메세지본문"),
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
						 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 order by S.방번호";
	                      
	                      ResultSet rs = handler.excuteQuery(qry);
	                      
	                      JSONArray mArr = new JSONArray();
	                        
	                      String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
	                         while(rs.next())
	                        {
	                           Object[] values = {
	                                 rs.getString("학번"),
	                                 rs.getString("이름"),
	                                 rs.getString("주소"),
	                                 rs.getString("휴대폰번호"),
	                                 rs.getString("자택전화번호"),
	                                 rs.getString("주민등록번호"),
	                                 rs.getString("성별"),
	                                 rs.getString("방번호"),
	                                 rs.getInt("학년"),
	                                 rs.getString("학과이름")
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
	                       String num = request.get("학번").toString();
	                       
	                       System.out.println(num);
	                       String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름, S.프로필사진URL  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.학번 = '"+num+"'";
	                       ResultSet rs = handler.excuteQuery(qry);
	                     
	                       JSONObject mArr = new JSONObject();
	                             
	                       String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과","이미지데이터"};
	                       while(rs.next())
	                       {
	                          if(rs.getString("프로필사진URL")!=null)
	                          {
	                              Object[] values = {
	                                     rs.getString("학번"),
	                                     rs.getString("이름"),
	                                     rs.getString("주소"),
	                                     rs.getString("휴대폰번호"),
	                                     rs.getString("자택전화번호"),
	                                     rs.getString("주민등록번호"),
	                                     rs.getString("성별"),
	                                     rs.getString("방번호"),
	                                     rs.getInt("학년"),
	                                     rs.getString("학과이름"),
	                                     Files.readAllBytes(new File(rs.getString("프로필사진URL")).toPath())
	                                     };
	                              mArr = Toolbox.createJSONProtocol(NetworkProtocols.USER_CONTENT_RESPOND, keys, values);
	                          }
	                          else
	                          {
	                              Object[] values = {
	                                     rs.getString("학번"),
	                                     rs.getString("이름"),
	                                     rs.getString("주소"),
	                                     rs.getString("휴대폰번호"),
	                                     rs.getString("자택전화번호"),
	                                     rs.getString("주민등록번호"),
	                                     rs.getString("성별"),
	                                     rs.getString("방번호"),
	                                     rs.getInt("학년"),
	                                     rs.getString("학과이름"),
	                                     "no-image"
	                                     };
	                              mArr = Toolbox.createJSONProtocol(NetworkProtocols.USER_CONTENT_RESPOND, keys, values);
	                          }

	                       }

	                       sendProtocol(mArr);  
	               
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
						 String qry = "select M.메세지번호, S.학번, S.이름, M.메세지제목, M.발신시각, M.메세지본문  from 메세지 M, 사용자 S where M.수신자='"+userIdentify+"' and M.발신자=S.학번 and M.구분='R' order by 발신시각 asc";
							
						ResultSet rs = handler.excuteQuery(qry);
						JSONArray mArr = new JSONArray();
						
						String[] keys = {"메세지번호","학번","발신자","메세지제목","발신시각", "메세지본문"};
						
						while(rs.next())
						{
							Object[] values = {
									rs.getInt("메세지번호"),
									rs.getString("학번"),
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
						String qry = "select M.메세지번호, S.학번, S.이름, M.메세지제목, M.발신시각, M.메세지본문 from 메세지 M, 사용자 S where M.발신자='"+userIdentify+"' and M.수신자=S.학번 and M.구분='S' order by 발신시각 asc";
						System.out.println("실행되는 쿼리 : "+qry);
						ResultSet rs = handler.excuteQuery(qry);
						JSONArray mArr = new JSONArray();
						
						String[] keys = {"메세지번호","학번","수신자","메세지제목","발신시각", "메세지본문"};
						
						try
						{
							while(rs.next())
							{
								Object[] values = {
										rs.getInt("메세지번호"),
										rs.getString("학번"),
										rs.getString("이름"),
										rs.getString("메세지제목"),
										rs.getDate("발신시각"),
										rs.getString("메세지본문")
								};
								mArr.add(Toolbox.createJSONProtocol(keys, values));
							}
						}
						catch(SQLException e)
						{
							e.printStackTrace();
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
					 else if(type.equals(NetworkProtocols.STUDENT_USER_INFO_REQUEST))
					 {
						 String query = "select S.학번, "
						 		+ "U.이름, "
						 		+ "S.주소, "
						 		+ "S.휴대폰번호, "
						 		+ "S.자택전화번호, "
						 		+ "S.주민등록번호, "
						 		+ "S.성별, "
						 		+ "S.방번호, "
						 		+ "S.프로필사진URL, "
						 		+ "S.학년, "
						 		+ "S.소속학과,"
						 		+ "Q.질문내용,"
						 		+ "S.비밀번호찾기_답변 "
						 		+ "from 학생 S, 사용자 U, 비밀번호찾기_질문목록 Q where S.학번=U.학번 and U.학번='"+userIdentify+"' and Q.질문번호=S.비밀번호찾기_질문;";
						 // 꺼내온 URL을 통해 서버 피시에 저장된 이미지 파일을 가져와 JSON에 같이 담아줌
						
						 
		    			ResultSet rs = handler.excuteQuery(query);
						 
		    			try
		    			{
		    				String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","이미지데이터","학년","소속학과","질문내용","비밀번호찾기_답변"};
		    				JSONObject respond = null;
		    				while(rs.next())
		    				{
		    					if(rs.getString("프로필사진URL")!=null)
		    					{
		    						byte[] iData = Files.readAllBytes(new File(rs.getString("프로필사진URL")).toPath());
		    						Object[] values = {
			    							rs.getString("학번"),
			    							rs.getString("이름"),
			    							rs.getString("주소"),
			    							rs.getString("휴대폰번호"),
			    							rs.getString("자택전화번호"),
			    							rs.getString("주민등록번호"),
			    							rs.getString("성별"),
			    							rs.getString("방번호"),
			    							iData,
			    							rs.getInt("학년"),
			    							rs.getString("소속학과"),
			    							rs.getString("질문내용"),
			    							rs.getString("비밀번호찾기_답변")
			    					};
			    					respond = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_USER_INFO_RESPOND, keys, values);
		    					}
		    					else
		    					{
		    						Object[] values = {
			    							rs.getString("학번"),
			    							rs.getString("이름"),
			    							rs.getString("주소"),
			    							rs.getString("휴대폰번호"),
			    							rs.getString("자택전화번호"),
			    							rs.getString("주민등록번호"),
			    							rs.getString("성별"),
			    							rs.getString("방번호"),
			    							"imageX",
			    							rs.getInt("학년"),
			    							rs.getString("소속학과"),
			    							rs.getString("질문내용"),
			    							rs.getString("비밀번호찾기_답변")
			    					};
			    					respond = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_USER_INFO_RESPOND, keys, values);
		    					}
		    					

		    				}
		    				
		    				String qry2 = "select sum(점수) AS 상벌점 from 상벌점부여목록 where 학번='"+userIdentify+"';";
		    				
		    				ResultSet rs2 = handler.excuteQuery(qry2);
		    				
		    				while(rs2.next())
		    				{
		    					respond.put("상벌점", rs2.getInt("상벌점"));
		    				}
		    				
		    				String qry3 = "select 질문내용 from 비밀번호찾기_질문목록;";
		    				ResultSet rs3 = handler.excuteQuery(qry3);
		    				JSONArray qList = new JSONArray();
		    				while(rs3.next())
		    				{
		    					qList.add(rs3.getString("질문내용"));
		    				}
	    					respond.put("질문목록", qList);	
	    					System.out.println(respond.toJSONString());
		    				sendProtocol(respond);
		    			}
		    			catch(SQLException e)
		    			{
		    				e.printStackTrace();
		    			}
					 }
					 else if(type.equals(NetworkProtocols.STUDENT_UPLOAD_PROFILE_IMAGE_REQUEST))
					 {
						 String requestFileFormat = request.get("fileName").toString().split("\\.")[1];
						 byte[] requestImageData = (byte[]) request.get("content");
						 
						 // 서버 로컬에 저장작업
						 String savePath = Statics.DEFAULT_USER_DATA_DIRECTORY+userIdentify+"_profilePhoto."+requestFileFormat;
						 String qry = "update 학생 set 프로필사진URL = '"+savePath+"' where 학번='"+userIdentify+"';";
						 
						 handler.excuteUpdate(qry);
						 System.out.println("쿼리실행 : "+qry);
						 Files.write(new File(savePath).toPath(), requestImageData);
						 
						 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_UPLOAD_PROFILE_IMAGE_RESPOND);
						 respond.put("Image-data", Files.readAllBytes(new File(savePath).toPath()));
						 sendProtocol(respond);
					 }
					 else if(type.equals(NetworkProtocols.STUDENT_MODIFY_USER_INFO_REQUEST))
					 {
						 String qry = "update 사용자 set 이름 = '"+request.get("이름")+"' where 학번 = '"+userIdentify+"';";
						 handler.excuteUpdate(qry);
						 
						 String qry2 = "update 학생 set 성별 = '"+request.get("성별")+"', 휴대폰번호 = '"+request.get("휴대폰번호")+"', 주민등록번호 = '"+request.get("주민등록번호")+"', 자택전화번호 = '"+request.get("자택전화번호")+"', 주소 = '"+request.get("주소")+"', 학년 = '"+request.get("학년")+"', 소속학과 = '"+request.get("소속학과")+"' where 학번 = '"+userIdentify+"';";
						 handler.excuteUpdate(qry2);
						 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_MODIFY_USER_INFO_RESPOND));
					 }
					 else if(type.equals(NetworkProtocols.STUDENT_REAUTH_REQUEST))
					 {
						 String qry = "select * from 사용자 where 학번 = '"+request.get("reqID")+"';";
						 ResultSet rs = handler.excuteQuery(qry);
						 
						 try
						 {
							if(rs.next())
							{
								JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_REAUTH_RESPOND);
								if(rs.getString("비밀번호").equals(request.get("reqPW")))
								{
									respond.put("accept", "Y");
								}
								else
								{
									respond.put("accept", "N");									
								}
								sendProtocol(respond);
							}
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
						 
					 }
					 else if(type.equals(NetworkProtocols.STUDENT_PASSWORD_SETUP_REQUEST))
					 {
						 String qry = "update 학생 set 비밀번호찾기_질문 = "+request.get("질문")+", 비밀번호찾기_답변 = '"+request.get("답변")+"' where 학번 = '"+userIdentify+"';";
						 handler.excuteUpdate(qry);
						 
						 if(request.get("새비밀번호").toString().length()!=0)
						 {
							 qry = "update 사용자 set 비밀번호 = '"+request.get("새비밀번호")+"' where 학번 = '"+userIdentify+"';";
							 handler.excuteUpdate(qry);
						 }
						 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_PASSWORD_SETUP_RESPOND));
					 }
					 else if(type.equals(NetworkProtocols.STUDENT_MEDIA_LIST_REQUEST))
					 {
						 String qry = "select 비디오번호, 비디오이름 from 비디오목록";
						 ResultSet rs = handler.excuteQuery(qry);
						 JSONArray data = new JSONArray();
						 String[] keys = {"비디오번호", "비디오이름"}; 
						 try
						 {
							 while(rs.next())
							 {
								 Object[] values = {rs.getInt("비디오번호"), rs.getString("비디오이름")};
								 data.add(Toolbox.createJSONProtocol(keys, values));
							 }
							 
							 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_MEDIA_LIST_RESPOND);
							 respond.put("data", data);
							 sendProtocol(respond);
						 }
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
						 
					 }
					 else if(type.equals(NetworkProtocols.STUDENT_MEDIA_CONTENT_REQUEST))
					 {
						 
						 String qry2 = "select 비디오이름, 비디오크기 from 비디오목록 where 비디오번호 = "+request.get("비디오번호")+";";
						 
						 ResultSet rs2 = handler.excuteQuery(qry2);
						 
						 rs2.next();
						 
						 JSONObject notification = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SEND_MEDIA_NOTIFICATION);
						 notification.put("name", rs2.getString("비디오이름"));
						 notification.put("size", rs2.getInt("비디오크기"));
						 sendProtocol(notification);
						 String qry = "select 비디오경로, 비디오크기 from 비디오목록 where 비디오번호 = "+request.get("비디오번호")+";";
						 
						 ResultSet rs = handler.excuteQuery(qry);
						 
						 try
						 {
							 rs.next();
							 String openPath = rs.getString("비디오경로");
							 
							 byte[] data = Files.readAllBytes(new File(openPath).toPath());
							 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_MEDIA_CONTENT_RESPOND);
							 respond.put("data", data);
							 respond.put("fileName", openPath.split("\\\\")[3]);
							 respond.put("format", openPath.split("\\.")[1]);
							 
							 fileServer.handleFileClient(respond, userName);
							 
						 }	
						 catch(SQLException e)
						 {
							 e.printStackTrace();
						 }
					 }
					 else if(type.equals(NetworkProtocols.MESSAGE_RICIEVER_SELECT_DELETE_REQUEST))
                     {
                    JSONObject json ;
                        
                    if(request.get("reqType").equals("R"))
                    {
                       json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RICIEVER_SELECT_DELETE_RESPOND);
                       json.put("resType", "R");
                    }
                    else
                    {
                       json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_SELECT_DELETE_RESPOND);
                       json.put("resType", "S");                        
                    }
                    
                    JSONArray jarray = (JSONArray)request.get("delete");
                        
                        for(Object a : jarray)
                        {
                           JSONObject aa = (JSONObject)a;
                           
                           String typemessage = aa.get("No").toString();
                           
                           String qry = "delete from 메세지 where 메세지번호 = '"+typemessage+"'";
                           
                           handler.excuteUpdate(qry);
                        }
                        
                        sendProtocol(json);
                        
                     }
					 else if(type.equals(NetworkProtocols.MESSAGE_RICIEVER_ALL_DELETE_REQUEST))
                     {
                        JSONObject json ;
                        String qry = "";
                        if(request.get("reqType").equals("R"))
                        {
                           json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_RICIEVER_ALL_DELETE_RESPOND);
                           qry = "delete from 메세지 where 수신자 = '"+userIdentify+"' and 구분='R';";
                           json.put("resType", "R");
                        }
                        else
                        {
                           json = Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_SEND_ALL_DELETE_RESPOND);
                           qry = "delete from 메세지 where 발신자 = '"+userIdentify+"' and 구분='S';";                            
                           json.put("resType", "S");
                        }
                        handler.excuteUpdate(qry);
                        
                        sendProtocol(json);
                     }
		                else if(type.equals(NetworkProtocols.LOGOUT_REQUEST))
		                {
		                	System.out.println("로그아웃");
		                	break;
		                }
		                else if(type.equals(NetworkProtocols.MESSAGE_REPLY_REQUEST))
		                {
		                	String qry = "select 학번, 이름 from 사용자 where 학번='"+request.get("reqID")+"';";
		                	
		                	try
		                	{
			                	ResultSet rs = handler.excuteQuery(qry);
			                	rs.next();
			                	
			                	String[] keys = {"학번","이름"};
			                	Object[] values = {rs.getString("학번"),rs.getString("이름")};
			                	
			                	sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.MESSAGE_REPLY_RESPOND, keys, values));
			                	
		                	}
		                	catch(SQLException e)
		                	{
		                		e.printStackTrace();
		                	}
		                }
		                else if(type.equals(NetworkProtocols.STUDENT_SORT_OVERLAP_REQUEST))
		                  {
		                      int classnum = (int)request.get("class")-1;
		                      int levelnum = (int)request.get("level")-1;
		                      String qry ="";
		                      if(classnum == 0 && levelnum == 0)
		                      {
		                         qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 order by S.방번호";
		                      }
		                      else if(classnum == 0)
		                      {
		                         qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.층="+"'"+levelnum+"'order by S.방번호";
		                      }
		                      else if(levelnum == 0)
		                      {
		                         qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.소속학과="+"'"+classnum+"'order by S.방번호";
		                      }
		                      else
		                      {
		                         qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.층="+"'"+levelnum+"' and"+" S.소속학과 = '"+classnum+"' order by S.방번호";
		                      }
		                      ResultSet rs = handler.excuteQuery(qry);
		                      
		                      JSONArray mArr = new JSONArray();
		                      
		                          String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
		                          while(rs.next())
		                          {
		                             Object[] values = {
		                                  rs.getString("학번"),
		                                  rs.getString("이름"),
		                                  rs.getString("주소"),
		                                  rs.getString("휴대폰번호"),
		                                  rs.getString("자택전화번호"),
		                                  rs.getString("주민등록번호"),
		                                  rs.getString("성별"),
		                                  rs.getString("방번호"),
		                                  rs.getInt("학년"),
		                                  rs.getString("학과이름")
		                                  };
		                                 mArr.add(Toolbox.createJSONProtocol(keys, values));
		                              }
		                         
		                              // 새로운 제이슨에 프로토콜 넣음
		                              JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SORT_OVERLAP_RESPOND);
		                              // 제이슨 어레이 넣음 
		                              res.put("user_list", mArr);
		                              sendProtocol(res);   
		                   }
		                else if(type.equals(NetworkProtocols.MEMBER_JOIN_REQUEST))
		                   {
		                      System.out.println("Member Join Info : "+request.toJSONString());
		                      
		                      String qry1 = "insert into 사용자 values('"+request.get("학번").toString()+"','"+request.get("이름").toString()+"','"
			                            +request.get("비밀번호").toString()+"', '학생');";
			                      
			                  handler.excuteUpdate(qry1);
		                      
		                      String qry = "insert into 학생 values('"+request.get("학번").toString()+"',"+(int)request.get("질문")+",'"+request.get("답변").toString()+"','"+request.get("주소").toString()+"','"
		                            +request.get("핸드폰번호").toString()+"','"+request.get("자택전화번호").toString()+"','"+request.get("주민등록번호").toString()+"','"+request.get("성별").toString()+"','"
		                            +request.get("방번호").toString()+"',NULL,"+(int)request.get("학년")+","+(int)request.get("소속학과")+",'"+request.get("방번호").toString()+"');";
		                      
		                      System.out.println(qry);
		                      handler.excuteUpdate(qry);
		                      
		                      JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MEMBER_JOIN_RESPOND);
		                      
		                      sendProtocol(json);
		                   }
		                else if(type.equals(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_REQUEST))
						 {
							 String check = request.get("comboCheck").toString();
							 if(check.equals("1"))
							 {
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 try
				                 {
				                	 while(rs.next())
					                 {
					                    Object[] values = {
					                         rs.getString("학번"),
					                         rs.getString("이름"),
					                         rs.getString("주소"),
					                         rs.getString("휴대폰번호"),
					                         rs.getString("자택전화번호"),
					                         rs.getString("주민등록번호"),
					                         rs.getString("성별"),
					                         rs.getString("방번호"),
					                         rs.getInt("학년"),
					                         rs.getString("학과이름")
					                         };
					                        mArr.add(Toolbox.createJSONProtocol(keys, values));
					                     }
				                 }
				                 catch(SQLException e)
				                 {
				                	 e.printStackTrace();
				                 }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);	
							 }
							 else if(check.equals("2"))
							 {	
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.소속학과=1 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);			 
							 }
							 else if(check.equals("3"))
							 {	
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.소속학과=2 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);			 
							 }
							 else if(check.equals("4"))
							 {	
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.소속학과=3 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);			 
							 }
							 else if(check.equals("5"))
							 {	

								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.소속학과=4 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);			 
							 }
							 else if(check.equals("6"))
							 {	
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.소속학과=5 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);			 
							 }
							 else
							 {
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.소속학과=6 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);			 
							 }
						 }
						 else if(type.equals(NetworkProtocols.STUDENT_LEVEL_SELECT_COMBOBOX_REQUEST))
						 {
							 String check = request.get("comboCheck").toString();
							 
							 
							 if(check.equals("1"))
							 {
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);	
							 }
							 else if(check.equals("2"))
							 {
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.층=1 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);			 
							 }
							 else if(check.equals("3"))
							 {
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.층=2 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);	
							 }
							 else if(check.equals("4"))
							 {
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.층=3 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);	
							 }
							 else
							 {
								 String qry = "select S.학번, A.이름 ,S.주소,S.휴대폰번호, S.자택전화번호,S.주민등록번호 ,S.성별 ,S.방번호 ,S.학년 ,M.학과이름  from 학생 S, 사용자 A, 학과_목록 M  where S.소속학과 = M.학과번호 and S.학번 = A.학번 and S.층=4 order by S.방번호";
								 
								 ResultSet rs = handler.excuteQuery(qry);
								 
				                 JSONArray mArr = new JSONArray();
				                     
				                 String[] keys = {"학번","이름","주소","휴대폰번호","자택전화번호","주민등록번호","성별","방번호","학년","소속학과"};
				                 while(rs.next())
				                 {
				                    Object[] values = {
				                         rs.getString("학번"),
				                         rs.getString("이름"),
				                         rs.getString("주소"),
				                         rs.getString("휴대폰번호"),
				                         rs.getString("자택전화번호"),
				                         rs.getString("주민등록번호"),
				                         rs.getString("성별"),
				                         rs.getString("방번호"),
				                         rs.getInt("학년"),
				                         rs.getString("학과이름")
				                         };
				                        mArr.add(Toolbox.createJSONProtocol(keys, values));
				                     }
				                
				                     // 새로운 제이슨에 프로토콜 넣음
				                     JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CLASS_SELECT_COMBOBOX_RESPOND);
				                     // 제이슨 어레이 넣음 
				                     res.put("user_list", mArr);
				                     sendProtocol(res);	
							 }
						 }
						 else if(type.equals(NetworkProtocols.ADMIN_CATEGORY_DELETE_REQUEST))
						 {
							 String qry = "select 카테고리이름 from 게시글_카테고리목록;";
							 ResultSet rs = handler.excuteQuery(qry);
							 JSONArray data = new JSONArray();
							 while(rs.next())
							 {
								 data.add(rs.getString("카테고리이름"));
							 }
							 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_CATEGORY_DELETE_RESPOND);
							 respond.put("category-list", data);
							 sendProtocol(respond);
						 }
						 else if(type.equals(NetworkProtocols.ADMIN_DELETE_CATEGORY_COUNT_REQUEST))
						 {
							 String qry = "select count(*) from 게시글 where 카테고리="+request.get("category")+";";
							 ResultSet rs = handler.excuteQuery(qry);
							 try
							 {
								 rs.next();
								 
								 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_DELETE_CATEGORY_COUNT_RESPOND);
								 respond.put("count", rs.getInt("count(*)"));
								 respond.put("category", request.get("category"));
								 sendProtocol(respond);
							 }
							 catch(SQLException e)
							 {
								 e.printStackTrace();
							 }
							 
						 }
						 else if(type.equals(NetworkProtocols.ADMIN_DELETE_FINAL_REQUEST))
						 {
							 String qry = "delete from 게시글 where 카테고리 = "+request.get("category")+";";
							 System.out.println();
							 handler.excuteUpdate(qry);
							 
							 qry = "delete from 게시글_카테고리목록 where 카테고리번호 = "+request.get("category")+";";
							 handler.excuteUpdate(qry);
							 
							 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_DELETE_FINAL_RESPOND));
						 }
						 else if(type.equals(NetworkProtocols.STUDENT_CATEGORY_LIST_REQUEST))
						 {
							 try
							 {
								 ResultSet rs = handler.excuteQuery("select 카테고리이름 from 게시글_카테고리목록");
								 JSONArray data = new JSONArray();
								 
								 while(rs.next())
								 {
									 data.add(rs.getString("카테고리이름"));
								 }
								 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_CATEGORY_LIST_RESPOND);
								 respond.put("category-list", data);
								 sendProtocol(respond);
							 }
							 catch(SQLException e)
							 {
								 e.printStackTrace();
							 }
						 }
						 else if(type.equals(NetworkProtocols.STUDENT_BOARD_SEARCH_REQUEST))
						 {
							 String qType = request.get("검색조건").toString();
							 String qWord = request.get("검색어").toString();
							 
							 String qry = "";
							 
							 if(qType.equals("작성자"))
							 {
								  qry = "select G.게시글번호, S.이름, G.게시글제목, G.작성일자, G.게시글본문, M.카테고리이름 from 사용자 S, 게시글 G, 게시글_카테고리목록 M "
				                         + "where S.학번=G.작성자 and M.카테고리번호=G.카테고리 and G.작성자 = (select 학번 from 사용자 where 이름 = '"+qWord+"');";
							 }
							 else if(qType.equals("제목"))
							 {
								 qry = "select G.게시글번호, S.이름, G.게시글제목, G.작성일자, G.게시글본문, M.카테고리이름 from 사용자 S, 게시글 G, 게시글_카테고리목록 M "
				                         + "where S.학번=G.작성자 and M.카테고리번호=G.카테고리 and G.게시글제목 like '%"+qWord+"%';";
							 }
							 else if(qType.equals("내용"))
							 {
								 qry = "select G.게시글번호, S.이름, G.게시글제목, G.작성일자, G.게시글본문, M.카테고리이름 from 사용자 S, 게시글 G, 게시글_카테고리목록 M "
				                         + "where S.학번=G.작성자 and M.카테고리번호=G.카테고리 and G.게시글본문 like '%"+qWord+"%';";
							 }
							 System.out.println(qry);
							ResultSet rs = handler.excuteQuery(qry);
							
							if(Toolbox.getResultSetSize(rs)==0)
							{
								sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.BOARD_NO_SEARCH_RESULT));
							}
							else
							{
								JSONArray arr = new JSONArray();
								JSONArray arr2 = new JSONArray();
								
								String[] keys = {"No","이름", "게시글제목", "작성일자", "게시글본문", "카테고리"};
								try
								{
									while(rs.next())
									{
										Object[] o = {rs.getInt("게시글번호"),
													  rs.getString("이름"),
												      rs.getString("게시글제목"),
												      rs.getDate("작성일자"),
												      rs.getString("게시글본문"),
												      rs.getString("카테고리이름")}; 
										
										JSONObject n = Toolbox.createJSONProtocol(keys, o);
										arr.add(n);								
									}
								}
								catch(SQLException e)
								{
									e.printStackTrace();
								}
								
								String qry2 = "select 카테고리이름 from 게시글_카테고리목록;";
								
								ResultSet rs2 = handler.excuteQuery(qry2);
								
								try
								{
									while(rs2.next())
									{
										arr2.add(rs2.getString(1));
									}
								}
								catch(SQLException e)
								{
									e.printStackTrace();
								}
								
								JSONObject res = Toolbox.createJSONProtocol(NetworkProtocols.BOARD_LIST_RESPOND);
								res.put("board_list", arr);
								res.put("category_list", arr2);
								sendProtocol(res);
								System.out.println("응답");
							}
						 }
						 else if(type.equals(NetworkProtocols.ADMIN_SUBMIN_MAIN_REQUEST))
						 {
							 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIN_MAIN_RESPOND);
							 
							 // 진행중인 서류가 없는 경우
							 if(Toolbox.getResultSetSize(handler.excuteQuery("select * from 제출서류목록"))==0)
							 {
								 respond.put("submit-process", "no-data");
							 }
							 // 있는경우
							 else
							 {
								 respond.put("submit-process", "exist-data");
								 
								 ResultSet rs = handler.excuteQuery("select 제출분류명, 마감시간 from 제출서류목록;");
								 rs.next();
								 respond.put("제출분류명", rs.getString("제출분류명"));
								 respond.put("마감시간", rs.getDate("마감시간"));
								 
								 ResultSet rs2 = handler.excuteQuery("select M.서류번호,M.제출자,S.이름,M.제출서류저장URL,M.제출시간 from 제출서류함 M, 사용자 S where M.제출자=S.학번;");					 
								 JSONArray data = new JSONArray();
								 try
								 {
									 while(rs2.next())
									 {
										 JSONObject rawData = null;
										 String[] keys = {"서류번호","학번","이름","제출여부","제출시간"};
										 
										 if(rs2.getString("제출서류저장URL")==null)
										 {
											 Object[] values = {rs2.getInt("서류번호"),
													 			rs2.getString("제출자"),
													 			rs2.getString("이름"),
													 			"미제출",
													 			"-------"};
											 rawData = Toolbox.createJSONProtocol(keys,values);
										 }
										 else
										 {
											 Object[] values = {rs2.getInt("서류번호"),
													 			rs2.getString("제출자"),
											 					rs2.getString("이름"),
											 					"제출",
											 					rs2.getTimestamp("제출시간")};
											 rawData = Toolbox.createJSONProtocol(keys,values);
										 }
										 data.add(rawData);
									 }
								 }
								 catch(SQLException e)
								 {
									 e.printStackTrace();
								 }
								 respond.put("data-bundle", data);
								 System.out.println(respond.toJSONString());
							 }
							 
							 sendProtocol(respond);
						 }
						 else if(type.equals(NetworkProtocols.ADMIN_SUBMIT_ENROLL_REQUEST))
						 {
							 JSONObject data = (JSONObject)request.get("data");
							 String title = data.get("title").toString();
							 Date date = (Date)data.get("date");
							 
							 SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							 String dateString = d.format(date);
							 
							 String qry = "insert into 제출서류목록(분류번호,제출분류명,마감시간) values(1,'"+title+"','"+dateString+"');";
							 handler.excuteUpdate(qry);
							 
							 ResultSet rs = handler.excuteQuery("select 학번 from 학생;");
							 int count = 0;
							 while(rs.next())
							 {
								 handler.excuteUpdate("insert into 제출서류함(서류번호,제출자,분류) values("+count+",'"+rs.getString("학번")+"',1);");
								 count++;
							 }
							 
							 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIT_ENROLL_RESPOND));
						 }
						 else if(type.equals(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_ASK_REQEUST))
						 {
							 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_ASK_RESPOND);
							 if(Toolbox.getResultSetSize(handler.excuteQuery("select * from 제출서류함 where 제출서류저장URL is NULL;"))!=0)
							 {
								 respond.put("result", "re-ask");
							 }
							 else
							 {
								 respond.put("result", "ok");
							 }
							 sendProtocol(respond);
						 }
						 else if(type.equals(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_REQEUST))
						 {
							 handler.excuteUpdate("delete from 제출서류함;");
							 handler.excuteUpdate("delete from 제출서류목록;");
							 // C:\DIMS\SubmittedData 폴더 파일들 다 지워함
							 
							 for(File target : new File(Statics.DEFAULT_SUBMITTED_DATA_DIRECTORY).listFiles())
							 {
								 target.delete();
							 }
							 
							 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_SUBMIT_DISPOSE_RESPOND));
						 }
						 else if(type.equals(NetworkProtocols.STUDENT_SUBMIT_REQUEST))
						 {
							 String filename = Statics.DEFAULT_SUBMITTED_DATA_DIRECTORY+userIdentify+"_submitted."+request.get("확장자");
							 byte[] data = (byte[])request.get("데이터");
							 ResultSet rs = handler.excuteQuery("select 제출서류저장URL from 제출서류함 where 제출자 = '"+userIdentify+"';");
							 rs.next();
							 if(rs.getString("제출서류저장URL")!=null)
							 {
								 File deleteTarget = new File(rs.getString("제출서류저장URL"));
								 deleteTarget.delete();
							 }
							 
							 Files.write(new File(filename).toPath(), data);
							 
							 handler.excuteUpdate("update 제출서류함 set 제출서류저장URL = '"+filename+"' where 제출자 = '"+userIdentify+"';");
							 handler.excuteUpdate("update 제출서류함 set 제출시간 = now() where 제출자 = '"+userIdentify+"';");							 
							 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_SUBMIT_RESPOND));
						 }
						 else if(type.equals(NetworkProtocols.ADMIN_LOCAL_SAVE_REQUEST))
						 {
							 new Thread(()->{
								 try
								 {
									 ByteArrayOutputStream bos = new ByteArrayOutputStream();
									 String zipFileName = Statics.DEFAULT_SUBMITTED_DATA_DIRECTORY+"submitted_data.zip";
									 ZipOutputStream zos = new ZipOutputStream(bos);
									 File[] fList = new File(Statics.DEFAULT_SUBMITTED_DATA_DIRECTORY).listFiles();
									 for(int i=0;i<fList.length;i++)
									 {
										 ZipEntry zipHandle = new ZipEntry(fList[i].toPath().getFileName().toString());
										 zos.putNextEntry(zipHandle);
										 System.out.println("압축중...."+(i+1)+"/"+(fList.length));
										 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_LOCAL_SAVE_RESPOND);
										 respond.put("progress-current", (i+1));
										 respond.put("progress-max", fList.length);
										 sendProtocol(respond);
										 zos.write(Files.readAllBytes(fList[i].toPath()));
									 }
									 bos.close();
									 zos.close();
									 
									 byte[] sendable = bos.toByteArray();
									 
									 JSONObject notification = Toolbox.createJSONProtocol(NetworkProtocols.ADMIN_LOCAL_SAVE_NOTIFICATION);
									 notification.put("name", zipFileName);
									 notification.put("size", sendable.length);
									 sendProtocol(notification);
									 
									 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.STUDENT_MEDIA_CONTENT_RESPOND);
									 respond.put("data", sendable);
									 respond.put("fileName", zipFileName.split("\\\\")[3]);
									 respond.put("format", zipFileName.split("\\.")[1]);
									 
									 fileServer.handleFileClient(respond, userName);
								 }
								 catch(IOException e)
								 {
									 e.printStackTrace();
								 }
							 }).start();
						 }
						 else if(type.equals(NetworkProtocols.EMAIL_SEND_REQUEST))
						 {
							 JSONObject mailData = request;
							 System.out.println(mailData.toJSONString());
							 
							 boolean constraint = true;
							 
							 ArrayList<File> attached = new ArrayList<File>();
							 for(File target : new File(Statics.DEFAULT_SUBMITTED_DATA_DIRECTORY).listFiles())
							 {
								 if(Files.readAllBytes(target.toPath()).length>1024*1024*10)
								 {
									 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.EMAIL_FILE_SIZE_LIMIT));
									 constraint = false;
									 break;
								 }
								 attached.add(target);
							 }
							 
							 if(attached.size()>10)
							 {
								 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.EMAIL_MAX_COUNT_EXCEED));
								 constraint = false;
							 }
							 
							 if(constraint)
							 {
								 new Thread(()->{
									 
									 try
									 {
										 File[] files = new File[attached.size()];
										 files = attached.toArray(files);
										 
										 MailProperties m = MailProperties.createNaverMailProperty()
							  					 .addProperty(MailProperty.PORT, 465)
												 .addProperty(MailProperty.TITLE, mailData.get("메일제목"))
												 .addProperty(MailProperty.CONTENT, mailData.get("메일본문"))
												 .addProperty(MailProperty.ATTACHED_FILE, files);
										 System.out.println("전송중...");
										 MailingService.sendMail(m, mailData.get("받을사람").toString());
										 System.out.println("전송완료");									 
										 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.EMAIL_SEND_RESPOND));
									 }
									 catch(IOException e)
									 {
										 e.printStackTrace();
									 }
									 
								 }).start();
							 }
							 
							 
							 
							 
							 
							 
						 }
						 else if(type.equals(NetworkProtocols.PASSWORD_FIND_IDENTIFY_REQUEST))
						 {
							 ResultSet rs = handler.excuteQuery("select 비밀번호찾기_질문 as 질, 비밀번호찾기_답변 as 답 from 학생 where 학번 = '"+request.get("학번")+"';");
				
							 JSONObject respond = Toolbox.createJSONProtocol(NetworkProtocols.PASSWORD_FIND_IDENTIFY_RESPOND); 
							 
							 if(Toolbox.getResultSetSize(rs)==0)
							 {
								 // 학번이 잘못된 경우
								 respond.put("identify-result", "fault");
							 }
							 else
							 {
								 // 질, 답  = 질문, 답변
								 rs.next();
								 if(rs.getInt("질")==(int)request.get("질문"))
								 {
									 if(rs.getString("답").equals(request.get("답변").toString()))
									 {
										 respond.put("identify-result", "commit");
									 }
									 else
									 {
										 respond.put("identify-result", "fault");								 										 
									 }
								 }
								 else
								 {
									 respond.put("identify-result", "fault");								 
								 }
							 }
							 sendProtocol(respond);
						 }
						 else if(type.equals(NetworkProtocols.PASSWORD_FIND_MODIFY_REQUEST))
						 {
							 handler.excuteUpdate("update 사용자 set 비밀번호 = '"+request.get("request-pw")+"' where 학번 = '"+request.get("request-id")+"';");
							 sendProtocol(Toolbox.createJSONProtocol(NetworkProtocols.PASSWORD_FIND_MODIFY_RESPOND));
						 }
		                else
						{
		                	if(PRINT_LOG)System.out.println("\t\t\t잘못된 요청");
						}
						System.out.println("응답 완료");
				}
			}
			catch (IOException|SQLException e)
			{
				/* 사용자가 종료한거임 */
				e.printStackTrace();
				if(PRINT_LOG)System.out.println("\t\t[Connector-"+userName+"] 사용자 접속 종료, 스레드 종료");
			}
			finally
			{
				try
				{
					fromClient.close();
					toClient.close();

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				System.out.println("클라이언트 스레드 종료");
			}
			
		}
		
		public void sendProtocol(JSONObject protocol) throws IOException
		{
			synchronized (toClient) {
				toClient.writeObject(protocol);
				toClient.flush();	
			}
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