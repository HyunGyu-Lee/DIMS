package com.hst.dims.files;

import com.hst.dims.databases.DatabaseHandler;
import com.hst.dims.tools.Statics;
import com.hst.dims.tools.Toolbox;
import com.orsoncharts.util.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class DIMSFileServer {

	private ServerSocket fileSender;
	private boolean isTaskFinish = true;

	public DIMSFileServer(int port)
	{
		try
		{
			fileSender = new ServerSocket(port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void handleFileClient(JSONObject sendData, String userName)
	{
		new Thread(()->{

			Socket c = null;
			try
			{
				System.out.println("파일 요청 감시, 클라이언트 접속 대기중");
				c = fileSender.accept();
				System.out.println("클라이언트 접속, 전송 스레드 시작");
				System.out.println(userName+"에게 "+sendData.toJSONString());
				new Connection(c, sendData).transmitFile();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	public void handleUploadClient(JSONObject info, DatabaseHandler handler)
	{
		byte[] recieve = new byte[(int)info.get("파일크기")];
		new Thread(()->{

			Socket c = null;
			try
			{
				System.out.println("파일 요청 감시, 클라이언트 접속 대기중");
				c = fileSender.accept();
				System.out.println("클라이언트 접속, 전송 스레드 시작");

				ObjectInputStream is = new ObjectInputStream(c.getInputStream());

				for(int i=0;i<recieve.length;i++)
				{
					recieve[i] = (byte) is.read();
				}

				int vCnt = Toolbox.getResultSetSize(handler.executeQuery("select * from 비디오목록"));

				String savePath = Statics.DEFAULT_MOVIE_DATA_DIRECTORY+info.get("파일이름");

				handler.excuteUpdate("insert into 비디오목록 values("+(vCnt+1)+",'"+info.get("이름")+"','"+savePath+"',"+recieve.length+");");
				Files.write(new File(savePath).toPath(), recieve);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	class Connection
	{
		ObjectInputStream ois;
		ObjectOutputStream oos;
		Socket target;
		JSONObject sendData;
		public Connection(Socket target, JSONObject sendData)
		{
			this.target = target;
			this.sendData = sendData;
		}

		@SuppressWarnings("unchecked")
		public void transmitFile()
		{
			System.out.println("파일 클라이언트 접속");
			try
			{
				oos = new ObjectOutputStream(target.getOutputStream());

				ois = new ObjectInputStream(target.getInputStream());

				send(Toolbox.createJSONProtocol(FileProtocol.CONFIRM_READY));

				while(true)
				{
					JSONObject pack = (JSONObject)ois.readObject();

					if(pack==null)break;

					String type = pack.get("type").toString();

					if(type.equals(FileProtocol.READY_OK))
					{
						JSONObject protocol = Toolbox.createJSONProtocol(FileProtocol.DATA_INFO);

						protocol.put("fileName", sendData.get("fileName"));
						protocol.put("format", sendData.get("format"));

						send(protocol);
					}
					else if(type.equals(FileProtocol.REQUEST_CONTENT))
					{
						JSONObject protocol = Toolbox.createJSONProtocol(FileProtocol.CONTENT);
						send(protocol);

						byte[] sData = (byte[])sendData.get("data");

						for(int i=0;i<sData.length;i++)
						{
							oos.writeByte(sData[i]);
						}
						oos.flush();

					}
					else if(type.equals(FileProtocol.BREAK_REQUEST))
					{
						send(Toolbox.createJSONProtocol(FileProtocol.BREAK_RESPOND));
					}

				}
			}
			catch(IOException|ClassNotFoundException e)
			{
				System.out.println("전송 작업 종료");
				try {
					ois.close();
					oos.close();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}

			}
			finally
			{
				try {
					System.out.println("파일 클라이언트와 연결종료");
					ois.close();
					oos.close();
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



}