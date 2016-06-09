package files;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

import com.orsoncharts.util.json.JSONObject;

import tools.Toolbox;

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
				System.out.println("���� ��û ����, Ŭ���̾�Ʈ ���� �����");
				c = fileSender.accept();
				System.out.println("Ŭ���̾�Ʈ ����, ���� ������ ����");
				System.out.println(userName+"���� "+sendData.toJSONString());
				new Connection(c, sendData).transmitFile();
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
			System.out.println("���� Ŭ���̾�Ʈ ����");
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
				System.out.println("���� �۾� ����");
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
					System.out.println("���� Ŭ���̾�Ʈ�� ��������");
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
