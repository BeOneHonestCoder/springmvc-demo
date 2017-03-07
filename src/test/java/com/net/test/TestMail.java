package com.net.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.net.mail.EmailRequest;
import com.net.mail.EmailService;

public class TestMail extends AbstractContextTest {
	
	@Autowired
	@Qualifier("emailServiceImpl")
	protected EmailService emailServiceImpl;
	
	@Test
	public void testHello(){
			//impumgjrbfhtjbbg
		 	EmailRequest emailRequest = new EmailRequest();
		 	emailRequest.setFromAddress("Distribution <noreturn@sina.net>");
		 	emailRequest.setToAddress("haijian_jiang@sina.com");
		 	emailRequest.setEmailSubject("Hello, Hi");
		 	emailRequest.setEmailContent("Hello, Hi");
		 	emailRequest.setUserId("Hello");
		 	
		 	emailServiceImpl.sendEmail(emailRequest);
	}

}
