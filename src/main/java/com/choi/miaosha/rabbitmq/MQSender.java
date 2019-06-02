package com.choi.miaosha.rabbitmq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choi.miaosha.redis.RedisService;

@Service
public class MQSender {
	
	private static Logger logger = LoggerFactory.getLogger(MQReceviver.class);

	
	@Autowired
	AmqpTemplate amqpTemplate;
	
	
	public void sendMiaoshaMessage(MiaoshaMessage miaoshaMessage){
		String msg = RedisService.beanToString(miaoshaMessage);
		logger.info("send message :" + msg);
		amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
	}
	
	
//	public void send(Object message){
//		String msg = RedisService.beanToString(message);
//		logger.info("send message:" + message);
//		amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
//	}
//	
//	public void sendTopic(Object message){
//		String msg = RedisService.beanToString(message);
//		logger.info("send topic message: " + msg);
//		amqpTemplate.convertAndSend(MQConfig.TOPIC_Exchange,"topic.key1", msg + 1);
//		amqpTemplate.convertAndSend(MQConfig.TOPIC_Exchange,"topic.key2", msg + 2);
//	}
//	public void sendFanout(Object message){
//		String msg = RedisService.beanToString(message);
//		logger.info("send fanout message: " + msg);
//		amqpTemplate.convertAndSend(MQConfig.Fanout_Exchange,"", msg);
//	}
//	public void sendHeader(Object message){
//		String msg = RedisService.beanToString(message);
//		logger.info("send header message: " + msg);
//		MessageProperties properties = new MessageProperties();
//		properties.setHeader("header1", "value1");
//		properties.setHeader("header2", "value2");
//		Message m = new Message(msg.getBytes(), properties);
//		amqpTemplate.convertAndSend(MQConfig.Headers_Exchange,"", m);
//	}
//	
//	
	
	

}
