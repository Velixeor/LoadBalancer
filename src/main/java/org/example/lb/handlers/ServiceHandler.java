package org.example.lb.handlers;


import org.example.lb.entity.ServiceConnection;

import java.util.List;


public interface ServiceHandler {
    void handle(List<ServiceConnection> services);
}
