package com.net.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SayHelloController {

    @RequestMapping(value = "/sayHelloWorld.do", method = RequestMethod.GET)
    public ModelAndView sayHelloWorld(@RequestParam(name = "name", required = false, defaultValue = "HelloWorld") String name) {
    	ModelAndView view = new ModelAndView();
    	view.addObject("name", name);
    	view.setViewName("helloworld");
        return view;
    }
}
