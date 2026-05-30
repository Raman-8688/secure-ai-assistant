package com.example.aiapp.aiapi.config;



import com.example.aiapp.aiapi.entity.User;
import com.example.aiapp.aiapi.repository.UserRepository;
import com.example.aiapp.aiapi.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        log.info("OAuth2 login successful for email: {}", email);

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            log.info("Existing user logged in via OAuth2: {}", email);
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setEmailVerified(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setRole("USER");
            userRepository.save(user);
            log.info("New user created via OAuth2: {}", email);
        }

        String jwtToken = jwtService.generateToken(user.getEmail());

        String redirectUrl = "http://localhost:4200/auth/callback?token=" + jwtToken;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
