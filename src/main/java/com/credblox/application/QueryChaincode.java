package com.credblox.application;
import com.credblox.service.ChaincodeService;

public class QueryChaincode {

    public static void main(String[] args) {
        System.out.println("Response:");
        System.out.println(ChaincodeService.query(args[0]));
    }

}
