package com.smarthub.smarthub.service;

import com.smarthub.smarthub.config.exception.AppException;
import com.smarthub.smarthub.domain.Users;
import com.smarthub.smarthub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng"));
    }

    @Transactional
    public Users createUser(Users user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AppException("Email đã tồn tại");
        }
        return userRepository.save(user);
    }

    @Transactional
    public Users updateUser(Long id, Users userReq) {
        Users user = getUserById(id);

        if (!user.getEmail().equals(userReq.getEmail()) &&
                userRepository.findByEmail(userReq.getEmail()).isPresent()) {
            throw new AppException("Email đã tồn tại");
        }

        user.setEmail(userReq.getEmail());
        user.setPassword(userReq.getPassword());
        user.setFullName(userReq.getFullName());
        user.setRole(userReq.getRole());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        Users user = getUserById(id);
        userRepository.delete(user);
    }

    public List<Users> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }
        return userRepository.searchUsers(keyword);
    }
}
