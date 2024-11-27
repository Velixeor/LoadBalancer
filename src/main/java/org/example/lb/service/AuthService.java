package org.example.lb.service;


import lombok.extern.slf4j.Slf4j;
import org.example.lb.handlers.AuthServiceHandler;
import org.example.lb.handlers.BillingServiceHandler;
import org.example.lb.handlers.ServiceHandler;
import org.example.lb.handlers.ServiceHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class AuthService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServiceHandlerContext serviceHandlerContext;
    @Autowired
    private DataConnectionService dataConnectionService;
    int numberService=0;

    public String getToken(String login,String password){
        String url = getAdress() + "/auth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("login", login);
        params.add("password", password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
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

    String getAdress(){
        ServiceHandler authHandler = serviceHandlerContext.getHandler("AUTH");
        if (  authHandler instanceof AuthServiceHandler) {
            AuthServiceHandler specificHandler = (AuthServiceHandler)  authHandler;
            List<String> addresses = specificHandler.getAllAddresses();
            if (addresses.isEmpty()) {
                throw new IllegalStateException("No billing services available");
            }
            boolean health = false;
            String address = "";
            int attempts = 0;
            int maxAttempts = addresses.size();
            while (!health && attempts < maxAttempts) {
                if (numberService >= addresses.size()) {
                    numberService = 0;
                }

                address = addresses.get(numberService);
                numberService++;
                attempts++;
                health = dataConnectionService.healthCheck(address);
            }
            if (!health) {
                throw new IllegalStateException("No available services");
            }
            return address;
        } else {
            throw new IllegalStateException("Handler for Billing is not of expected type AuthServiceHandler");
        }
    }
}
