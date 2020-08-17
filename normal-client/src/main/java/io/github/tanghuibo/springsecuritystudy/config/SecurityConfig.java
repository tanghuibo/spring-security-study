package io.github.tanghuibo.springsecuritystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author tanghuibo
 * @date 2020/8/18上午12:11
 */
@Configuration
public class SecurityConfig {

    @Bean
    UserDetailsService buildUserDetailsService() {
        return username -> User.withUsername(username)
                .password(BCrypt.hashpw("admin123", BCrypt.gensalt()))
                .roles("admin")
                .authorities("admin")
                .build();
    }

    @Bean
    PasswordEncoder buildPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
