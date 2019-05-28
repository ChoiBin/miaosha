package com.choi.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	
	private static final String salt = "1a2b3c4d";
	
	public static String md5(String src){
		return DigestUtils.md5Hex(src);
	}
	
	public static String inputPassToFormPass(String inputPass){
		String string = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
		System.out.println(string);
		return md5(string);
	}
	
	public static String formPassToDBPass(String formPass, String salt){
		String string = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
		return md5(string);
	}
	
	public static String inputPassToDBPass(String inputPass, String saltDB){
		String formPass = inputPassToFormPass(inputPass);
		String dbPass = formPassToDBPass(formPass, saltDB);
		return dbPass;
	}
	

}
