package tech.alexberbo.berboapp.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tech.alexberbo.berboapp.dto.UserDTO;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static tech.alexberbo.berboapp.dtomapper.UserDTOMapper.fromUser;

/**
    User Principal, this is the Spring frameworks user that is customizable.
    This is what the Spring context takes when the user is logging in, so the User class object has to be transferred to this User Principal object
 */
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    private final User user;
    private final Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(role.getPermissions().split(",".trim())).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEnabled();
    }

    public UserDTO getUser() {
        return fromUser(this.user, role);
    }
}
