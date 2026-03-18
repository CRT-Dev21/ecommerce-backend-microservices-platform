package com.ecommerce.crtdev.auth_service.service;

import com.ecommerce.crtdev.auth_service.dto.UserRegisterRequest;
import com.ecommerce.crtdev.auth_service.entity.Role;
import com.ecommerce.crtdev.auth_service.entity.User;
import com.ecommerce.crtdev.auth_service.exception.custom.EmailAlreadyExistsException;
import com.ecommerce.crtdev.auth_service.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public void registerUser(UserRegisterRequest userRegister){
        if(userRepository.existsByEmail(userRegister.email())){
            throw new EmailAlreadyExistsException("This email is already registered","AUTH_EMAIL_DUPLICATED", HttpStatus.CONFLICT);
        }
        User user = new User(userRegister.name(),
                userRegister.email(),
                encoder.encode(userRegister.password()), Set.of(Role.USER));

        userRepository.save(user);
    }
}
