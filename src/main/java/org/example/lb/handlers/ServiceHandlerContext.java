package org.example.lb.handlers;


import org.example.lb.entity.ServiceConnection;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class ServiceHandlerContext {

    private final Map<String, ServiceHandler> handlers = new HashMap<>();

    public ServiceHandlerContext(List<ServiceHandler> handlerList) {
        for (ServiceHandler handler : handlerList) {
            if (handler instanceof AuthServiceHandler) {
                handlers.put("AUTH", handler);
            } else if (handler instanceof CoreServiceHandler) {
                handlers.put("CORE", handler);
            }else if (handler instanceof BillingServiceHandler) {
                handlers.put("BILLING", handler);
            }
        }
    }

    public void handleServices(String type, List<ServiceConnection> services) {
        ServiceHandler handler = handlers.get(type);
        if (handler != null) {
            handler.handle(services);
        } else {
            throw new IllegalArgumentException("Handler for type " + type + " not found");
        }
    }

    public ServiceHandler getHandler(String type) {
        ServiceHandler handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("Handler for type " + type + " not found");
        }
        return handler;
    }
}
