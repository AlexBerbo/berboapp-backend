CREATE SCHEMA IF NOT EXISTS berboapp;

 SET NAMES 'UTF8MB4';
-- SET TIME_ZONE = 'Europe/Berlin';
-- SET TIME_ZONE = '+2:00';

DROP TABLE IF EXISTS berboapp.Users;

CREATE TABLE berboapp.Users
(
        user_id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
        first_name   VARCHAR(50) NOT NULL,
        last_name    VARCHAR(50) NOT NULL,
        email        VARCHAR(100) NOT NULL,
        password     VARCHAR(255) DEFAULT NULL,
        address      VARCHAR(255) DEFAULT NULL,
        phone        VARCHAR(30) DEFAULT NULL,
        title        VARCHAR(50) DEFAULT NULL,
        bio          VARCHAR(255) DEFAULT NULL,
        enabled      BOOLEAN DEFAULT FALSE,
        non_locked   BOOLEAN DEFAULT TRUE,
        using_mfa    BOOLEAN DEFAULT FALSE,
        created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
        image_url    VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
        CONSTRAINT UQ_Users_Email UNIQUE (email)
);

DROP TABLE IF EXISTS berboapp.Roles;

CREATE TABLE berboapp.Roles
(
    role_id    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    permission VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_Roles_Name UNIQUE (name)
);

DROP TABLE IF EXISTS berboapp.UserRoles;

CREATE TABLE berboapp.UserRoles
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT UNSIGNED NOT NULL,
    role_id     BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles (role_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT UQ_UserRole_User_Id UNIQUE (user_id)
);

DROP TABLE IF EXISTS berboapp.Events;

CREATE TABLE berboapp.Events
(
    event_id    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(50) NOT NULL CHECK(type IN ('LOGIN_ATTEMPT',
                                             'LOGIN_ATTEMPT_FAILURE',
                                             'LOGIN_ATTEMPT_SUCCESS',
                                             'PROFILE_UPDATE',
                                             'PROFILE_PICTURE_UPDATE',
                                             'ROLE_UPDATE',
                                             'ACCOUNT_SETTINGS_UPDATE',
                                             'PASSWORD_UPDATE',
                                             'MFA_UPDATE')),
    description VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_Events_Type UNIQUE (type)
);

DROP TABLE IF EXISTS berboapp.UserEvents;

CREATE TABLE berboapp.UserEvents
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT UNSIGNED NOT NULL,
    event_id    BIGINT UNSIGNED NOT NULL,
    device      VARCHAR(100) DEFAULT NULL,
    ip_address  VARCHAR(50) DEFAULT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Events (event_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

DROP TABLE IF EXISTS berboapp.AccountVerifications;

CREATE TABLE berboapp.AccountVerifications
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT UNSIGNED NOT NULL,
    url         VARCHAR(255) NOT NULL,
    -- date add logic later
    FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_AccountVerifications_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_AccountVerifications_Url UNIQUE (url)
);

DROP TABLE IF EXISTS berboapp.PasswordResetVerifications;

CREATE TABLE berboapp.PasswordResetVerifications
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    url             VARCHAR(255) NOT NULL,
    expiration_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_PasswordResetVerifications_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_PasswordResetVerifications_Url UNIQUE (url)
);

DROP TABLE IF EXISTS berboapp.TwoFactorVerifications;

CREATE TABLE berboapp.TwoFactorVerifications
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL,
    code            VARCHAR(10) NOT NULL,
    expiration_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_TwoFactorVerifications_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_TwoFactorVerifications_Code UNIQUE (code)
);