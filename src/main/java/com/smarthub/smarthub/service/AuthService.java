package com.smarthub.smarthub.service;

import com.smarthub.smarthub.config.exception.AppException;
import com.smarthub.smarthub.domain.Users;
import com.smarthub.smarthub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public Users register(Users user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AppException("Email đã tồn tại");
        }
        user.setRole("USER");
        return userRepository.save(user);
    }

    public Users login(Users userReq) {
        Users user = userRepository.findByEmail(userReq.getEmail())
                .orElseThrow(() -> new AppException("Email không tồn tại"));

        if (!user.getPassword().equals(userReq.getPassword())) {
            throw new AppException("Sai mật khẩu");
        }
        return user;
    }
}

