package com.engeman.technical_test_engeman.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.engeman.technical_test_engeman.emailService.dto.EmailServiceSMTPdto;



@Service
public class EmailServiceSMTP {

	private final JavaMailSender mailSender;
	
	public EmailServiceSMTP(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void SendSimpleMessage(EmailServiceSMTPdto emailInfos) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(emailInfos.to());
		message.setSubject(emailInfos.subject());
		message.setText(emailInfos.text());
		
		mailSender.send(message);
	}
}
