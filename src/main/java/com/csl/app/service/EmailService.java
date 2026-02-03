package com.csl.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // --- RECUPERACIÓN DE CONTRASEÑA (El de siempre) ---
    public void enviarCorreoRecuperacion(String destino, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("controlsystemlogistic@gmail.com");
        message.setTo(destino);
        message.setSubject("Recuperar Contraseña (LOCAL)");
        
        // Apunta al reset_password.html (NO LO TOCAMOS)
        // ANTES: String urlFrontend = "http://localhost:5500/reset_password.html?token=" + token;
String urlFrontend = "https://controlsystemlogistic.com/reset_password.html?token=" + token;
        
        String cuerpo = "Hola,\n\nPara restablecer tu contraseña en local, haz clic aquí:\n" + urlFrontend;
        
        message.setText(cuerpo);
        mailSender.send(message);
    }

    // --- CONFIRMACIÓN DE REGISTRO MANUAL ---
    public void enviarCorreoRegistro(String destino, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("controlsystemlogistic@gmail.com");
        message.setTo(destino);
        message.setSubject("Confirma tu cuenta (LOCAL)");

        String urlConfirmacion = "https://controlsystemlogistic.com/api/auth/confirm-account?token=" + token;
        String cuerpo = "Hola,\n\nConfirma tu registro en local haciendo clic aquí:\n" + urlConfirmacion;

        message.setText(cuerpo);
        mailSender.send(message);
    }

    // --- NUEVO: BIENVENIDA GOOGLE (Este es el que te faltaba) ---
    public void enviarCorreoConfiguracion(String destino, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("controlsystemlogistic@gmail.com");
        message.setTo(destino);
        message.setSubject("¡Bienvenido a CSL! Completa tu perfil");

        // Apunta a la NUEVA página complete_profile.html
        String urlFrontend = "https://controlsystemlogistic.com/complete_profile.html?token=" + token;

        String cuerpo = "¡Hola!\n\n" +
                "Gracias por registrarte con Google en CSL.\n" +
                "Para activar tu cuenta y establecer tu contraseña, haz clic aquí:\n\n" +
                urlFrontend;

        message.setText(cuerpo);
        mailSender.send(message);
    }

    public void enviarPresupuesto(String destino, String transporte, String peso, String precio) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("controlsystemlogistic@gmail.com");
    message.setTo(destino);
    message.setSubject("Tu Presupuesto - CSL"); // Queda mejor sin lo de "(LOCAL)"
    // Cambiamos localhost por el dominio real
    message.setText("El precio estimado es: " + precio + "\n\nEntra en https://controlsystemlogistic.com para gestionar tus pedidos.");
    mailSender.send(message);
}
}