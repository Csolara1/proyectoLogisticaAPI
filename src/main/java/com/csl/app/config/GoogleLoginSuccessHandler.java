package com.csl.app.config;

import com.csl.app.model.User;
import com.csl.app.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Obtener datos de Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        // Si quieres la foto: String picture = oAuth2User.getAttribute("picture");

        // 2. Verificar si el usuario existe en tu BD
        Optional<User> userOptional = userRepository.findByUserEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Actualizamos el nombre por si lo cambió en Google
            user.setFullName(name);
            userRepository.save(user);
        } else {
            // Usuario nuevo: Lo registramos automáticamente
            user = new User();
            user.setUserEmail(email);
            user.setFullName(name);
            user.setRoleId(2); // Rol 2 = CLIENTE por defecto
            user.setIsActive(true); // Google verifica el email, así que activamos directo
            user.setCreatedAt(LocalDateTime.now());
            // Contraseña vacía o aleatoria, ya que entra por Google
            user.setUserPassword(""); 
            userRepository.save(user);
        }

        // 3. Redirigir al Frontend (Puerto 5500) con los datos en la URL
        // Usamos URLEncoder para evitar errores con espacios o tildes en el nombre
        String encodedName = URLEncoder.encode(user.getFullName(), StandardCharsets.UTF_8);
        
        String redirectUrl = "https://controlsystemlogistic.com/index.html" +
                "?google_auth=true" +
                "&userId=" + user.getUserId() +
                "&roleId=" + user.getRoleId() +
                "&fullName=" + encodedName +
                "&email=" + user.getUserEmail();

        response.sendRedirect(redirectUrl);
    }
}