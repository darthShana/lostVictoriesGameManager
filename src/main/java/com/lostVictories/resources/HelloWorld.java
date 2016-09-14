package com.lostVictories.resources;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/helloWorld")
public class HelloWorld {

	@RequestMapping(path="", method=GET, produces=TEXT_PLAIN_VALUE)
	public String sayPlainTextHello(){
		return "Hello World";
	}
}
