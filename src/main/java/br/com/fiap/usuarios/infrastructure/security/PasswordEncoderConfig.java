package br.com.fiap.usuarios.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Expõe o {@link PasswordEncoder} usado para gerar e validar hashes de senha (BCrypt),
 * garantindo que nenhuma senha seja persistida ou comparada em texto plano.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
