package org.example.lb.service;


import lombok.extern.slf4j.Slf4j;
import org.example.lb.repository.ServiceConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class SystemService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServiceConnectionRepository serviceConnectionRepository;


}
