package com.choi.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.choi.miaosha.redis.RedisService;
import com.choi.miaosha.result.Result;
import com.choi.miaosha.service.MiaoShaUserService;
import com.choi.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {

	private static Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private MiaoShaUserService miaoShaUserService;
	
	@Autowired
	private RedisService redisService;
	
	@RequestMapping("/to_login")
	public String toLogin(){
		return "login";
	}
	
	@RequestMapping("/do_login")
	@ResponseBody
	public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
		logger.info(loginVo.toString());
		//登录
		String token = miaoShaUserService.login(response, loginVo);
		return Result.sucess(token);
	}
	
}
