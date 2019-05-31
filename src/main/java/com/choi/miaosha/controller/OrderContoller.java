package com.choi.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.domain.OrderInfo;
import com.choi.miaosha.result.CodeMsg;
import com.choi.miaosha.result.Result;
import com.choi.miaosha.service.GoodsService;
import com.choi.miaosha.service.OrderService;
import com.choi.miaosha.vo.GoodsVo;
import com.choi.miaosha.vo.OrderDetailVo;

@Controller
@RequestMapping("/order")
public class OrderContoller {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private GoodsService goodsService;
	
	
	@RequestMapping("/detail")
	@ResponseBody
	public Result<OrderDetailVo> info(Model model, MiaoShaUser user, @RequestParam("orderId") long orderId){
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		//获取订单信息
		OrderInfo orderInfo = orderService.getOrderById(orderId);
		System.out.println(orderInfo);
		if(orderInfo == null){
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		//根据订单，获取商品Id
		long goodsId = orderInfo.getGoodsId();
		//根据商品ID，获取秒杀商品
		GoodsVo goodsVo = goodsService.getGoodVoByGoodsId(goodsId);
		System.out.println(goodsVo);
		OrderDetailVo orderDetailVo = new OrderDetailVo();
		orderDetailVo.setOrder(orderInfo);
		orderDetailVo.setGoodsVo(goodsVo);
		return Result.sucess(orderDetailVo);
	}

}
