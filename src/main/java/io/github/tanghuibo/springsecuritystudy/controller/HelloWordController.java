package io.github.tanghuibo.springsecuritystudy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghuibo
 * @date 2020/8/3下午11:34
 */
@RequestMapping("helloWord")
@RestController
public class HelloWordController {

    @GetMapping("sayHello")
    public String sayHello(@RequestParam("name") String name) {
        return String.format("hello %s", name);
    }
}
