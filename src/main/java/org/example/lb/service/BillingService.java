package org.example.lb.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lb.handlers.BillingServiceHandler;
import org.example.lb.handlers.ServiceHandler;
import org.example.lb.handlers.ServiceHandlerContext;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final RestTemplate restTemplate;
    private final SystemService systemService;
    private final AuthService authService;
    private AtomicInteger numberService;

    public byte[] generatePdf(String login, String password) {
        String url = systemService.getAddress("/reports/commissions/pdf","BILLING",numberService);
        String token = authService.getToken(login, password);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to generate PDF. Status code: " + response.getStatusCode());
        }
    }

}
