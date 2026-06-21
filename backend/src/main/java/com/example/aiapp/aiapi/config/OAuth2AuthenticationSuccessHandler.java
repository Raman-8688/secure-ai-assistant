package com.example.aiapp.aiapi.config;

import com.example.aiapp.aiapi.entity.User;
import com.example.aiapp.aiapi.repository.UserRepository;
import com.example.aiapp.aiapi.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        String email = null;
        String name = null;

        if ("google".equalsIgnoreCase(provider)) {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
        } else if ("github".equalsIgnoreCase(provider)) {
            String githubLogin = oauth2User.getAttribute("login");
            name = oauth2User.getAttribute("name");
            if (name == null) name = githubLogin;
            email = oauth2User.getAttribute("email");
            if (email == null) {
                email = githubLogin + "@github.com";
                log.warn("Email not provided by GitHub, using: {}", email);
            }
        }

        if (email == null) {
            log.error("Could not extract email from OAuth2 provider: {}", provider);
            response.sendRedirect(frontendUrl + "/login?error=email_required");
            return;
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            log.info("Existing user logged in via {}: {}", provider, email);

            if (user.getProvider() == null) {
                user.setProvider(provider);
                userRepository.save(user);
            }
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : email.split("@")[0]);
            user.setEmailVerified(true);
            user.setProvider(provider);
            user.setRole("USER");
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("New user created via {}: {}", provider, email);
        }

        String jwtToken = jwtService.generateToken(user.getEmail());
        String redirectUrl = frontendUrl + "/auth/callback?token=" + jwtToken;

        log.info("Redirecting to: {}", redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}