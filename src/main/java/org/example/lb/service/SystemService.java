package org.example.lb.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lb.handlers.BillingServiceHandler;
import org.example.lb.handlers.ServiceHandler;
import org.example.lb.handlers.ServiceHandlerContext;
import org.example.lb.repository.ServiceConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
@RequiredArgsConstructor
public class SystemService {

    private final ServiceHandlerContext serviceHandlerContext;
    private final DataConnectionService dataConnectionService;
    public String getAddress(String supportStr, String type, AtomicInteger numberService) {
        List<String> addresses = getAllAddresses(type);
        String address = getHealthyAddress(addresses,numberService);
        return address + supportStr;
    }

    private List<String> getAllAddresses(String type) {
        ServiceHandler specificHandler = serviceHandlerContext.getHandler(type);
        List<String> addresses = specificHandler.getAllAddresses();
        if (addresses.isEmpty()) {
            throw new IllegalStateException("No billing services available");
        }
        return addresses;
    }

    private String getHealthyAddress(List<String> addresses, AtomicInteger numberService) {
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
