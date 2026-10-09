package com.example.api.configuration;

import com.example.api.configuration.JwtUtils;
import com.example.api.filter.JwtFilter;
import com.example.api.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userService;
    private final JwtUtils jwtUtils;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/soutien/auth/*").permitAll()
                                .requestMatchers("/soutien/*").permitAll()
                                .requestMatchers("/api/upload").permitAll()
                                .requestMatchers("/api/documents").permitAll()
                                .requestMatchers("/api/document/**").permitAll()
                                .requestMatchers("/api/kanban/**").permitAll()
                                .requestMatchers("/soutien/**").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(new JwtFilter(userService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
