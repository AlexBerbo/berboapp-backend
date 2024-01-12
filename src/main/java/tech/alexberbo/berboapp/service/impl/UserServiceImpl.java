package tech.alexberbo.berboapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.alexberbo.berboapp.dto.UserDTO;
import tech.alexberbo.berboapp.exception.CodeExpiredException;
import tech.alexberbo.berboapp.exception.EmailDoesNotExistException;
import tech.alexberbo.berboapp.exception.EmailExistsException;
import tech.alexberbo.berboapp.exception.PasswordResetCodeExpiredException;
import tech.alexberbo.berboapp.form.UpdateForm;
import tech.alexberbo.berboapp.model.Role;
import tech.alexberbo.berboapp.model.User;
import tech.alexberbo.berboapp.repository.RoleRepository;
import tech.alexberbo.berboapp.repository.UserRepository;
import tech.alexberbo.berboapp.service.UserService;

import static tech.alexberbo.berboapp.dtomapper.UserDTOMapper.fromUser;

/**
    This is implementing the UserService which is just calling the UserRepository implementation where all the logic is actually done.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createUser(User user) throws EmailExistsException {
        return mapToUserDTO(userRepository.register(user));
    }
    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        userRepository.sendVerificationCode(user);
    }

    @Override
    public UserDTO verifyCode(String email, String code) throws CodeExpiredException {
        return mapToUserDTO(userRepository.verifyCode(email, code));
    }

    @Override
    public void resetPassword(String email) throws EmailDoesNotExistException {
        userRepository.resetPassword(email);
    }

    @Override
    public UserDTO verifyVerificationURL(String code) throws PasswordResetCodeExpiredException {
        return mapToUserDTO(userRepository.verifyVerificationURL(code));
    }

    @Override
    public void renewPassword(String url, String password, String confirmPassword) {
        userRepository.renewPassword(url, password, confirmPassword);
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
        userRepository.updatePassword(id, currentPassword, newPassword, confirmPassword);
    }

    @Override
    public void updateRole(Long id, String roleName) {
        roleRepository.updateRole(id, roleName);
    }

    @Override
    public void updateSettings(Long userId, Boolean enabled, Boolean notLocked) {
        userRepository.updateSettings(userId, enabled, notLocked);
    }

    @Override
    public UserDTO updateMfa(String email) {
        return mapToUserDTO(userRepository.updateMfa(email));
    }

    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        userRepository.updateImage(user, image);
    }

    @Override
    public UserDTO verifyAccount(String key) {
        return mapToUserDTO(userRepository.verifyAccount(key));
    }

    @Override
    public UserDTO updateUser(UpdateForm user) {
        return mapToUserDTO(userRepository.updateUserData(user));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return mapToUserDTO(userRepository.getUser(userId));
    }

    private UserDTO mapToUserDTO(User user) {
        return fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
