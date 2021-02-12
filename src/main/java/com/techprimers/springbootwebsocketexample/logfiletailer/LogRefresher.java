package com.techprimers.springbootwebsocketexample.logfiletailer;

import com.techprimers.springbootwebsocketexample.model.UserResponse;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogRefresher {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public void logRefresher() throws Exception {
        //Get the last ten lines from the log file
        File file = new File("xyz.log");
        List<String> temp = new ArrayList<>();
        int n_lines = 10;
        int counter = 0;
        try {
            ReversedLinesFileReader object = new ReversedLinesFileReader(file);
            while (counter < n_lines) {
                temp.add(0, object.readLine());
                counter++;
            }
        } catch (Exception e) {
            System.out.println("error.........");
            return;
        }

        //publish it on the socket
        for (String s : temp) {
            Thread.sleep(10);
            System.out.println(s);
            messagingTemplate.convertAndSend("/topic/log", new UserResponse(s));
        }

        String filePath = "xyz.log";
        List<String> lines = new ArrayList<>();
        LogFileTailer logFileTailer = new LogFileTailer(filePath, file.length() - 1, messagingTemplate);
        Thread thread = new Thread(logFileTailer);
//        thread.setDaemon(true);
        thread.start();
    }
}
