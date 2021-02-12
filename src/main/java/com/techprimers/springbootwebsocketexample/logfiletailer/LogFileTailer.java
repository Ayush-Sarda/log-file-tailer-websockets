package com.techprimers.springbootwebsocketexample.logfiletailer;

import com.techprimers.springbootwebsocketexample.model.UserResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class LogFileTailer implements Runnable {
    private boolean debug = false;
    public static List<String> lines;
    private int runTimer = 1000;
    private long lastKnownPosition = 0;
    private boolean shouldIRun = true;
    private File myFile = null;
    private static int counter = 0;

    SimpMessagingTemplate messagingTemplate;

    public LogFileTailer(String filePath, long last, SimpMessagingTemplate messagingTemplate) {
        this.myFile = new File(filePath);
        this.lastKnownPosition = last;
        this.messagingTemplate = messagingTemplate;
    }

    private void printLine(String message) {
        System.out.println(message);
    }

    public void stopRunning() {
        shouldIRun = false;
    }

    @Override
    public void run() {
        try {
            while (shouldIRun) {
                Thread.sleep(runTimer);
                long fileLength = myFile.length();
                if (fileLength > lastKnownPosition) {

                    // Reading and writing file
                    File file = new File("help.log");
                    FileUtils.write(file, "", Charset.defaultCharset());
                    RandomAccessFile readWriteFileAccess = new RandomAccessFile(myFile, "rw");
                    readWriteFileAccess.seek(lastKnownPosition);
                    String crunchifyLine = null;
                    lines = new ArrayList<>();
                    while ((crunchifyLine = readWriteFileAccess.readLine()) != null) {
                        this.printLine(crunchifyLine);
                        lines.add(crunchifyLine);
                        Thread.sleep(10);
                        messagingTemplate.convertAndSend("/topic/log", new UserResponse(crunchifyLine));
                        Files.write(Paths.get("help.log"), (crunchifyLine+"\n").getBytes(), StandardOpenOption.APPEND);
                        counter++;
                    }
                    lastKnownPosition = readWriteFileAccess.getFilePointer();
                    readWriteFileAccess.close();
                } else {
                    if (debug)
                        this.printLine("Hmm.. Couldn't found new line after line # " + counter);
                }
            }
        } catch (Exception e) {
            stopRunning();
        }
        if (debug)
            this.printLine("Exit the program...");
    }
}
