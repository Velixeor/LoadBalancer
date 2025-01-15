package org.example.lb.handlers;


import org.example.lb.entity.ServiceConnection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class CoreServiceHandler implements ServiceHandler {

    private final ConcurrentHashMap<String, String> coreServices = new ConcurrentHashMap<>();

    @Override
    public void handle(List<ServiceConnection> services) {
        for (ServiceConnection service : services) {
            coreServices.put(service.getType().toString(), service.getAddress());
        }
    }

    public String getCoreService(String name) {
        return coreServices.get(name);
    }
}
