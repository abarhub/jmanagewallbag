package org.jmanagewallbag;

import org.jmanagewallbag.properties.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class JmanagewallbagApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmanagewallbagApplication.class, args);
    }

}
