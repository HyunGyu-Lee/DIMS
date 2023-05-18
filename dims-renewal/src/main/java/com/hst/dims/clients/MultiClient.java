package com.hst.dims.clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MultiClient {

	private MulticastSocket client;
	private String hostName;
	private int port;
	public MultiClient(String hostName, String ip, int port)
	{
		try
		{
			this.hostName = hostName;
			this.port = port;
			client = new MulticastSocket(port);
			client.joinGroup(InetAddress.getByName(ip));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message)
	{
		DatagramPacket p = new DatagramPacket(message.getBytes(), 0, message.getBytes().length, client.getInetAddress(), port);
		try
		{
			client.send(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class Reciever extends Thread
	{
		@Override
		public void run() {
			byte[] data = new byte[1024*1024];
			
			while(true)
			{
				try
				{
					client.receive(new DatagramPacket(data, data.length));
					System.out.println("����::"+hostName+" >> "+new String(data));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
		}
	}
	
}
