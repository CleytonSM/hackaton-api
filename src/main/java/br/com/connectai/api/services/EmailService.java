package br.com.connectai.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String recipient, String code) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setSubject("Código de Acesso do Connectai");
        email.setText("Aqui está seu código: "+ code);
        email.setFrom("cleytonsm1309@gmail.com");
        mailSender.send(email);
    }
}
