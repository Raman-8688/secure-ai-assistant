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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId(); // "google" or "github"

        log.info("OAuth2 login successful for provider: {}", provider);

        String email = null;
        String name = null;

        // Extract user info based on provider (Google and GitHub have different attribute names)
        if ("google".equalsIgnoreCase(provider)) {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
        } else if ("github".equalsIgnoreCase(provider)) {
            // GitHub uses "login" for username, but may need email from email endpoint
            String githubLogin = oauth2User.getAttribute("login");
            name = oauth2User.getAttribute("name") != null ? oauth2User.getAttribute("name") : githubLogin;

            // GitHub might not always return email in the basic profile
            // The user:email scope should include it, but fallback to login if needed
            email = oauth2User.getAttribute("email");
            if (email == null) {
                // If email not provided, use GitHub username as unique identifier
                email = githubLogin + "@github.com";
                log.warn("Email not provided by GitHub, using: {}", email);
            }
        }

        if (email == null) {
            log.error("Could not extract email from OAuth2 provider: {}", provider);
            response.sendRedirect("http://localhost:4200/login?error=email_required");
            return;
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            log.info("Existing user logged in via {}: {}", provider, email);

            // Update provider info if this is first time using this provider
            if (user.getProvider() == null) {
                user.setProvider(provider);
                userRepository.save(user);
            }
        } else {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : email.split("@")[0]);
            user.setEmailVerified(true); // OAuth2 users are verified
            user.setProvider(provider);
            user.setRole("USER");
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("New user created via {}: {}", provider, email);
        }

        String jwtToken = jwtService.generateToken(user.getEmail());

        String redirectUrl = "http://localhost:4200/auth/callback?token=" + jwtToken;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}