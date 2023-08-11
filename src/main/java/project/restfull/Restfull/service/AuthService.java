package project.restfull.Restfull.service;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import project.restfull.Restfull.entity.User;
import project.restfull.Restfull.model.LoginUserRequest;
import project.restfull.Restfull.model.TokenResponse;
import project.restfull.Restfull.repository.UserRepository;
import project.restfull.Restfull.security.BCrypt;

import java.util.UUID;

import static project.restfull.Restfull.security.BCrypt.checkpw;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidatorService validatorService;

    @Transactional
    public TokenResponse login(LoginUserRequest request) {

        validatorService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, " username or password wrong"));


        if (checkpw(request.getPassword(),user.getPassword())) {
            // Success
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .Token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            //fail
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, " username or password wrong");

        }
    }

    private Long next30Days() {
        return System.currentTimeMillis() + (1000 * 16 * 24 * 30);
    }

    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }

}
