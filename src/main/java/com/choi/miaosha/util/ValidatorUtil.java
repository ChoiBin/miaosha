package com.choi.miaosha.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.util.StringUtils;


public class ValidatorUtil {
	
	private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");
	
	public static boolean isMobile(String src){
		if(StringUtils.isEmpty(src)){
			return false;
		}else {
			Matcher matcher = mobile_pattern.matcher(src);
			return matcher.matches();
		}
	}

}
