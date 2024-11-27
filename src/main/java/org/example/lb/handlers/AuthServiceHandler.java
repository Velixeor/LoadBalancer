package org.example.lb.handlers;


import org.example.lb.entity.ServiceConnection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class AuthServiceHandler  implements ServiceHandler {
    private final ConcurrentHashMap<String, String> authServices = new ConcurrentHashMap<>();

    @Override
    public void handle(List<ServiceConnection> services) {
        for (ServiceConnection service : services) {
            authServices.put(service.getTypeService().toString(), service.getAddress());
        }
    }

    public String getAuthService(String name) {
        return authServices.get(name);
    }
    public List<String> getAllAddresses() {
        return new ArrayList<>(authServices.values());
    }
}
