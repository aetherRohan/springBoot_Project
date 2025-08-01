package com.smart.smartcontactmanager.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("rajrohanapple786123@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        this.javaMailSender.send(message);

        System.out.println("~~~~MAIL SENT~~~~");
    }
}
