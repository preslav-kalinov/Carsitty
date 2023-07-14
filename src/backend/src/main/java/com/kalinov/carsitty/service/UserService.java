package com.kalinov.carsitty.service;

import com.kalinov.carsitty.RoleEnum;
import com.kalinov.carsitty.dao.PartDao;
import com.kalinov.carsitty.dao.RoleDao;
import com.kalinov.carsitty.dao.SaleDao;
import com.kalinov.carsitty.dao.UserDao;
import com.kalinov.carsitty.dto.*;
import com.kalinov.carsitty.entity.Role;
import com.kalinov.carsitty.entity.Sale;
import com.kalinov.carsitty.entity.User;
import com.kalinov.carsitty.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    private final ModelMapperUtil modelMapper;
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PartDao partDao;
    private final SaleDao saleDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(ModelMapperUtil modelMapper, UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder,
                       PartDao partDao, SaleDao saleDao) {
        this.modelMapper = modelMapper;
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.partDao = partDao;
        this.saleDao = saleDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        return this.modelMapper.mapList(this.userDao.findAll(), UserDto.class);
    }

    public AuthenticatedUserDto getAuthenticatedUser(Authentication authentication) {
        User authenticatedUser = this.userDao.getUsersByUsername(authentication.getName()).get(0);
        return this.modelMapper.map(authenticatedUser, AuthenticatedUserDto.class);
    }

    public UserDto getUser(String username) {
        if (this.userDao.getUserCountByUsername(username) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The user '%s' is not found", username));
        }
        return this.modelMapper.map(this.userDao.getUsersByUsername(username).get(0), UserDto.class);
    }

    public void createUser(UserDto userDto, RoleEnum roleEnum) throws MethodArgumentNotValidException, NoSuchMethodException {
        if (this.userDao.getUserCountByUsername(userDto.getUsername()) > 0) {
            throwMethodArgumentNotValidExceptionForTakenUsername(userDto);
        }

        if (this.userDao.getUserCountByEmail(userDto.getEmail()) > 0) {
            throwMethodArgumentNotValidExceptionForTakenEmail(userDto);
        }

        encodePassword(userDto);
        User userToCreate = this.modelMapper.map(userDto, User.class);

        Role role = this.roleDao.findByRole(roleEnum);

        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role specified");
        }

        userToCreate.setRole(role);
        this.userDao.save(userToCreate);
    }

    @Transactional
    public void deleteUser(String username, RoleEnum roleEnum) {
        this.checkUserExistenceAndDesiredRole(username, roleEnum);
        User user = this.userDao.getUserByUsername(username);

        if (!this.partDao.getPartsByUserId(user.getId()).isEmpty() || !this.saleDao.getSalesByUserId(user.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The user '%s' could not be deleted. Only deactivation is applicable.", username));
        }

        this.userDao.deleteUserByUsername(username);
    }

    public void updateUser(String username, RoleEnum roleEnum, UpdatedUserDto updatedUserDto) throws MethodArgumentNotValidException, NoSuchMethodException {
        this.checkUserExistenceAndDesiredRole(username, roleEnum);
        this.updateUserEntityFromUpdatedUserDto(this.userDao.getUsersByUsername(username).get(0), updatedUserDto);
    }

    private void checkUserExistenceAndDesiredRole(String username, RoleEnum roleEnum) {
        List<User> users = this.userDao.getUsersByUsername(username);

        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The user '%s' is not found", username));
        }

        boolean hasDesiredRole = users.stream()
                .anyMatch(user -> user.getRole().getRole() == roleEnum);

        if (!hasDesiredRole) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The user '%s' does not have the desired role '%s'", username, roleEnum));
        }
    }

    private void updateUserEntityFromUpdatedUserDto(User userEntity, UpdatedUserDto updatedUserDto) throws MethodArgumentNotValidException, NoSuchMethodException {
        if (updatedUserDto.getUsername() != null) {
            if (this.userDao.getUserCountByUsername(updatedUserDto.getUsername()) > 0) {
                this.throwMethodArgumentNotValidExceptionForTakenUsernameForUpdatedUser(updatedUserDto);
            }
            userEntity.setUsername(updatedUserDto.getUsername());
        }

        if (updatedUserDto.getDisplayName() != null) {
            userEntity.setDisplayName(updatedUserDto.getDisplayName());
        }

        if (updatedUserDto.getPassword() != null) {
            this.encodePasswordForUpdatedUser(updatedUserDto);
            userEntity.setPassword(updatedUserDto.getPassword());
        }

        if (updatedUserDto.getEmail() != null) {
            userEntity.setEmail(updatedUserDto.getEmail());
        }

        if (updatedUserDto.getRole() != null) {
            Role role = this.roleDao.findByRole(updatedUserDto.getRole());
            userEntity.setRole(role);
        }

        if (updatedUserDto.isEnabled() != null) {
            userEntity.setEnabled(updatedUserDto.isEnabled());
        }

        this.userDao.save(userEntity);
    }

    private void throwMethodArgumentNotValidExceptionForTakenUsername(UserDto userDto)
            throws MethodArgumentNotValidException, NoSuchMethodException {
        BeanPropertyBindingResult bindingResultWithErrors = new BeanPropertyBindingResult(userDto, "userDto");
        FieldError fieldError = new FieldError(
                "userDto",
                "username",
                String.format("The username '%s' is already in use, please use another one", userDto.getUsername())
        );
        bindingResultWithErrors.addError(fieldError);
        MethodParameter throwFieldErrorForClass = new MethodParameter(
                this.getClass().getDeclaredMethod("throwMethodArgumentNotValidExceptionForTakenUsername", UserDto.class),
                0
        );

        throw new MethodArgumentNotValidException(throwFieldErrorForClass, bindingResultWithErrors);
    }

    private void throwMethodArgumentNotValidExceptionForTakenUsernameForUpdatedUser(UpdatedUserDto updatedUserDto)
            throws MethodArgumentNotValidException, NoSuchMethodException {
        BeanPropertyBindingResult bindingResultWithErrors = new BeanPropertyBindingResult(updatedUserDto, "userDto");
        FieldError fieldError = new FieldError(
                "updatedUserDto",
                "username",
                String.format("The username '%s' is already in use, please use another one", updatedUserDto.getUsername())
        );
        bindingResultWithErrors.addError(fieldError);
        MethodParameter throwFieldErrorForClass = new MethodParameter(
                this.getClass().getDeclaredMethod("throwMethodArgumentNotValidExceptionForTakenUsernameForUpdatedUser", UpdatedUserDto.class),
                0
        );

        throw new MethodArgumentNotValidException(throwFieldErrorForClass, bindingResultWithErrors);
    }

    private void throwMethodArgumentNotValidExceptionForTakenEmail(UserDto userDto)
            throws MethodArgumentNotValidException, NoSuchMethodException {
        BeanPropertyBindingResult bindingResultWithErrors = new BeanPropertyBindingResult(userDto, "userDto");
        FieldError fieldError = new FieldError(
                "userDto",
                "email",
                String.format("The email '%s' is already taken, please use another one", userDto.getEmail())
        );
        bindingResultWithErrors.addError(fieldError);
        MethodParameter throwFieldErrorForClass = new MethodParameter(
                this.getClass().getDeclaredMethod("throwMethodArgumentNotValidExceptionForTakenUsername", UserDto.class),
                0
        );
        throw new MethodArgumentNotValidException(throwFieldErrorForClass, bindingResultWithErrors);
    }

    private void encodePassword(UserDto userDto) {
        String encodedPassword = this.passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
    }

    private void encodePasswordForUpdatedUser(UpdatedUserDto updatedUserDto) {
        String encodedPassword = this.passwordEncoder.encode(updatedUserDto.getPassword());
        updatedUserDto.setPassword(encodedPassword);
    }
}