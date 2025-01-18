package org.example.lb.handlers;


import org.example.lb.entity.ServiceConnection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class CoreServiceHandler implements ServiceHandler {

    private final List<String> coreServices = new ArrayList<>();

    @Override
    public void handle(List<ServiceConnection> services) {
        for (ServiceConnection service : services) {
            if (!coreServices.contains(service.getAddress())) {
                coreServices.add(service.getAddress());
            }
        }
    }

    public List<String> getAllAddresses() {
        return new ArrayList<>(coreServices);
    }
}
