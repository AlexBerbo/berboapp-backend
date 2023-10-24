package tech.alexberbo.berboapp.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.enumerator.VerificationType;
import tech.alexberbo.berboapp.event.NewUserEvent;
import tech.alexberbo.berboapp.exception.*;
import tech.alexberbo.berboapp.form.UpdateForm;
import tech.alexberbo.berboapp.model.Role;
import tech.alexberbo.berboapp.model.User;
import tech.alexberbo.berboapp.model.UserPrincipal;
import tech.alexberbo.berboapp.repository.RoleRepository;
import tech.alexberbo.berboapp.repository.UserRepository;
import tech.alexberbo.berboapp.rowmapper.UserRowMapper;
import tech.alexberbo.berboapp.service.EmailService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static tech.alexberbo.berboapp.constant.exception.ExceptionConstants.*;
import static tech.alexberbo.berboapp.constant.query.UserQuery.*;
import static tech.alexberbo.berboapp.enumerator.EventType.LOGIN_ATTEMPT_SUCCESS;
import static tech.alexberbo.berboapp.enumerator.RoleType.ROLE_USER;
import static tech.alexberbo.berboapp.enumerator.VerificationType.ACCOUNT;
import static tech.alexberbo.berboapp.enumerator.VerificationType.PASSWORD;

/**
    This is where all the logic and functionality of the User business logic is being realised and implemented.
    With the User and Role logic MySql is used, for other business logic JPA Data will be used, just to have a bit of both worlds and expand my knowledge a bit.
    MySql is harder, and has more code to be written and all the SQL has to be individually written, queries, tables, schemas etc.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {
    private static final String DATE_FORMAT = "yyyy-MM-dd hh-mm-ss";
    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;

    /**
        This method is from the UserDetailsService, when the user logs in, it loads the user by username, in this case by email.
        Setting his corresponding permissions so that ce cannot view unauthorized data.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null) {
            log.error("User with email: " + email + " not found!");
            throw new UsernameNotFoundException("User with email: " + email + " not found!");
        } else {
            log.info("User found: " + user.getFirstName() + " " + user.getLastName() + ".");
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
        }
    }

    /**
        This is where the register logic is implemented. Firstly I checked if the user that is registering is already registered.
        Then, if that is not true, I proceeded with the creation of the new user.
        I used jdbc template from Spring dependency.
        KeyHolder is an ID generator, Auto incrementing the User ID.
        SqlParameterSource is basically what data came in as parameters.
        Then the query is run for inserting the new user in the Database.
        RoleRepository is also called, setting the default user Role and user permissions.
        Verification link is generated so the user can confirm his email, also added to the Database.
        Sending the email to the User so that he can confirm his email, and then he can access his account when logging in.
        User is set as not enabled, because he has to confirm his email first.
     */
    @Override
    public User register(User user) throws EmailExistsException {
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0) {
            throw new EmailExistsException(EMAIL_EXISTS);
        }
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameter = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, parameter, holder);
            user.setId(Objects.requireNonNull(holder.getKey()).longValue());
            roleRepository.setUserRole(user.getId(), ROLE_USER.name());
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType().toLowerCase());
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));
            emailService.sendVerifyAccountEmail(user.getEmail(), user.getFirstName(), verificationUrl);
            // sendTwoFactorAuthCode();
            user.setEnabled(false);
            user.setNotLocked(true);
            return user;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An Error occurred, please try again later.");
        }
    }

    @Override
    public Collection<User> getAllUsers(int page, int pageSize) {
        return null;
    }

    /**
     * Get the user by id, very useful for manipulating the user
     * Used in the JWT provider, and it will be used when passing requests from the client
     */
    @Override
    public User getUser(Long id) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, Map.of("id", id), new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new ApiException("No user found with id: " + id);
        } catch (Exception e) {
            throw new ApiException("An Error occurred");
        }
    }

    @Override
    public Boolean deleteUser(Long id) {
        return null;
    }

    /**
        Here I made a query that will get the user from the DB by Email.
        Exceptions are also handled when this method is used.
     */
    @Override
    public User getUserByEmail(String email) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new ApiException("No user found with email: " + email);
        } catch (Exception e) {
            throw new ApiException("An Error occurred");
        }
    }

    /**
        Verification code for MFA is generated here and also sent to the user by email.
        Code has its own settings which are also set here.
        Email is sent using my email configuration that can be seen in the config package.
     */
    @Override
    public void sendVerificationCode(UserDTO user) {
        String verificationCode = randomAlphanumeric(10).toUpperCase();
        String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
        try {
            jdbc.update(DELETE_FROM_ACCOUNT_VERIFICATION_QUERY, Map.of("userId", user.getId()));
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_CODE_QUERY, Map.of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
            emailService.sendTwoFactorCode(user.getFirstName(), user.getEmail(), verificationCode);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An error occurred");
        }
    }

    /**
        Implementation of the code verification.
        If the code is not expired, the logic will proceed onto the next verification pattern.
        If the user that is trying to verify the code is the same as the one that the code belongs to
        aka when the code was generated it was generated only for that user, and that data is saved in the Database,
        meaning the code belongs only to this user until he verifies it or the code expires.
        If the check for ownership is good, the app will grant access and complete the MFA.
     */
    @Override
    public User verifyCode(String email, String code) throws CodeExpiredException {
        if(isVerificationCodeExpired(code)) throw new CodeExpiredException(CODE_EXPIRED);
        try {
            User userByCode = jdbc.queryForObject(SELECT_USER_BY_CODE_QUERY, Map.of("code", code), new UserRowMapper());
            User userByEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            if(userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())) {
                jdbc.update(DELETE_CODE_QUERY, Map.of("code", code));
                publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_SUCCESS));
                return userByCode;
            } else {
                throw new ApiException("Code is invalid, please try again!");
            }
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new ApiException("No results found!");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An error occurred!");
        }
    }

    /**
        Creates a verification link for the user, sends it to the user per email,
        then when the user sends a request via link, it will send him to the next method to verify that link: verifyVerificationURL(String url)
     */
    @Override
    public void resetPassword(String email) throws EmailDoesNotExistException {
        if(getEmailCount(email) <= 0) throw new EmailDoesNotExistException(EMAIL_DOES_NOT_EXIST);
        try {
            String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType().toLowerCase());
            User user = getUserByEmail(email);
            jdbc.update(DELETE_PASSWORD_VERIFICATION_URL_BY_USER_ID, Map.of("userId", user.getId()));
            jdbc.update(INSERT_PASSWORD_VERIFICATION_URL, Map.of("userId", user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
            emailService.sendPasswordResetEmail(user.getFirstName(), user.getEmail(), verificationUrl);
            log.info("URL: " + verificationUrl);
        } catch (Exception e) {
            throw new ApiException("An error occurred! Please try again or contact berbo99@gmail.com");
        }
    }

    /**
        Verification of the URL that has been sent in resetPassword(String email) method.
        IsURLExpired is checking if the url is valid, then it sends a confirmation to the user whether it is or not.
        User is not redirected to the page to change his password.
     */
    @Override
    public User verifyVerificationURL(String url) throws PasswordResetCodeExpiredException {
        if(isURLExpired(url)) throw new PasswordResetCodeExpiredException(RESET_PASSWORD_URL_EXPIRED);
        try {
            //jdbc.update(DELETE_PASSWORD_VERIFICATION_URL_BY_USER_ID, Map.of("userId", user.getId()));
            return jdbc.queryForObject(SELECT_USER_BY_PW_RESET_URL_QUERY, Map.of("url", getVerificationUrl(url, PASSWORD.getType().toLowerCase())), new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new ApiException("User not found!");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An error occurred please reset your password again!");
        }
    }

    /**
        Simple change password checks and updating the database with the new password if they match.
     */
    @Override
    public void renewPassword(String url, String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) throw new ApiException("Passwords do not match, try again.");
        try {
            jdbc.update(UPDATE_USR_PASSWORD_BY_URL_QUERY, Map.of("password", encoder.encode(password), "url", getVerificationUrl(url, PASSWORD.getType().toLowerCase())));
            jdbc.update(DELETE_PASSWORD_URL_QUERY, Map.of("url", getVerificationUrl(url, PASSWORD.getType().toLowerCase())));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An Error occurred!");
        }
    }

    /**
        This method will enable the user to login after his/her registration.
        The link to the confirmation is sent in the register method when the user successfully registers to the app.
     */
    @Override
    public User verifyAccount(String key) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_ACC_URL_QUERY, Map.of("url", getVerificationUrl(key, ACCOUNT.getType().toLowerCase())), new UserRowMapper());
            jdbc.update(UPDATE_USER_ENABLED_QUERY, Map.of("enabled", true, "userId", user.getId()));
            return user;
        } catch (EmptyResultDataAccessException e) {
            log.error(e.getMessage());
            throw new ApiException("Link Expired!");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An Error occurred!");
        }
    }

    /**
     *  Used to update the user data, this is very useful when a user wants to change data on his profile
     */
    @Override
    public User updateUserData(UpdateForm user) {
        try {
            jdbc.update(UPDATE_USER_DATA_QUERY, updateUserDataSqlParameterSource(user));
            return getUser(user.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occurred please try again!");
        }
    }

    /**
     * Update user password method when the user has access to his account
     * New password data is being passed from the client as request body and processed here with a check:
     * If the new and the confirmed passwords are identical and if the current password is the same as the current password that has been passed in the client
     * */
    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if(!newPassword.equals(confirmPassword)) { throw new ApiException("Passwords do not match!"); }
        User user = getUser(id);
        if(encoder.matches(currentPassword, user.getPassword())) {
            try {
                jdbc.update(UPDATE_PASSWORD_QUERY, Map.of("password", encoder.encode(newPassword), "userId", user.getId()));
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new ApiException("Something went wrong, please try again!");
            }
        }
        else {
            throw new ApiException("Current password do not match!");
        }
    }

    /**
     * Simple update of users account setting/status
     * */
    @Override
    public void updateSettings(Long userId, Boolean enabled, Boolean notLocked) {
        try {
            jdbc.update(UPDATE_USER_SETTINGS_QUERY, Map.of("userId", userId, "enabled", enabled, "notLocked", notLocked));
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An error occurred please reset your password again!");
        }
    }

    /**
     * Toggling user's MFA with a simple Sql update query and a change in the Users original object
     * */
    @Override
    public User updateMfa(String email) {
        User user = getUserByEmail(email);
        if(user.getPhone().isBlank()) { throw new ApiException("You need to have a phone number to use MFA!"); }
        user.setUsingMfa(!user.isUsingMfa());
        try {
            jdbc.update(UPDATE_USER_MFA_QUERY, Map.of("email", email, "isUsingMfa", user.isUsingMfa()));
            return user;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("Could not update MFA!");
        }
    }

    /**
     * Method to process and save the image coming from the client side.
     * */
    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        String imageUrl = setImageUrl(user.getEmail());
        saveImage(user.getEmail(), image);
        jdbc.update(UPDATE_USER_IMAGE_QUERY, Map.of("userId", user.getId(), "imageUrl", imageUrl));
    }

    /**
     * Pattern for: Creating a directory for user's profile images, and saving those images there.
     * Using the Path interface to set a path for the photo
     * If the path does not exist we create one
     * Then we copy the image bytes into the path, and replace existing image if there is one.
     * The image is stored on this device, and the url that is stored in the DB has access to this image
     *  */
    private void saveImage(String email, MultipartFile image) {
        Path path = Paths.get(System.getProperty("user.home") + "/berbogram/images").toAbsolutePath().normalize();
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException("Could not create a directory for updating the image!");
            }
            log.info("Directory created! {}", path);
        }
        try {
            Files.copy(image.getInputStream(), path.resolve(email + ".png"), REPLACE_EXISTING);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Could not create a directory for updating the image!");
        }
        log.info("File saved in {}", path);
    }

    /**
     * Setting the image URL to be saved in the database
     * */
    private String setImageUrl(String email) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/" + email + ".png").toUriString();
    }

    /**
        Returns true if the statement is true with the url that has been passed on in the method
     */
    private Boolean isURLExpired(String url) {
        try {
            return jdbc.queryForObject(SELECT_PASSWORD_RESET_URL_EXPIRATION_QUERY, Map.of("url", getVerificationUrl(url, VerificationType.PASSWORD.getType())), Boolean.class);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new ApiException("Code is invalid, please reset your password again!");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An error occurred please reset your password again!");
        }
    }

    /**
        Simple check if the verification MFA code is expired.
     */
    private Boolean isVerificationCodeExpired(String code) {
        try {
            return jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code), Boolean.class);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            throw new ApiException("Code is not valid, please login again!");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiException("An error occurred!");
        }
    }

    /**
        Get the email count of a specific email.
        This method is used in the register method to check if the email that was entered for creating a new user already exists.
        If it exists, of course the app will tell the user the corresponding message.
     */
    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(EMAIL_COUNT_QUERY, Map.of("email", email), Integer.class);
    }

    /**
        These are the parameters that are passed into the register method as a form from the frontend.
        The required information for creating a new user.
     */
    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
                //.addValue("enabled", 1);
    }

    /**
     These are the parameters that are passed into the updateUser method as a form from the frontend.
     The required information for updating the user info.
     */
    private SqlParameterSource updateUserDataSqlParameterSource(UpdateForm user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("phone", user.getPhone())
                .addValue("title", user.getTitle())
                .addValue("bio", user.getBio())
                .addValue("address", user.getAddress());
    }

    /**
        This is the generated link for the confirmation of the email when the user registers.
        If this does not go through the user will not be able to log in.
     */
    private String getVerificationUrl(String key, String type) {
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/" + type + "/" + key).toUriString();
        } catch (EmptyResultDataAccessException e) {
            log.error("No data returned");
            throw new ApiException("No data returned");
        } catch (Exception e) {
            log.error("An error occurred!");
            throw new ApiException("An error occurred!");
        }
    }
}
