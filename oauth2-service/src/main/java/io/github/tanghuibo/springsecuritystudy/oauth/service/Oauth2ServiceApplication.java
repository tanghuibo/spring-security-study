package io.github.tanghuibo.springsecuritystudy.oauth.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

/**
 * @author tanghuibo
 * @date 2020/8/19下午11:13
 */
@SpringBootApplication
@EnableOAuth2Sso
public class Oauth2ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ServiceApplication.class, args);
    }
}
