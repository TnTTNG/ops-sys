package ops.example.backend.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class JWTUtils {
    private static String secret;
    private static Algorithm algorithm;
    private static JWTVerifier verifier;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
        secret = jwtSecret;
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm).build();
    }

    public static String validateToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getAudience().get(0).split("-")[0];
        } catch (JWTVerificationException e) {
            return null;
        }
    }
} 