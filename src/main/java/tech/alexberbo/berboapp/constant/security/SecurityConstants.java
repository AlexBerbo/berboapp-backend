package tech.alexberbo.berboapp.constant.security;

public interface SecurityConstants {
    String[] PUBLIC_URLS = { "/user/verify/**", "/user/login/**", "/user/register/**", "/user/renew-password", "/user/reset-password/**", "/user/refresh/token/**", "/user/image/**" };
    String TOKEN_PREFIX = "Bearer ";
    String ALEXBERBO = "alexberbo.tech RS";
    String ALEXBERBO_MANAGEMENT = "alexberbo Team";
    long TOKEN_EXPIRATION_DATE = 1200000;
    long REFRESH_TOKEN_EXPIRATION_DATE = 1500000;
    String AUTHORITIES = "authorities";
}
