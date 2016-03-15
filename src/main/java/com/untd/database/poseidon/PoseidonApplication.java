package com.untd.database.poseidon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PoseidonApplication {

    public static void main(String[] args) {
    	// Disable DNS cache
    	java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
    	java.security.Security.setProperty("networkaddress.cache.negative.ttl" , "0");	
    			
        SpringApplication.run(PoseidonApplication.class, args);
    }

}