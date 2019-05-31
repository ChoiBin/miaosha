package com.choi.miaosha.rabbitmq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choi.miaosha.redis.RedisService;

@Service
public class MQSender {
	
	private static Logger logger = LoggerFactory.getLogger(MQReceviver.class);

	
	@Autowired
	AmqpTemplate amqpTemplate;
	
	public void send(Object message){
		String msg = RedisService.beanToString(message);
		logger.info("send message:" + message);
		amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
	}

}
