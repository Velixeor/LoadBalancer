package org.example.lb.controller;


import lombok.RequiredArgsConstructor;
import org.example.lb.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    @GetMapping("/token")
    public ResponseEntity<Map<String, Object>> getToken(@RequestParam("login") String login,
                                                        @RequestParam("password") String password) {
        Map<String, Object> response = new HashMap<>();
        String token = authService.getToken(login, password);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
