package org.example.lb.service;


import lombok.extern.slf4j.Slf4j;
import org.example.lb.handlers.AuthServiceHandler;
import org.example.lb.handlers.BillingServiceHandler;
import org.example.lb.handlers.ServiceHandler;
import org.example.lb.handlers.ServiceHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Slf4j
@Service
public class BillingService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServiceHandlerContext serviceHandlerContext;
    @Autowired
    private AuthService authService;
    @Autowired
    private DataConnectionService dataConnectionService;
    int numberService=0;

    public byte[] generatePdf(String login,String password) {
        String url = getAdress() + "/reports/commissions/pdf";
        String token = authService.getToken(login,password);
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
    String getAdress(){
        ServiceHandler billingHandler = serviceHandlerContext.getHandler("BILLING");
        if ( billingHandler instanceof BillingServiceHandler) {
            BillingServiceHandler specificHandler = (BillingServiceHandler) billingHandler;
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
