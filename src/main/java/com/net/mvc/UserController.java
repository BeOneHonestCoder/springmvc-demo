package com.net.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/user")
public class UserController {
	
	
	@RequestMapping(value="/usernameCheck", method=RequestMethod.POST)
	public @ResponseBody String checkUsername(String username) {
		if("116338@qq.com".equals(username)){
			return "false";
		}
		return "false";
	}

}
