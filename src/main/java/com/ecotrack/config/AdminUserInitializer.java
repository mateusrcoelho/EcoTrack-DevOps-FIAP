package com.ecotrack.config;

import com.ecotrack.entity.Role;
import com.ecotrack.entity.Usuario;
import com.ecotrack.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.enabled:false}")
    private boolean enabled;

    @Value("${app.admin.name:Administrador}")
    private String name;

    @Value("${app.admin.email:admin@ecotrack.com}")
    private String email;

    @Value("${app.admin.password:}")
    private String password;

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }

        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD deve ser informado quando APP_ADMIN_ENABLED=true");
        }

        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setNome(name);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(password));
            usuario.setRole(Role.ADMIN);
            usuarioRepository.save(usuario);
        }
    }
}
