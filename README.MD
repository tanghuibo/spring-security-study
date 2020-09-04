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

## oauth2 服务器配置

按上述方式配置用户

配置好用户后配置 oauth2 client 信息

```java
package io.github.tanghuibo.springsecuritystudy.oauth.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanghuibo
 * @date 2020/8/22下午10:06
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Resource
    UserDetailsService userDetailsService;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    AuthenticationManager authenticationManager;


    @Qualifier("myJwtAccessTokenConverter")
    @Autowired
    JwtAccessTokenConverter accessTokenConverter;
    @Resource
    TokenStore tokenStore;
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //这里client使用存在模式，可以实际过程调整为jdbc的方式
        //这里说明一下，redirectUris的连接可以是多个，这里通过access_token都可以访问的
        //简单点，就是授权的过程
        clients.inMemory()
                .withClient("client1")
                .secret(passwordEncoder.encode("test"))
                .authorizedGrantTypes("authorization_code", "password", "implicit", "refresh_token", "client_credentials")
                .scopes("All")
                .autoApprove(true)
                .redirectUris("http://127.0.0.1:8082/login", "http://127.0.0.1:9001/login");
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        //权限控制
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        //认证体系使用security的方式
        endpoints.authenticationManager(authenticationManager);
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
        endpoints.userDetailsService(userDetailsService);
        endpoints.tokenStore(tokenStore);
        endpoints.accessTokenConverter(accessTokenConverter);
        endpoints.authorizationCodeServices(authorizationCodeServices());
    }


    @Bean
    AuthorizationCodeServices authorizationCodeServices() {
        // 用 map 模拟 DB，后续我们可以将数据放到 数据库 或 redis
        Map<String, byte[]> data = new ConcurrentHashMap<>(100);
        return new RandomValueAuthorizationCodeServices() {

            @Override
            protected void store(String code, OAuth2Authentication authentication) {
                byte[] serialize = SerializationUtils.serialize(authentication);
                data.put(code, serialize);
            }

            @Override
            protected OAuth2Authentication remove(String code) {
                byte[] bytes = data.remove(code);
                if(bytes == null) {
                    return null;
                }
                return SerializationUtils.deserialize(bytes);
            }
        };
    }
}
```

注意事项: oauth2 server 需要配置一下 `context-path` (应该是个BUG...)

## oauth2 client 配置

启动类添加 EnableOAuth2Sso
```java
@SpringBootApplication
@EnableOAuth2Sso
public class Oauth2ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ClientApplication.class, args);
    }
}
```
配置 oauth 认证服务器信息

```yaml
security:
  oauth2:
    client:
      client-id: client1
      client-secret: test
      access-token-uri: http://127.0.0.1:8081/serve/oauth/token
      user-authorization-uri: http://127.0.0.1:8081/serve/oauth/authorize
    resource:
      jwt:
        key-value: test123
```

## oauth 

1. 打开 

```text
http://127.0.0.1:8081/serve/oauth/authorize?response_type=code&client_id=client1&redirect_uri=http://127.0.0.1:8082/login
```

2. 浏览器会自动跳转到登录页

```text
http://127.0.0.1:8081/serve/login
```

3. 登录跳转到

```text
http://127.0.0.1:8082/login?code=Al6Mpn
```

4. 此时客户端拿着 code 以及客户端 appKey 获取 token

```bash
curl --location --request GET 'http://localhost:8081/serve/oauth/token?grant_type=authorization_code&code=Al6Mpn&redirect_uri=http://127.0.0.1:8082/login' \
--header 'Authorization: Basic Y2xpZW50MTp0ZXN0' \
```

oauth 任务服务器默认对客户端开启 basic 登录验证

basic登录验证方式

添加header 

|key|Authorization|
| --- | --- |
|value|Basic Base64.encode(username:password)|

 

