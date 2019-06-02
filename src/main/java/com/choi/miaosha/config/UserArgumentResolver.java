package com.choi.miaosha.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.choi.miaosha.access.UserContext;
import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.service.MiaoShaUserService;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
	private MiaoShaUserService miaoShaUserService;
	
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return UserContext.getUser();
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		Class<?> clazz = methodParameter.getParameterType();
		return clazz == MiaoShaUser.class;
	}

	
}
