package tech.alexberbo.berboapp.constant.security;

public interface SecurityConstants {
    String[] PUBLIC_URLS = { "/user/verify/**", "/user/login/**", "/user/register/**", "/user/renew-password", "/user/reset-password/**", "/user/refresh/token", "/user/image/**" };
    String TOKEN_PREFIX = "Bearer ";
}
