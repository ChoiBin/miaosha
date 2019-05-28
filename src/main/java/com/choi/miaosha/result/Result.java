package com.choi.miaosha.result;

public class Result<T> {

	private int code;
	private String msg;
	private T data;
	public Result(int code, String msg, T data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	private Result(T data) {
		this.code = 0;
		this.msg = "sucess";
		this.data = data;
	}

	private Result(CodeMsg cm) {
		if(cm == null){
			return;
		}
		this.code = cm.getCode();
		this.msg = cm.getMsg();
	}

	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
	public T getData() {
		return data;
	}

	public static <T> Result<T> sucess(T data){
		return new Result<T>(data);
	}
	
	public static <T> Result<T> error(CodeMsg cm){
		return new Result<T>(cm);
	}

}
