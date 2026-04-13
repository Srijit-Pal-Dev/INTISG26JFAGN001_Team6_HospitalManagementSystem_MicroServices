//package com.cognizant.labService.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // Disable CSRF for H2 console
//                .csrf(csrf -> csrf.disable())
//
//                // Allow H2 console to be displayed in frames
//                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
//
//                // Allow all requests (for now)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/h2-console/**",
//                                "/lab-tests/**",
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**"
//                        ).permitAll()
//                        .anyRequest().permitAll()
//                )
//
//                // Disable default login page
//                .httpBasic(Customizer.withDefaults());
//
//        return http.build();
//    }
//}
