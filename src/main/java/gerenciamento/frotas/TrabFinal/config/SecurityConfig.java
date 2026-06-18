package gerenciamento.frotas.TrabFinal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // ════════════════════════════════════════════════════════════════════
    // BEAN: RestTemplate para chamadas HTTP (GPS, APIs externas)
    // ════════════════════════════════════════════════════════════════════
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // ════════════════════════════════════════════════════════════════════
    // BEAN: PasswordEncoder para BCrypt
    // ════════════════════════════════════════════════════════════════════
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ════════════════════════════════════════════════════════════════════
    // BEAN: AuthenticationManager
    // ════════════════════════════════════════════════════════════════════
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ════════════════════════════════════════════════════════════════════
    // BEAN: CORS Configuration
    // ════════════════════════════════════════════════════════════════════
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:3000", "*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ════════════════════════════════════════════════════════════════════
    // BEAN: Security Filter Chain (JWT + Stateless)
    // ════════════════════════════════════════════════════════════════════
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilitar CSRF (stateless não precisa)
                .csrf().disable()

                // CORS habilitado
                .cors().and()

                // Sessão stateless (JWT)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                // Autorização por rota
                .authorizeHttpRequests(authz -> authz
                        // ═══ ROTAS PÚBLICAS (sem autenticação) ═══
                        .requestMatchers("/api/auth/**").permitAll()           // Login e registro
                        .requestMatchers("/api/usuarios").permitAll()         // Criar usuário novo
                        .requestMatchers("/").permitAll()                     // Raiz
                        .requestMatchers("/index.html").permitAll()           // Página de login
                        .requestMatchers("/*.html").permitAll()               // Todos os HTMLs
                        .requestMatchers("/*.css").permitAll()                // Todos os CSSs
                        .requestMatchers("/*.js").permitAll()                 // Todos os JavaScripts
                        .requestMatchers("/favicon.ico").permitAll()          // Favicon

                        // ═══ ROTAS PROTEGIDAS (precisam de JWT) ═══
                        .requestMatchers("/api/**").authenticated()           // Todas as APIs

                        // ═══ PADRÃO ═══
                        .anyRequest().authenticated()
                )

                // Adicionar filtro JWT antes do UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Tratamento de erros de autenticação
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"erro\": \"Não autenticado. Faça login.\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"erro\": \"Acesso negado (403).\"}");
                });

        return http.build();
    }
}