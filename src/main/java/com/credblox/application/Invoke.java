package com.credblox.application;
import com.credblox.service.ChaincodeService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Invoke {

    public static void main(String[] args) {
        Logger.getRootLogger().setLevel(Level.OFF);
        System.out.println(ChaincodeService.invoke(args[0], args[1]));
    }
}
