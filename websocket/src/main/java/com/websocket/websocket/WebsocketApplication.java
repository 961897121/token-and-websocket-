package com.websocket.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketApplication.class, args);
        System.out.println("----------------");
        System.out.println("项目启动成功");
        System.out.println("----------------");
    }

}
