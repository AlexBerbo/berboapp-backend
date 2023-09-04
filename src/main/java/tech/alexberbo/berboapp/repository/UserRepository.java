package tech.alexberbo.berboapp.repository;

import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.CodeExpiredException;
import tech.alexberbo.berboapp.exception.EmailDoesNotExistException;
import tech.alexberbo.berboapp.exception.EmailExistsException;
import tech.alexberbo.berboapp.exception.PasswordResetCodeExpiredException;
import tech.alexberbo.berboapp.model.User;

import java.util.Collection;

public interface UserRepository<T extends User> {
    T register(T user) throws EmailExistsException;
    Collection<T> getAllUsers(int page, int pageSize);
    T getUser(Long id);
    T updateUser(T data);
    Boolean deleteUser(Long id);
    User getUserByEmail(String email);
    void sendVerificationCode(UserDTO userDTO);
    User verifyCode(String email, String code) throws CodeExpiredException;
    void resetPassword(String email) throws EmailDoesNotExistException;
    User verifyVerificationURL(String code) throws PasswordResetCodeExpiredException;
    void renewPassword(String url, String password, String confirmPassword);
    User verifyAccount(String key);
}
