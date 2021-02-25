package ctp.thostmduserapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    static {
        System.loadLibrary("thostmduserapi_se");
        System.loadLibrary("thostmduserapi_wrap");
    }
}
