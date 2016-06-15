package tools;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailingService {

	public static final String HOST_NAME_NAVER = "smtp.naver.com";
	public static final String HOST_NAME_GOOGLE = "smtp.gmail.com";
	
	private static String SENDER_ID;
	private static String SENDER_PW;
	
	public static void sendMail(MailProperties m, String reciever)
	{
		Properties props = setProperties(m);

		if(m.getHost().equals(HOST_NAME_NAVER))
		{
			SENDER_ID = "gusrb0808@naver.com";
			SENDER_PW = "zxc798";
		}
		else if(m.getHost().equals(HOST_NAME_GOOGLE))
		{
			SENDER_ID = "dlgusrb0808@gmail.com";
			SENDER_PW = "cjsrn1992";
		}
		
		Session session = getSession(props, SENDER_ID, SENDER_PW);
        session.setDebug(true);
        Message msg = new MimeMessage(session);
        try
        {
			try
			{
				msg.setFrom(new InternetAddress(new String("DIMS메일서비스".getBytes("KSC5601"),"8859_1")+"<"+SENDER_ID+">"));
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			//msg.setFrom(new InternetAddress("DIMS메일서비스 <"+SENDER_ID+">"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(reciever));
            msg.setSubject(m.getTitle());
			msg.setSentDate(new Date());
			
			MimeBodyPart body1 = new MimeBodyPart();
			if(m.getHost()==HOST_NAME_NAVER)
			{
				body1.setText(m.getContent());
			}
			else
			{
				try
				{
					body1.setText(new String(m.getContent().getBytes("KSC5601"),"8859_1"));
				} catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
			}

			Multipart multipart = new MimeMultipart();     
		    multipart.addBodyPart(body1);
			
			for(File target : m.getAttachedFiles())
			{
				MimeBodyPart body2 = new MimeBodyPart();
				DataSource source = new FileDataSource(target);
		        body2.setDataHandler(new DataHandler(source));
		        try
		        {
					body2.setFileName(new String(target.getName().getBytes("KSC5601"),"8859_1"));
				}
		        catch (UnsupportedEncodingException e)
		        {
					e.printStackTrace();
				}
		        multipart.addBodyPart(body2);
			}
			
		    
		    msg.setContent(multipart);
			
		    Transport.send(msg);
		    
		}
        catch (MessagingException e)
        {
			e.printStackTrace();
		}
        
	}
	
	private static Properties setProperties(MailProperties m)
	{
		Properties props = System.getProperties();
        props.put("mail.smtp.host", m.getHost());
        props.put("mail.smtp.port", m.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", m.getHost());
        return props;
	}
	
	private static Session getSession(Properties props, String id, String pw)
	{
		Session session = Session.getDefaultInstance(props, new Authenticator() {
			 
        	protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(id, pw);
            }
        });
		return session;
	}
	
}
