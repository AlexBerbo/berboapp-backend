package tech.alexberbo.berboapp.constant.query;

import org.springframework.jdbc.core.PreparedStatementCreator;

/**
 Queries that are executed in the Repository implementation package and classes
 */
public interface UserQuery {
    String EMAIL_COUNT_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    String INSERT_USER_QUERY = "INSERT INTO Users (first_name, last_name, email, password) VALUES (:firstName, :lastName, :email, :password)";
    String INSERT_USER_QUERY_2 = "INSERT INTO Users (first_name, last_name, email, password, enabled) VALUES (:firstName, :lastName, :email, :password, :enabled)";
    String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = "INSERT INTO AccountVerifications (user_id, url) VALUES (:userId, :url)";
    String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM Users WHERE email = :email";
    String DELETE_FROM_ACCOUNT_VERIFICATION_QUERY = "DELETE FROM TwoFactorVerifications WHERE user_id = :userId";
    String INSERT_ACCOUNT_VERIFICATION_CODE_QUERY = "INSERT INTO TwoFactorVerifications (user_id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";
    String SELECT_USER_BY_CODE_QUERY = "SELECT * FROM Users WHERE user_id = (SELECT user_id FROM TwoFactorVerifications WHERE code = :code)";
    String DELETE_CODE_QUERY = "DELETE FROM TwoFactorVerifications WHERE code = :code";
    String SELECT_CODE_EXPIRATION_QUERY = "SELECT expiration_date < NOW() FROM TwoFactorVerifications WHERE code = :code";
    String DELETE_PASSWORD_VERIFICATION_URL_BY_USER_ID = "DELETE FROM PasswordResetVerifications WHERE user_id = :userId";
    String INSERT_PASSWORD_VERIFICATION_URL = "INSERT INTO PasswordResetVerifications (user_id, url, expiration_date) VALUES(:userId, :url, :expirationDate)";
    String SELECT_PASSWORD_RESET_URL_EXPIRATION_QUERY = "SELECT expiration_date < NOW() FROM PasswordResetVerifications WHERE url = :url";
    String SELECT_USER_BY_PW_RESET_URL_QUERY = "SELECT * FROM Users WHERE user_id = (SELECT user_id FROM PasswordResetVerifications WHERE url = :url)";
    String UPDATE_USR_PASSWORD_BY_URL_QUERY = "UPDATE Users SET password = :password WHERE user_id = (SELECT user_id FROM PasswordResetVerifications WHERE url = :url)";
    String DELETE_PASSWORD_URL_QUERY = "DELETE FROM PasswordResetVerifications WHERE url = :url";
    String SELECT_USER_BY_ACC_URL_QUERY = "SELECT * FROM Users WHERE user_id = (SELECT user_id FROM AccountVerifications WHERE url = :url)";
    String UPDATE_USER_ENABLED_QUERY = "UPDATE Users SET enabled = :enabled WHERE user_id = :userId";
    String UPDATE_USER_DATA_QUERY = "UPDATE Users SET first_name = :firstName, last_name = :lastName, email = :email, phone = :phone, title = :title, bio = :bio, address = :address WHERE user_id = :id";
    String SELECT_USER_BY_ID_QUERY = "SELECT * FROM Users WHERE user_id = :id";
}
