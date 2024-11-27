package org.example.lb.handlers;


import org.example.lb.entity.ServiceConnection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class BillingServiceHandler  implements ServiceHandler {
    private final ConcurrentHashMap<String, String> billingServices = new ConcurrentHashMap<>();

    @Override
    public void handle(List<ServiceConnection> services) {
        for (ServiceConnection service : services) {
            billingServices.put(service.getTypeService().toString(), service.getAddress());
        }
    }

    public String getBillingService(String name) {
        return  billingServices.get(name);
    }

    public List<String> getAllAddresses() {
        return new ArrayList<>(billingServices.values());
    }
}
