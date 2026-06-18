package gerenciamento.frotas.TrabFinal.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:frotas-secret-key-2026-gerenciamento-sistema}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration; // padrão: 24 horas em ms

    /**
     * Gera um token JWT com email, role e expiração
     */
    public String gerarToken(String email, String role) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(agora)
                .setExpiration(expiracao)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida o token JWT
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("Token inválido ou expirado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extrai o email (subject) do token
     */
    public String extrairEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrai a role (papel) do token
     */
    public String extrairRole(String token) {
        Object role = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");

        return role != null ? role.toString() : "ROLE_USER";
    }

    /**
     * Verifica se o token está expirado
     */
    public boolean estaExpirado(String token) {
        try {
            Date expiracao = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expiracao.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}