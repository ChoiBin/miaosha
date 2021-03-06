package com.choi.miaosha.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choi.miaosha.dao.OrderDao;
import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.domain.MiaoshaOrder;
import com.choi.miaosha.domain.OrderInfo;
import com.choi.miaosha.redis.OrderKey;
import com.choi.miaosha.redis.RedisService;
import com.choi.miaosha.vo.GoodsVo;

@Service
public class OrderService {

	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private RedisService redisService;
	
	public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId,long goodsId){
		return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+ userId + "_" + goodsId  , MiaoshaOrder.class);
	}
	
	public OrderInfo getOrderById(long orderId){
		return orderDao.getOrderById(orderId);
	}
	
	@Transactional
	public OrderInfo createOrder(MiaoShaUser user, GoodsVo goods){
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId());
		long orderId = orderDao.insert(orderInfo);
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderId);
		miaoshaOrder.setUserId(user.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		
		redisService.set(OrderKey.getMiaoshaOrderByUidGid,""+ user.getId() + "_" + goods.getId(), miaoshaOrder);
		return orderInfo;
	}
	
}
