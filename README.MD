# spring security study

spring security 框架学习

## 项目介绍

- normal-client 使用普通的 spring security 登录方式
- oauth2-service oauth2 验证服务器
- oauth2-client 使用 oauth2 进行单点登录

## 动态用户

自定义 UserDetailsService, 通过用户名获取用户信息


```java
@Bean
UserDetailsService buildUserDetailsService() {
    return username -> "admmin".equals(username) ? User.withUsername(username)
            .password(BCrypt.hashpw("admin123", BCrypt.gensalt()))
            .roles("admin")
            .authorities("admin")
            .build() : null;
}
```

配置密码加密解密器

推荐使用 `BCryptPasswordEncoder`
```java
@Bean
PasswordEncoder buildPasswordEncoder() {
    return new BCryptPasswordEncoder();
}
```

完成以上配置我们就可以用 `admin` 和 `admin123` 登录系统了。

