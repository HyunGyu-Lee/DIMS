package com.hst.dims.tools;

import java.io.File;

public class MailProperties {

	private String host;
	private int port;
	private String title;
	private String content;
	private File[] attachedFiles;
	
	public void setHost(String host)
	{
		this.host = host;
	}
	
	public static MailProperties createEmptyMailProperty()
	{
		MailProperties p = new MailProperties();
		return p;
	}
	
	public static MailProperties createNaverMailProperty()
	{
		MailProperties p = new MailProperties();
		p.setHost("smtp.naver.com");
		return p;
	}
	
	public static MailProperties createGmailProperty()
	{
		MailProperties p = new MailProperties();
		p.setHost("smtp.gmail.com");
		return p;
	}
	
	public MailProperties addProperty(MailProperty key, Object value)
	{
		if(key.equals(MailProperty.PORT))
		{
			this.port = (int)value;
		}
		else if(key.equals(MailProperty.HOST))
		{
			this.host = value.toString();
		}
		else if(key.equals(MailProperty.TITLE))
		{
			this.title = value.toString();
		}
		else if(key.equals(MailProperty.CONTENT))
		{
			this.content = value.toString();
		}
		else if(key.equals(MailProperty.ATTACHED_FILE))
		{
			this.attachedFiles = (File[]) value;
		}
		
		return this;
	}

	public String getHost()
	{
		return host;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public File[] getAttachedFiles()
	{
		return attachedFiles;
	}
	
}
