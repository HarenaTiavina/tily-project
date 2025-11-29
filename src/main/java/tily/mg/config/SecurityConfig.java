package tily.mg.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tily.mg.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Resources publiques
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                // Pages d'authentification accessibles à tous
                .requestMatchers("/login", "/inscription", "/auth/**", "/access-denied").permitAll()
                // Pages réservées aux ADMIN uniquement
                .requestMatchers("/dashboard", "/", "/responsables/**", "/eleves/**").hasRole("ADMIN")
                // Page profil accessible aux USER et ADMIN
                .requestMatchers("/profil/**").hasAnyRole("USER", "ADMIN")
                // Toutes les autres requêtes nécessitent une authentification
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/auth/login")
                .successHandler((request, response, authentication) -> {
                    // Redirection basée sur le rôle
                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    if (isAdmin) {
                        response.sendRedirect("/dashboard");
                    } else {
                        response.sendRedirect("/profil");
                    }
                })
                .failureUrl("/login?error=true")
                .usernameParameter("email")
                .passwordParameter("motDePasse")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/access-denied");
                })
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/auth/**", "/eleves/import-excel", "/responsables/import-excel")
            );

        return http.build();
    }
}
