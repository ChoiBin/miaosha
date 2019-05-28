package com.choi.miaosha.redis;

public interface KeyPrefix {

	public int expireSeconds();
	
	public String getPrefix();
}
