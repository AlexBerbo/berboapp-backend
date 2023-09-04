package tech.alexberbo.berboapp.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;

import static com.twilio.rest.api.v2010.account.Message.creator;

/**
    This is not used anywhere in the app, but this is a pattern of sending the SMS for MFA.
    And it is a paid dependency and plugin, so I will not use this for this project.
 */
@Slf4j
public class SmsUtil {
    public static final String FROM_NUMBER = "+491631858459";
    public static final String SID_KEY = "key";
    public static final String TOKEN_KEY = "token";
    public static void sendSMS(String to, String messageBody) {
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = creator(new PhoneNumber("+1" + to), new PhoneNumber(FROM_NUMBER), messageBody).create();
        log.info(message.toString());
    }
}
