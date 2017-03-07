package com.net.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.net.util.LogUtil;

@Component("emailServiceImpl")
public class EmailServiceImpl implements EmailService {
	
	private static Logger logger = LogUtil.getLogger();
	
	private static final String MAIL_HOST = "mail.smtp.host";

	@Value("${email.smtp.host}")
	private transient String mailHost;
	
	/**
	 *  Method used to send Email
	 * 
	 * @param emailRequest
	 */
	public void sendEmail(final EmailRequest emailRequest) {

		final Properties props = new Properties();
		props.put(MAIL_HOST, mailHost);
		final Session mailSession = Session.getDefaultInstance(props);
		final Message simpleMessage = new MimeMessage(mailSession);
		final Multipart multipart = new MimeMultipart();
		final MimeBodyPart htmlPart = new MimeBodyPart();
		InternetAddress fromAddress = null;
		InternetAddress[] toAddress = null;

		try {
			fromAddress = new InternetAddress(emailRequest.getFromAddress());
			toAddress = InternetAddress.parse(emailRequest.getToAddress());
		} catch (AddressException e) {
			logger.error("Email notificaiton failed  - " + e.getMessage());
		}
		try {
			multipart.addBodyPart(htmlPart);
			htmlPart.setContent(emailRequest.getEmailContent(), "text/html");
			simpleMessage.setFrom(fromAddress);
			simpleMessage.setRecipients(RecipientType.TO, toAddress);
			simpleMessage.setSubject(emailRequest.getEmailSubject());
			simpleMessage.setContent(multipart);
			Transport.send(simpleMessage);

		} catch (MessagingException e) {
			logger.error("Email notificaiton failed - " + e.getMessage(), e);
		}
	}

}
