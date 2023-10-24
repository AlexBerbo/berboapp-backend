package tech.alexberbo.berboapp.util;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import static nl.basjes.parse.useragent.UserAgent.*;

public class RequestUtils {
    public static final String UNKNOWN_IP_ADDRESS = "Unknown IP Address";
    public static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";
    public static final String USER_AGENT_HEADER = "user-agent";

    public static String getIpAddress(HttpServletRequest request) {
        return setIpAddress(request, UNKNOWN_IP_ADDRESS);
    }

    public static String getDevice(HttpServletRequest request) {
        UserAgentAnalyzer analyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();
        UserAgent agent = analyzer.parse(request.getHeader(USER_AGENT_HEADER));
        return agent.getValue(OPERATING_SYSTEM_NAME) + " - " + agent.getValue(AGENT_NAME) + " - " + agent.getValue(DEVICE_NAME);
    }

    private static String setIpAddress(HttpServletRequest request, String ipAddress) {
        if(request != null) {
            ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
            if(ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }
}
