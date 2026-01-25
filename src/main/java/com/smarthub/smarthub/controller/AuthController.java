package com.smarthub.smarthub.controller;

import com.smarthub.smarthub.domain.Users;
import com.smarthub.smarthub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Users register(@RequestBody Users user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public Users login(@RequestBody Users user) {
        return authService.login(user);
    }
}

