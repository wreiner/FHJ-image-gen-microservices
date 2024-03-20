package at.wreiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IngressServiceApplication {
    private static Logger logger = LoggerFactory.getLogger(IngressServiceApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(IngressServiceApplication.class, args);
        logger.info("IngressServiceApplication started successfully with Log4j2 configuration");
    }
}