package project.restfull.Restfull.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.RegisterUserRequest;
import project.restfull.Restfull.model.UpdateUserRequest;
import project.restfull.Restfull.model.UserResponse;
import project.restfull.Restfull.repository.UserRepository;
import project.restfull.Restfull.security.BCrypt;

import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidatorService validatorService;

    @Transactional
    public void register(RegisterUserRequest request) {

        validatorService.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exist");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        validatorService.validate(request);

        log.info("USER {}", request);

        if (Objects.nonNull(request.getName())) {
            user.setName(request.getName());


        }
        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository
                .save(user);

        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername()).build();

    }


}