package br.com.AutoStock.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityDemo {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @SuppressWarnings({ "deprecation", "removal" })
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Área exclusiva da agência: gerenciamento de funcionários
                        .requestMatchers("/employees/**").hasRole("USER")

                        // Convite de funcionário: só agência
                        .requestMatchers("/funcionario/convite", "/funcionario/convite/**").hasRole("USER")

                        // Cadastro do funcionário via token: público
                        .requestMatchers("/funcionario/cadastro", "/funcionario/cadastro/**").permitAll()

                        // Área exclusiva do FUNCIONÁRIO (se tiver algo no futuro)
                        .requestMatchers("/employee/**").hasRole("EMPLOYEE")

                        // Veículos e contratos: tanto agência quanto funcionário
                        .requestMatchers(
                                "/vehicles/**",
                                "/contracts/**"
                        ).hasAnyRole("USER", "EMPLOYEE")

                        // Home logada (os dois podem ver)
                        .requestMatchers("/home").hasAnyRole("USER", "EMPLOYEE")

                        // Público
                        .requestMatchers(
                                "/",
                                "/login",
                                "/error",
                                "/terms",
                                "/registration/**",
                                "/verify-code",
                                "/verify-code/**",
                                "/api/validate/**",
                                "/api/cnpj/**",
                                "/api/cep/**",
                                "/vehicles/fipe/**",
                                "/uploads/**",
                                "/styles/**",
                                "/scripts/**",
                                "/imgs/**"
                        ).permitAll()

                        // Qualquer outra coisa: autenticado
                        .requestMatchers("/profile/**").hasRole("USER")
                        .anyRequest().authenticated()
  
                ).exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/home")
                        )
                
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                )
                .build();
    }
}
