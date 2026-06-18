package gerenciamento.frotas.TrabFinal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Extrair token do header Authorization
            String token = extrairToken(request);

            // 2. Se token existe e é válido, autenticar
            if (token != null && jwtUtil.validarToken(token)) {
                String email = jwtUtil.extrairEmail(token);
                String role = jwtUtil.extrairRole(token);

                // 3. Criar autenticação (sem senha, apenas email + role)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Arrays.asList(new SimpleGrantedAuthority(role))
                        );

                // 4. Colocar autenticação no contexto Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar JWT: " + e.getMessage());
            // Se erro, não autentica (rota protegida dará 401)
        }

        // 5. Continuar a requisição
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do header Authorization
     * Esperado: "Authorization: Bearer eyJ..."
     */
    private String extrairToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer "
        }

        return null;
    }
}