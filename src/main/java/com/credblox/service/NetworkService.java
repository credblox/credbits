package main.java.com.credblox.service;

import main.java.com.credblox.util.ProcessUtil;

public class NetworkService {

    public static void start() {
        ProcessBuilder processBuilder = new ProcessBuilder(System.getProperty("user.dir")+"/network/start.sh");
        ProcessUtil.run(processBuilder);
    }

    public static void stop() {
        ProcessBuilder processBuilder = new ProcessBuilder(System.getProperty("user.dir")+"/network/stop.sh");
        ProcessUtil.run(processBuilder);
    }

    public static void teardown() {
        ProcessBuilder processBuilder = new ProcessBuilder(System.getProperty("user.dir")+"/network/teardown.sh");
        ProcessUtil.run(processBuilder);
    }
}
