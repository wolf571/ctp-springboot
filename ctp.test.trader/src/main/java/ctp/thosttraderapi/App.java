package ctp.thosttraderapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    static {
        System.loadLibrary("thosttraderapi_se");
        System.loadLibrary("thosttraderapi_wrap");
    }
}
