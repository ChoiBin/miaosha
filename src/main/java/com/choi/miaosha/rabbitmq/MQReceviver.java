package com.choi.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceviver {
	
	private static Logger logger = LoggerFactory.getLogger(MQReceviver.class);
	
	@RabbitListener(queues=MQConfig.QUEUE)
	public void receive(String message){
		logger.info("receive message" + message);
	}

}
