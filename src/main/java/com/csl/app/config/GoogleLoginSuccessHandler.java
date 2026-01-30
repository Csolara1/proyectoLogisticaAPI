package com.csl.app.config;

import com.csl.app.model.User;
import com.csl.app.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class GoogleLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();
        String email = googleUser.getAttribute("email");
        String name = googleUser.getAttribute("name");

        Optional<User> existingUser = userRepository.findByUserEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setUserEmail(email);
            user.setFullName(name);
            user.setRoleId(2);
            user.setIsActive(true);
            user.setMobilePhone("");
            user.setUserPassword(UUID.randomUUID().toString());

            user = userRepository.save(user);
        }

        // --- CORRECCIÓN AQUÍ ---
        // Redirigimos explícitamente al puerto 5500 donde está tu Frontend
        getRedirectStrategy().sendRedirect(request, response,
                "http://controlsystemlogistic.com/login.html?google_auth=success&user_id=" + user.getUserId());
    }
}