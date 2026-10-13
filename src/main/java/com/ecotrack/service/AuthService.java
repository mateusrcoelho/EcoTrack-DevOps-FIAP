package com.ecotrack.service;

import com.ecotrack.dto.*;
import com.ecotrack.entity.Role;
import com.ecotrack.entity.Usuario;
import com.ecotrack.exception.BusinessException;
import com.ecotrack.repository.UsuarioRepository;
import com.ecotrack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) throw new BusinessException("E-mail já cadastrado");
        Role role = request.role() == null ? Role.USER : request.role();
        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .role(role)
                .build();
        usuarioRepository.save(usuario);
        String token = jwtService.generateToken(new User(usuario.getEmail(), usuario.getSenha(),
                List.of(() -> "ROLE_" + role.name())));
        return new AuthResponse(token, "Bearer", usuario.getEmail(), role.name());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(new User(usuario.getEmail(), usuario.getSenha(),
                List.of(() -> "ROLE_" + usuario.getRole().name())));
        return new AuthResponse(token, "Bearer", usuario.getEmail(), usuario.getRole().name());
    }
}
