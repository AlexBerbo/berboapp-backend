package tech.alexberbo.berboapp.constant.security;

public interface SecurityConstants {
    String[] PUBLIC_URLS = { "/user/verify/**", "/user/login/**", "/user/register/**", "/user/reset-password/**", "/user/refresh/token" };
    String TOKEN_PREFIX = "Bearer ";
}
