package com.choi.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.domain.MiaoshaOrder;
import com.choi.miaosha.redis.RedisService;
import com.choi.miaosha.service.GoodsService;
import com.choi.miaosha.service.MiaoshaService;
import com.choi.miaosha.service.OrderService;
import com.choi.miaosha.vo.GoodsVo;

@Service
public class MQReceviver {
	
	@Autowired
	private RedisService redisService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private MiaoshaService miaoshaService;
	
	
	private static Logger logger = LoggerFactory.getLogger(MQReceviver.class);
	
	@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
	public void receive(String message){
		logger.info("receive message:" + message);
		MiaoshaMessage miaoshaMessage = RedisService.stringToBean(message, MiaoshaMessage.class);
		//获取秒杀信息 用户及秒杀商品id
		MiaoShaUser user = miaoshaMessage.getUser();
		long goodsId = miaoshaMessage.getGoodsId();
		//根据秒杀商品id查询数据库，获取商品
		GoodsVo goodsVo = goodsService.getGoodVoByGoodsId(goodsId);
		//根据商品获取商品的库存
		int stock = goodsVo.getGoodsStock();
		
		//如果库存小于等于0，直接返回
		if(stock <= 0){
			return;
		}
		//判断是否已经秒杀过了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null){
			return;
		}
		//如果没有秒杀过，则减少困存，生成秒杀订单并写入
		miaoshaService.miaosha(user, goodsVo);
	}
	
	
//	@RabbitListener(queues=MQConfig.QUEUE)
//	public void receive(String message){
//		logger.info("receive message" + message);
//	}
//	
//	@RabbitListener(queues=MQConfig.TOPIC_Queue1)
//	public void receiveTopic1(String message){
//		logger.info("topic queue1 receive message" + message);
//	}
//	@RabbitListener(queues=MQConfig.TOPIC_Queue2)
//	public void receiveTopic2(String message){
//		logger.info("topic queue2 receive message" + message);
//	}
//	
//	@RabbitListener(queues=MQConfig.HEADERS_Queue)
//	public void receiveHeader(byte[] message){
//		logger.info("header queue receive message" + new String(message));
//	}

}
