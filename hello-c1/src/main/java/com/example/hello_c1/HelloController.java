package com.example.hello_c1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/api/users")
public class HelloController {

    @GetMapping("/test")
    public String testApi() throws UnknownHostException {
        return "Hello from: " +
                InetAddress.getLocalHost().getHostName();
    }
}
