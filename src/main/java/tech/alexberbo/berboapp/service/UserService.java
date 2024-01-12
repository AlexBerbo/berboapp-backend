package tech.alexberbo.berboapp.service;

import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.CodeExpiredException;
import tech.alexberbo.berboapp.exception.EmailDoesNotExistException;
import tech.alexberbo.berboapp.exception.EmailExistsException;
import tech.alexberbo.berboapp.exception.PasswordResetCodeExpiredException;
import tech.alexberbo.berboapp.form.UpdateForm;
import tech.alexberbo.berboapp.model.User;

public interface UserService {
    UserDTO createUser(User user) throws EmailExistsException;
    UserDTO getUserByEmail(String email);
    void sendVerificationCode(UserDTO userDTO);
    UserDTO verifyCode(String email, String code) throws CodeExpiredException;
    void resetPassword(String email) throws EmailDoesNotExistException;
    UserDTO verifyVerificationURL(String code) throws PasswordResetCodeExpiredException;
    void renewPassword(String url, String password, String confirmPassword);
    UserDTO verifyAccount(String key);
    UserDTO updateUser(UpdateForm user);
    UserDTO getUserById(Long userId);
}
