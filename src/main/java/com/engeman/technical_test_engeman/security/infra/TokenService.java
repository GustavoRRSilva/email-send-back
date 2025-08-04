package com.engeman.technical_test_engeman.security.infra;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.engeman.technical_test_engeman.domain.User;



@Service
public class TokenService {
    @Value("${api.security.props}")
    private String secret;
    public String generateToken(User user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
            		.withIssuer("login-auth-api")
            		.withSubject(user.getEmail())
            		.withExpiresAt(this.generateExpirationDate())
            		.sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while authetication");
        }
    }
    
    public String validateToken(String token) {
    	try {
    		Algorithm algorithm = Algorithm.HMAC256(secret);
    		return JWT.require(algorithm)
    				.withIssuer("login-auth-api")
    				.build()
    				.verify(token)
    				.getSubject()
;		} catch (JWTVerificationException exception) {
			return null;
		}
    }
    
    private Instant generateExpirationDate() {
    	return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.of("-3"));
    }
}
