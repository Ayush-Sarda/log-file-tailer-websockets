package com.techprimers.springbootwebsocketexample.resource;

import com.techprimers.springbootwebsocketexample.logfiletailer.LogRefresher;
import com.techprimers.springbootwebsocketexample.model.User;
import com.techprimers.springbootwebsocketexample.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    LogRefresher logRefresher;

    @MessageMapping("/log")
    @SendTo("/topic/log")
    public void getUser(User user) {
        try {
            logRefresher.logRefresher();
        } catch(Exception e) {
            System.out.println("Caught exception while fetching logs");
        }
    }
}
