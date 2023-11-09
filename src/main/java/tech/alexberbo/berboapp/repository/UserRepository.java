package tech.alexberbo.berboapp.repository;

import org.springframework.web.multipart.MultipartFile;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.CodeExpiredException;
import tech.alexberbo.berboapp.exception.EmailDoesNotExistException;
import tech.alexberbo.berboapp.exception.EmailExistsException;
import tech.alexberbo.berboapp.exception.PasswordResetCodeExpiredException;
import tech.alexberbo.berboapp.form.UpdateForm;
import tech.alexberbo.berboapp.model.User;

import java.util.Collection;

public interface UserRepository<T extends User> {
    T register(T user) throws EmailExistsException;
    Collection<T> getAllUsers(int page, int pageSize);
    T getUser(Long id);
    Boolean deleteUser(Long id);
    User getUserByEmail(String email);
    void sendVerificationCode(UserDTO userDTO);
    User verifyCode(String email, String code) throws CodeExpiredException;
    void resetPassword(String email) throws EmailDoesNotExistException;
    User verifyVerificationURL(String code) throws PasswordResetCodeExpiredException;
    void resetPassword(Long userId, String newPassword, String confirmNewPassword);
    User verifyAccount(String key);
    T updateUserData(UpdateForm user);
    void updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword);
    void updateSettings(Long userId, Boolean enabled, Boolean notLocked);
    User updateMfa(String email);
    void updateImage(UserDTO user, MultipartFile image);
}
