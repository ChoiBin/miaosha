package com.choi.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.choi.miaosha.dao.MiaoShaUserDao;
import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.exception.GlobalException;
import com.choi.miaosha.redis.MiaoShaUserKey;
import com.choi.miaosha.redis.RedisService;
import com.choi.miaosha.result.CodeMsg;
import com.choi.miaosha.util.MD5Util;
import com.choi.miaosha.util.UUIDUtil;
import com.choi.miaosha.vo.GoodsVo;
import com.choi.miaosha.vo.LoginVo;

@Service
public class MiaoShaUserService {
	
	public static final String COOKI_NAME_TOKEN = "token";

	@Autowired
	private MiaoShaUserDao miaoShaUserDao;
	
	@Autowired
	private RedisService redisService;
	
	public MiaoShaUser getById(long id){
		//去缓存
		MiaoShaUser user = redisService.get(MiaoShaUserKey.getById, "" + id, MiaoShaUser.class);
		if(user != null){
			return user;
		}
		//取数据库
		user = miaoShaUserDao.getById(id);
		if(user != null){
			redisService.set(MiaoShaUserKey.getById, "" + id, user);
		}
		return user;
	}
	
	public boolean updatePassword(String token, long id, String formPassword){
		//取user
		MiaoShaUser user = getById(id);
		if(user == null){
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//更新数据库
		MiaoShaUser toBeUpdate = new MiaoShaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPassword, user.getSalt()));
		miaoShaUserDao.update(toBeUpdate);
		//更新缓存
		redisService.delete(MiaoShaUserKey.getById,"" + id);
		user.setPassword(toBeUpdate.getPassword());
		redisService.set(MiaoShaUserKey.token, token, user);
		return true;
	}
	
	public MiaoShaUser getByToken(HttpServletResponse response, String token){
		if(StringUtils.isEmpty(token)){
			return null;
		}
		MiaoShaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoShaUser.class);
		//延长有效期
		if(user != null){
			addCookie(response, token, user);
		}
		return user;
	}
	
	
	
	public String login(HttpServletResponse response, LoginVo loginVo){
		if(loginVo == null){
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		//判断手机号码是否存在
		MiaoShaUser user = miaoShaUserDao.getById(Long.parseLong(mobile));
		if(user == null){
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//System.out.println(formPass);
		//验证密码
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
		//System.out.println(calcPass);
		if(!calcPass.equals(dbPass)){
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		//生成cookie
		String token = UUIDUtil.uuid();
		addCookie(response,token,user);
		return token;
	}



	private void addCookie(HttpServletResponse response, String token, MiaoShaUser user) {
		redisService.set(MiaoShaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	
}
