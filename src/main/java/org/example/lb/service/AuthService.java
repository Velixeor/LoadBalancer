package org.example.lb.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lb.handlers.AuthServiceHandler;
import org.example.lb.handlers.ServiceHandler;
import org.example.lb.handlers.ServiceHandlerContext;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final ServiceHandlerContext serviceHandlerContext;
    private final DataConnectionService dataConnectionService;
    private AtomicInteger numberService;

    public String getToken(String login, String password) {
        String url = getAddress("/auth/token");
        ResponseEntity<Map> response = restTemplate.postForEntity(url, prepareRequest(login, password), Map.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            if (body.containsKey("token")) {
                return (String) body.get("token");
            } else {
                throw new RuntimeException("Token not found in the response: " + body);
            }
        } else {
            throw new RuntimeException("Failed to obtain token. Status code: " + response.getStatusCode());
        }
    }

    private HttpEntity<Map<String, String>> prepareRequest(String login, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HashMap<String, String> params = new HashMap<>();
        params.put("login", login);
        params.put("password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
        return request;
    }

    private String getAddress(String supportStr) {
        List<String> addresses = getAllAddresses();
        String address = getHealthyAddress(addresses);
        return address + supportStr;
    }

    private List<String> getAllAddresses() {
        ServiceHandler authHandler = serviceHandlerContext.getHandler("AUTH");
        AuthServiceHandler specificHandler = (AuthServiceHandler) authHandler;
        List<String> addresses = specificHandler.getAllAddresses();
        if (addresses.isEmpty()) {
            throw new IllegalStateException("No auth services available");
        }
        return addresses;
    }

    private String getHealthyAddress(List<String> addresses) {
        int attempts = 0;
        int maxAttempts = addresses.size();

        while (attempts < maxAttempts) {
            int currentIndex = numberService.getAndUpdate(index -> (index + 1) % addresses.size());
            String address = addresses.get(currentIndex);

            if (dataConnectionService.healthCheck(address)) {
                return address;
            }

            attempts++;
        }

        throw new IllegalStateException("No available services");
    }
}
