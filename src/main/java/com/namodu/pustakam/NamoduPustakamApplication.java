package com.namodu.pustakam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by sanemdeepak on 7/14/16.
 */
@SpringBootApplication
@ComponentScan("com.namodu.pustakam")
public class NamoduPustakamApplication {
    public static void main(String[] args) {
        SpringApplication.run(NamoduPustakamApplication.class);
    }
}
