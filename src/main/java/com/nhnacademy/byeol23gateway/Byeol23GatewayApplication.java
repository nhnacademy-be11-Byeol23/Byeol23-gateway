package com.nhnacademy.byeol23gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Byeol23GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(Byeol23GatewayApplication.class, args);
    }

}
