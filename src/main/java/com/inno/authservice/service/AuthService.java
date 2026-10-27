package com.inno.authservice.service;

import com.inno.authservice.dto.*;
import com.inno.authservice.entity.Credentials;
import com.inno.authservice.entity.Role;
import com.inno.authservice.exception.InvalidCredentialsException;
import com.inno.authservice.exception.InvalidTokenException;
import com.inno.authservice.exception.LoginAlreadyExistsException;
import com.inno.authservice.repository.CredentialsRepository;
import com.inno.authservice.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void save(SaveCredentialsRequest request) {
        if (credentialsRepository.existsByLogin(request.getLogin())) {
            throw new LoginAlreadyExistsException("Login already exists");
        }

        Credentials credentials = new Credentials();
        credentials.setUserId(request.getUserId());
        credentials.setLogin(request.getLogin());
        credentials.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        credentials.setRole(request.getRole());

        credentialsRepository.save(credentials);
    }

    public LoginResponse login(LoginRequest request) {
        Credentials credentials = credentialsRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login or password"));

        if (!passwordEncoder.matches(request.getPassword(), credentials.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid login or password");
        }

        return new LoginResponse(
                jwtService.generateAccessToken(credentials),
                jwtService.generateRefreshToken(credentials));
    }

    public ValidateResponse validate(ValidateRequest request) {
        try {
            Claims claims = jwtService.parse(request.getAccessToken());

            if (!"access".equals(claims.get("type", String.class))) {
                return new ValidateResponse(false, null, null);
            }

            Long userId = claims.get("userId", Long.class);
            Role role = Role.valueOf(claims.get("role", String.class));
            return new ValidateResponse(true, userId, role);
        } catch (JwtException e) {
            return new ValidateResponse(false, null, null);
        }
    }

    public RefreshResponse refresh(RefreshRequest request) {
        Claims claims;
        try {
            claims = jwtService.parse(request.getRefreshToken());
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        Long userId = claims.get("userId", Long.class);
        Credentials credentials = credentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        return new RefreshResponse(
                jwtService.generateAccessToken(credentials),
                jwtService.generateRefreshToken(credentials));
    }
}