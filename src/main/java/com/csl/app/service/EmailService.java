package com.csl.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // --- CORREO DE RECUPERACIÓN DE CONTRASEÑA ---
    public void enviarCorreoRecuperacion(String destino, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        // Remitente (Debe coincidir con application.properties)
        message.setFrom("controlsystemlogistic@gmail.com");
        message.setTo(destino);
        message.setSubject("Restablecer Contraseña - CSL Logistics");
        
        // Enlace al Frontend (reset_password.html)
        // Ajusta el puerto 5500 si usas otro en VS Code
        String urlFrontend = "http://127.0.0.1:5500/reset_password.html?token=" + token;
        
        String cuerpo = "Hola,\n\n" +
                "Has solicitado restablecer tu contraseña.\n" +
                "Haz clic en el siguiente enlace para crear una nueva:\n\n" +
                urlFrontend + "\n\n" +
                "Si no has sido tú, ignora este mensaje.\n" +
                "Atentamente,\nEquipo de CSL Logistics.";
        
        message.setText(cuerpo);
        mailSender.send(message);
        System.out.println("✅ Correo recuperación enviado a: " + destino);
    }

    // --- NUEVO: CORREO DE CONFIRMACIÓN DE REGISTRO ---
    public void enviarCorreoRegistro(String destino, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("controlsystemlogistic@gmail.com");
        message.setTo(destino);
        message.setSubject("¡Bienvenido a CSL! Confirma tu cuenta");

        // El enlace apunta al BACKEND para activar la cuenta
        String urlConfirmacion = "http://localhost:8080/api/auth/confirm-account?token=" + token;

        String cuerpo = "Hola,\n\n" +
                "Gracias por registrarte en Control System Logistics.\n" +
                "Para activar tu cuenta y poder entrar, confirma que eres tú haciendo clic aquí:\n\n" +
                urlConfirmacion + "\n\n" +
                "Si no has sido tú, simplemente ignora este correo.\n\n" +
                "¡Nos vemos pronto!\n" +
                "El equipo de CSL.";

        message.setText(cuerpo);
        mailSender.send(message);
        System.out.println("✅ Correo confirmación enviado a: " + destino);
    }
}