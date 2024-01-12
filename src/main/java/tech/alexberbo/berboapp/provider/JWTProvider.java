package tech.alexberbo.berboapp.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import tech.alexberbo.berboapp.model.UserPrincipal;
import tech.alexberbo.berboapp.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static tech.alexberbo.berboapp.constant.security.SecurityConstants.*;

/**
 * This is where the JWToken is generated.
 * This is how the token is generated and how the info and settings are set.
 * Auth0 is being used as a third party library for setting all the information. <a href="https://github.com/auth0/java-jwt">Documentation</a>
 */
@Component
@RequiredArgsConstructor
public class JWTProvider {
    @Value("${jwt.secret}")
    private String secret;
    private final UserService userService;

    /**
     * This is the pattern to create the access token and set the information about it.
     * To who it belongs (subject), who is the creator and what authorities the owner has, and with what algorithm is set for encryption.
     */
    public String createAccessToken(UserPrincipal userPrincipal) {
        return JWT.create()
                .withIssuer(ALEXBERBO)
                .withAudience(ALEXBERBO_MANAGEMENT)
                .withIssuedAt(new Date())
                .withSubject(String.valueOf(userPrincipal.getUser().getId())).withArrayClaim(AUTHORITIES, getUserPermission(userPrincipal))
                .withExpiresAt(new Date(currentTimeMillis() + TOKEN_EXPIRATION_DATE))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    /**
     * This is the pattern tho set the refresh token, everything is the same as for the access token, it just does not have the claims.
     * Because it will only be set to the user that gets the access token first, and then this one.
     */
    public String createRefreshAccessToken(UserPrincipal userPrincipal) {
        return JWT.create()
                .withIssuer(ALEXBERBO)
                .withAudience(ALEXBERBO_MANAGEMENT)
                .withIssuedAt(new Date())
                .withSubject(String.valueOf(userPrincipal.getUser().getId()))
                .withExpiresAt(new Date(currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_DATE))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    /**
     * What this is doing: Setting the user authenticated after user's token has been verified.
     * Passing the info to Spring so the can log in and access the app.
     */
    public Authentication getAuthentication(Long userId, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userService.getUserById(userId), null, authorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }

    /**
     * Get the claims of the token, in this case user authorities.
     * Just like in userPrincipal class, map the authorities to new SGAuths and collect them to a list.
     * This is used in the AuthorizationFilter class to set the user authorities for the getAuthentication method.
     */
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] authorities = getTokenAuthorities(token);
        return stream(authorities).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * Getting the user's email that we will set in the AuthorizationFilter class.
     */
    public Long getSubject(String token, HttpServletRequest request) {
        try {
            return Long.valueOf(getJwtVerifier().verify(token).getSubject());
        } catch (TokenExpiredException e) {
            System.out.println(e.getMessage());
            request.setAttribute("tokenExpired", e.getMessage());
            throw e;
        } catch (InvalidClaimException e) {
            request.setAttribute("invalidClaim", e.getMessage());
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean isTokenValid(String token, Long userId) {
        JWTVerifier verifier = getJwtVerifier();
        return !Objects.isNull(userId) && !isTokenExpired(verifier, token);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        return verifier.verify(token).getExpiresAt().before(new Date());
    }

    /**
     * Verifying the token with the verifier from the documentation that is really helpful, and it can be found on this link here ->
     * <a href="https://github.com/auth0/java-jwt">Documentation</a>.
     * Getting the claims (Authorities) from the token and returning them as an array.
     */
    private String[] getTokenAuthorities(String token) {
        JWTVerifier verifier = getJwtVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    /**
     * This returns an array of Authorities (User permissions).
     * This method is used in the createAccessToken method when the claims (Authorities) are being set.
     */
    private String[] getUserPermission(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

    /**
     * Verifier that will be used to verify data about the token and the token itself.
     * Setting the algorithm in this case we use HMAC512.
     * We sign the algorithm with our key meaning the key will be able to decrypt the token later in the jwt.io website.
     */
    private JWTVerifier getJwtVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(ALEXBERBO).build();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Token can't be verified!");
        }
        return verifier;
    }
}
