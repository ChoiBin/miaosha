package com.choi.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.domain.OrderInfo;
import com.choi.miaosha.vo.GoodsVo;

@Service
public class MiaoshaService {

	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private OrderService orderService;
	
	@Transactional
	public OrderInfo miaosha(MiaoShaUser user, GoodsVo goods) {
		//减库存，下订单 写入秒杀订单
		goodsService.reduceStock(goods);
		return orderService.createOrder(user, goods);
	}
}
