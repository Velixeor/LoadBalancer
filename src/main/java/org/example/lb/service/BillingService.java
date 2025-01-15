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
    private final ServiceHandlerContext serviceHandlerContext;
    private final AuthService authService;
    private final DataConnectionService dataConnectionService;
    private AtomicInteger numberService;

    public byte[] generatePdf(String login, String password) {
        String url = getAddress("/reports/commissions/pdf");
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

    private String getAddress(String supportStr) {
        List<String> addresses = getAllAddresses();
        String address = getHealthyAddress(addresses);
        return address + supportStr;
    }

    private List<String> getAllAddresses() {
        ServiceHandler billingHandler = serviceHandlerContext.getHandler("BILLING");
        BillingServiceHandler specificHandler = (BillingServiceHandler) billingHandler;
        List<String> addresses = specificHandler.getAllAddresses();
        if (addresses.isEmpty()) {
            throw new IllegalStateException("No billing services available");
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
