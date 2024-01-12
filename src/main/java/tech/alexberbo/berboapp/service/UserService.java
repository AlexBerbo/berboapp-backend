package tech.alexberbo.berboapp.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.CodeExpiredException;
import tech.alexberbo.berboapp.exception.EmailDoesNotExistException;
import tech.alexberbo.berboapp.exception.EmailExistsException;
import tech.alexberbo.berboapp.exception.PasswordResetCodeExpiredException;
import tech.alexberbo.berboapp.form.UpdateForm;
import tech.alexberbo.berboapp.model.User;

public interface UserService {
    UserDTO register(User user) throws EmailExistsException;
    UserDTO getUserByEmail(String email);
    void sendVerificationCode(UserDTO userDTO);
    UserDTO verifyCode(String email, String code) throws CodeExpiredException;
    void resetPassword(String email) throws EmailDoesNotExistException;
    UserDTO verifyVerificationURL(String code) throws PasswordResetCodeExpiredException;
    void resetPassword(Long id, String newPassword, String confirmNewPassword);
    UserDTO verifyAccount(String key);
    UserDTO updateUser(UpdateForm user);
    UserDTO getUserById(Long userId);
    void updatePassword(Long userId, String currentPassword, String newPassword, String confirmPassword);
    void updateRole(Long userId, String roleName);
    void updateSettings(Long userId, Boolean enabled, Boolean notLocked);
    UserDTO updateMfa(String email);
    void updateImage(UserDTO user, MultipartFile image);
    Page<UserDTO> getUsers(int page, int size);
}
