package com.csl.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // --- RECUPERACIÓN DE CONTRASEÑA ---
    public void enviarCorreoRecuperacion(String destino, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("controlsystemlogistic@gmail.com");
        message.setTo(destino);
        message.setSubject("Recuperar Contraseña (LOCAL)");
        
        // Apunta al Frontend Local
        String urlFrontend = "http://localhost:5500/reset_password.html?token=" + token;
        
        String cuerpo = "Hola,\n\nPara restablecer tu contraseña en local, haz clic aquí:\n" + urlFrontend;
        
        message.setText(cuerpo);
        mailSender.send(message);
    }

    // --- CONFIRMACIÓN DE REGISTRO ---
    public void enviarCorreoRegistro(String destino, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("controlsystemlogistic@gmail.com");
        message.setTo(destino);
        message.setSubject("Confirma tu cuenta (LOCAL)");

        // Apunta al Backend Local
        String urlConfirmacion = "http://localhost:8080/api/auth/confirm-account?token=" + token;

        String cuerpo = "Hola,\n\nConfirma tu registro en local haciendo clic aquí:\n" + urlConfirmacion;

        message.setText(cuerpo);
        mailSender.send(message);
    }

    public void enviarPresupuesto(String destino, String transporte, String peso, String precio) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("controlsystemlogistic@gmail.com");
        message.setTo(destino);
        message.setSubject("Presupuesto (LOCAL)");
        message.setText("Presupuesto: " + precio + "\nEntra en http://localhost:5500 para verlo.");
        mailSender.send(message);
    }
}