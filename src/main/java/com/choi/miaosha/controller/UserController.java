
package com.choi.miaosha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.redis.RedisService;
import com.choi.miaosha.result.Result;
import com.choi.miaosha.service.GoodsService;
import com.choi.miaosha.service.MiaoShaUserService;
import com.choi.miaosha.vo.GoodsVo;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private MiaoShaUserService userService;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	RedisService redisService;
	
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoShaUser> list(Model model,MiaoShaUser user) {
        return Result.sucess(user);
    }
    
}
