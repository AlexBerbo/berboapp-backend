package tech.alexberbo.berboapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.alexberbo.berboapp.provider.JWTProvider;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.OPTIONS;
import static tech.alexberbo.berboapp.constant.security.FilterConstants.PUBLIC_URLS;
import static tech.alexberbo.berboapp.constant.security.SecurityConstants.TOKEN_PREFIX;
import static tech.alexberbo.berboapp.exception.FilterExceptionHandler.handleExceptions;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {
    private final JWTProvider jwtProvider;

    /**
     * Here a user is passing the token, and we are checking if the token is valid,
     * and we are telling spring that the token is good, assigning the token to its rightful
     * owner and setting the user as authenticated, else we are clearing the spring context
     * removing access for the user, then we let the filterChain do its own thing of going through
     * other important filters and returning corresponding info to the user.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        try {
            String token = getToken(request);
            Long userId = getUserId(request);
            if (jwtProvider.isTokenValid(token, userId)) {
                List<GrantedAuthority> authorities = jwtProvider.getAuthorities(token);
                Authentication authentication = jwtProvider.getAuthentication(userId, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
            filter.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            handleExceptions(response, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getHeader(AUTHORIZATION) == null || !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
                request.getMethod().equalsIgnoreCase(OPTIONS.name()) || asList(PUBLIC_URLS).contains(request.getRequestURI());
    }

    private Long getUserId(HttpServletRequest request) {
        return jwtProvider.getSubject(getToken(request), request);
    }

    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, EMPTY)).get();
    }
}
