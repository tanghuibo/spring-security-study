package io.github.tanghuibo.springsecuritystudy.oauth.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tanghuibo
 * @date 2020/7/12下午11:57
 */
@Order(-Integer.MAX_VALUE)
@Component
public class WebLogHttpFilter extends OncePerRequestFilter {
    Logger logger = LoggerFactory.getLogger(WebLogHttpFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        logger.info("method: {}, url: {}?{}", httpServletRequest.getMethod(), httpServletRequest.getRequestURL(), httpServletRequest.getQueryString());
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
