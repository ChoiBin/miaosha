package com.choi.miaosha.access;

import com.choi.miaosha.domain.MiaoShaUser;

public class UserContext {
	
	private static ThreadLocal<MiaoShaUser> userHolder = new ThreadLocal<MiaoShaUser>();
	
	public static void setUser(MiaoShaUser user){
		userHolder.set(user);
	}
	
	public static MiaoShaUser getUser(){
		return userHolder.get();
	}

}
