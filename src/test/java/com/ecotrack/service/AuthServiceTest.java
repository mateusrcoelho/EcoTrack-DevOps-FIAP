package com.ecotrack.service;

import com.ecotrack.dto.RegisterRequest;
import com.ecotrack.entity.Role;
import com.ecotrack.exception.BusinessException;
import com.ecotrack.repository.UsuarioRepository;
import com.ecotrack.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService service;

    @Test
    void deveImpedirCadastroComEmailDuplicado() {
        RegisterRequest request = new RegisterRequest(
                "Usuário Teste",
                "usuario@ecotrack.com",
                "senha123",
                Role.USER
        );
        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("E-mail já cadastrado");
    }
}
