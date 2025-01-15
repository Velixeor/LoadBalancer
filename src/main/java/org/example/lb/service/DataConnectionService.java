package org.example.lb.service;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.lb.entity.ServiceConnection;
import org.example.lb.handlers.ServiceHandlerContext;
import org.example.lb.repository.ServiceConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DataConnectionService {
    @Autowired
    private ServiceHandlerContext serviceHandlerContext;
    @Autowired
    private ServiceConnectionRepository serviceEntityRepository;
    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void initializeHandlers() {
        List<ServiceConnection> allServices = serviceEntityRepository.findAll();
        Map<String, List<ServiceConnection>> groupedServices = allServices.stream()
                .collect(Collectors.groupingBy(service -> service.getType().toString()));
        groupedServices.forEach((type, services) -> {
            try {
                serviceHandlerContext.handleServices(type, services);
                log.info("Successfully initialized handler for type: {}", type);
            } catch (IllegalArgumentException e) {
                log.warn("No handler found for type: {}", type, e);
            }
        });
        log.info("All service handlers initialized successfully.");
    }

    public boolean healthCheck(String address) {
        String url = address + "/system";
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            return response.getStatusCode().equals(HttpStatus.OK);
        } catch (Exception e) {
            return false;
        }
    }
}
