package com.choi.miaosha.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import com.choi.miaosha.result.CodeMsg;
import com.choi.miaosha.result.Result;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

	public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
		e.printStackTrace();
		if(e instanceof GlobalException){
			GlobalException exception = (GlobalException) e;
			return Result.error(exception.getCodeMsg());
		}else if(e instanceof BindException){
			BindException bindException = (BindException) e;
			List<ObjectError> errors = bindException.getAllErrors();
			ObjectError error = errors.get(0);
			String message = error.getDefaultMessage();
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(message));
		}else{
			return Result.error(CodeMsg.SERVER_ERROR);
		}
	}
}
