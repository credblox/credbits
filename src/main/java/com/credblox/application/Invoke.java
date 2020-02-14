package com.credblox.application;
import com.credblox.service.ChaincodeService;

public class Invoke {

    public static void main(String[] args) {
        System.out.println("Response:");
        System.out.println(ChaincodeService.invoke(args[0], args[1]));
    }
}
