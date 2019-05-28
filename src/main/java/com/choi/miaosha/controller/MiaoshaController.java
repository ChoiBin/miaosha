package com.choi.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.domain.MiaoshaOrder;
import com.choi.miaosha.domain.OrderInfo;
import com.choi.miaosha.result.CodeMsg;
import com.choi.miaosha.service.GoodsService;
import com.choi.miaosha.service.MiaoshaService;
import com.choi.miaosha.service.OrderService;
import com.choi.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MiaoshaService miaoshaService;

	@RequestMapping("/do_miaosha")
	public String list(Model model, MiaoShaUser user,@RequestParam("goodsId") long goodsId){
		model.addAttribute("user", user);
		if(user == null){
			return "login";
		}
		//判断库存
		GoodsVo goods = goodsService.getGoodVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0){
			model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
			return "miaosha_fail";
		}
		//判断是否已经秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);;
		if(order != null){
			model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
			return "miaosha_fail";
		}
		//减库存 下订单 写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
		model.addAttribute("orderInfo", orderInfo);
		model.addAttribute("goods", goods);
		return "order_detail";
	}
}
