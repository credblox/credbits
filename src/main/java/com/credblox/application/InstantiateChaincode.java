package com.credblox.application;

import com.credblox.service.ChaincodeService;

public class InstantiateChaincode {
    public static void main(String[] args) {
        ChaincodeService.instantiate(args[0]);
    }
}
