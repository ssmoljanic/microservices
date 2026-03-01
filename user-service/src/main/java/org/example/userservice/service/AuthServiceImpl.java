package org.example.userservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.userservice.domain.ActivationToken;
import org.example.userservice.domain.PasswordResetToken;
import org.example.userservice.domain.Role;
import org.example.userservice.domain.User;
import org.example.userservice.dto.LoginRequest;
import org.example.userservice.dto.LoginResponse;
import org.example.userservice.dto.PasswordResetConfirm;
import org.example.userservice.dto.PasswordResetRequest;
import org.example.userservice.dto.RegisterRequest;
import org.example.userservice.dto.RegisterResponse;
import org.example.userservice.messaging.NotifProducer;
import org.example.userservice.repository.ActivationTokenRepository;
import org.example.userservice.repository.PasswordResetTokenRepository;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.security.service.TokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static io.micrometer.core.instrument.util.StringEscapeUtils.escapeJson;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ActivationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final TokenService tokenService;
    private final NotifProducer notifProducer;

    public AuthServiceImpl(UserRepository userRepository,
                           ActivationTokenRepository tokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           TokenService tokenService,
                           NotifProducer notifProducer) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.tokenService = tokenService;
        this.notifProducer = notifProducer;
    }


    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(Role.PLAYER);
        user.setActivated(false);
        user.setBlocked(false);

        userRepository.save(user);

        ActivationToken token = new ActivationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUsed(false);

        tokenRepository.save(token);


        String activationLink = "http://localhost:5173/activate?token=" + token.getToken();
        String correlationId = "activation-" + user.getId(); // ili UUID ako želiš


        String firstName = user.getFullName();
        if (firstName != null && firstName.contains(" ")) {
            firstName = firstName.split("\\s+")[0];
        }

        String msg = """
    {
      "type": "ACCOUNT_ACTIVATION",
      "toEmail": "%s",
      "userId": %d,
      "correlationId": "%s",
      "vars": {
        "firstName": "%s",
        "activationLink": "%s"
      }
    }
    """.formatted(
                user.getEmail(),
                user.getId(),
                correlationId,
                escapeJson(firstName),
                escapeJson(activationLink)
        );

        notifProducer.send(msg);
        return new RegisterResponse(token.getToken());
    }

    @Override
    public boolean activate(String tokenValue){

        ActivationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        if(token.isUsed()){
            throw new RuntimeException("Token already used");
        }

        if(token.getExpiresAt().isBefore(LocalDateTime.now())){
            throw  new RuntimeException("Token expired");
        }

        token.getUser().setActivated(true);
        token.setUsed(true);
        tokenRepository.save(token);

        return true;

    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Optional<User> opt = userRepository.findByEmail(request.getEmail());
        if(opt.isEmpty()){
            return new LoginResponse(true,null);
        }
        User user = opt.get();

        if(!user.isActivated() || user.isBlocked()){
            return new LoginResponse(true,null);
        }

        if(!user.getPassword().equals(request.getPassword())){
            return new LoginResponse(true,null);
        }

        Claims claims = Jwts.claims();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", "ROLE_" + user.getRole().name());

        String token = tokenService.generate(claims);

        return new LoginResponse(false, token);
    }

    @Override
    public void requestPasswordReset(PasswordResetRequest request) {
        Optional<User> optUser = userRepository.findByEmail(request.getEmail());


        if (optUser.isEmpty()) {
            return;
        }

        User user = optUser.get();


        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now(ZoneId.of("UTC")).plusHours(1));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);


        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken.getToken();
        String firstName = user.getFullName();
        if (firstName != null && firstName.contains(" ")) {
            firstName = firstName.split("\\s+")[0];
        }

        String msg = """
            {
              "type": "PASSWORD_RESET",
              "toEmail": "%s",
              "userId": %d,
              "correlationId": "reset-%s",
              "vars": {
                "firstName": "%s",
                "resetLink": "%s"
              }
            }
            """.formatted(
                user.getEmail(),
                user.getId(),
                resetToken.getToken(),
                escapeJson(firstName),
                escapeJson(resetLink)
        );

        notifProducer.send(msg);
    }

    @Override
    public boolean confirmPasswordReset(PasswordResetConfirm request) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Nevazeci token za reset lozinke"));


        if (resetToken.isUsed()) {
            throw new RuntimeException("Token je vec iskoriscen");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
            throw new RuntimeException("Token je istekao");
        }

        User user = resetToken.getUser();
        user.setPassword(request.getNewPassword());

        resetToken.setUsed(true);

        userRepository.save(user);
        passwordResetTokenRepository.save(resetToken);

        return true;
    }
}
