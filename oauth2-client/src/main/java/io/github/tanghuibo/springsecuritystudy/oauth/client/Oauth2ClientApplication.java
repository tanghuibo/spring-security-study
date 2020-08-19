package io.github.tanghuibo.springsecuritystudy.oauth.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * @author tanghuibo
 * @date 2020/8/19下午11:13
 */
@SpringBootApplication
@EnableOAuth2Client
public class Oauth2ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ClientApplication.class, args);
    }
}
