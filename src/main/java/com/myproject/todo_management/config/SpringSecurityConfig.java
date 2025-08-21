package com.myproject.todo_management.config;

import com.myproject.todo_management.security.JwtAuthenticationEntryPoint;
import com.myproject.todo_management.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // Aktifkan @PreAuthorize dan @PostAuthorize
@AllArgsConstructor
public class SpringSecurityConfig {

    private UserDetailsService userDetailsService;

    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    private JwtAuthenticationFilter authenticationFilter;

//    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
//                Menonaktifkan CSRF (Cross-Site Request Forgery) protection. Biasanya dimatikan untuk API atau testing.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> {
                    // Endpoint publik
                    authorize.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                    authorize.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll();

                    // IZINKAN folder uploads (gambar dll) bisa diakses publik
                    authorize.requestMatchers("/uploads/**").permitAll();

                    // Endpoint delete user hanya untuk ADMIN
                    authorize.requestMatchers(HttpMethod.DELETE, "/api/auth/delete/**").hasRole("ADMIN");

                    // Endpoint get all user hanya untuk ADMIN
                    authorize.requestMatchers(HttpMethod.GET, "/api/auth/users").hasRole("ADMIN");

                    // Preflight request (CORS)
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // Semua request lain harus login
                    authorize.anyRequest().authenticated();

                }).exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Filter JWT sebelum UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
