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
