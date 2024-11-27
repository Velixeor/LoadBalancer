package org.example.lb.controller;


import org.example.lb.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> getToken(@RequestParam("login") String login,
                                                        @RequestParam("password") String password) {
        Map<String, Object> response = new HashMap<>();
        String token = authService.getToken(login, password);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
