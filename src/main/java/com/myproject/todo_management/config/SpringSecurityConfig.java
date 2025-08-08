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
@EnableMethodSecurity
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
                    // Hanya login yang boleh tanpa autentikasi
                    authorize.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();

                    // Register hanya boleh diakses oleh ADMIN
                    authorize.requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("ADMIN");

//                Semua request HARUS login dulu (tidak ada endpoint publik).
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    authorize.anyRequest().authenticated();
//                Autentikasi dilakukan dengan HTTP Basic Auth (username & password dikirim di header).

                })// Hapus httpBasic(), ganti dengan stateless session untuk JWT
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                // Tambahkan .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) supaya Spring Security tidak membuat session (karena JWT sifatnya stateless).
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


//        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
