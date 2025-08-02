package loginauthapi.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import loginauthapi.domain.User;

@Service
public class TokenService {
    @Value("${api.security.props}")
    private String secret;
    public String generateToken(User user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create().withIssuer("login-auth-api").withSubject(user.getEmail()).withExpiresAt(this.generateExpirationDate()).sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while authetication");
        }
    }
    
    private Instant generateExpirationDate() {
    	return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.of("-3"));
    }
}
