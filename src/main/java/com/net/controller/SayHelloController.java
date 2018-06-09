package com.net.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SayHelloController {

    @RequestMapping(value = "/sayHelloWorld", method = RequestMethod.GET)
    public String sayHelloWorld(@RequestParam(name = "name", required = false, defaultValue = "HelloWorld") String name,
                                Model model) {
        model.addAttribute("name", name);

        return "helloworld";
    }
}
