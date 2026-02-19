package com.example.hi_c2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/api/orders")
public class HelloController {

    @GetMapping("/test")
    public String testApi() throws UnknownHostException {
        return "Hi from order: " +
                InetAddress.getLocalHost().getHostName();
    }
}
