package tech.alexberbo.berboapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tech.alexberbo.berboapp.filter.AuthorizationFilter;
import tech.alexberbo.berboapp.handler.CustomAccessDeniedHandler;
import tech.alexberbo.berboapp.handler.CustomAuthenticationEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static tech.alexberbo.berboapp.constant.security.SecurityConstants.PUBLIC_URLS;

/**
 * Custom security configuration, allowing certain urls to be permitted without authentication, making other
 * protected urls protected with our logic that is done with implementing the JWToken system, adding certain filters,
 * handlers and our custom userDetailService. Providing an authentication manager and setting the password encoder.
 * Enabling method security for easy management of authorities in the controller package and classes.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final BCryptPasswordEncoder encoder;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final AuthorizationFilter authorizationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(withDefaults());
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(STATELESS));
        http.authorizeHttpRequests(matcher -> matcher.requestMatchers(PUBLIC_URLS).permitAll());
        http.authorizeHttpRequests(matcher -> matcher.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll());
        http.authorizeHttpRequests(matcher -> matcher.requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasAnyAuthority("DELETE:USER"));
        http.authorizeHttpRequests(matcher -> matcher.requestMatchers(HttpMethod.DELETE, "/customer/delete/**").hasAnyAuthority("DELETE:CUSTOMER"));
        http.exceptionHandling(e -> e.accessDeniedHandler(customAccessDeniedHandler).authenticationEntryPoint(customAuthenticationEntryPoint));
        http.authorizeHttpRequests(matcher -> matcher.anyRequest().authenticated());
        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authProvider);
    }
}
