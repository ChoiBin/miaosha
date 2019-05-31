package com.choi.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.choi.miaosha.domain.User;
import com.choi.miaosha.rabbitmq.MQSender;
import com.choi.miaosha.redis.RedisService;
import com.choi.miaosha.redis.UserKey;
import com.choi.miaosha.result.CodeMsg;
import com.choi.miaosha.result.Result;
import com.choi.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class DemoController {
	
	@Autowired
	private MQSender mqSender;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RedisService redisService;

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}
	
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello() {
		return Result.sucess("hello choibin");
		//return new Result(0, "sucess", "hello,choibin");
	}
	
	@RequestMapping("/helloError")
	@ResponseBody
	public Result<String> helloError() {
		return Result.error(CodeMsg.SERVER_ERROR);
	}
	
	
	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model) {
		model.addAttribute("name", "choibin");
		return "hello";
	}
	
	@RequestMapping("/db/get")
	@ResponseBody
	public Result<User> dbGet(){
		User user = userService.getById(1);
		return Result.sucess(user);
	}
	
	@RequestMapping("/db/tx")
	@ResponseBody
	public Result<Boolean> dbTx(){
		userService.tx();
		return Result.sucess(true);
	}
	
	@RequestMapping("/redis/get")
	@ResponseBody
	public Result<User> redisGet(){
		User user = redisService.get(UserKey.getById, "" + 1,User.class);
		return Result.sucess(user);
	}
	
	@RequestMapping("/redis/set")
	@ResponseBody
	public Result<Boolean> redisSet(){
		User user = new User();
		user.setId(1);
		user.setName("choibin");
		redisService.set(UserKey.getById, ""+1, user);
		return Result.sucess(true);
	}
	
	@RequestMapping("/mq")
	@ResponseBody
	public Result<String> mq(){
		mqSender.send("hello,world");
		return Result.sucess("hello world");
	}
	
}
