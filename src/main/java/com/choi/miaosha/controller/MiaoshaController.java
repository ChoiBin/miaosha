package com.choi.miaosha.controller;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.choi.miaosha.access.AccessLimit;
import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.domain.MiaoshaOrder;
import com.choi.miaosha.domain.OrderInfo;
import com.choi.miaosha.rabbitmq.MQSender;
import com.choi.miaosha.rabbitmq.MiaoshaMessage;
import com.choi.miaosha.redis.GoodsKey;
import com.choi.miaosha.redis.RedisService;
import com.choi.miaosha.result.CodeMsg;
import com.choi.miaosha.result.Result;
import com.choi.miaosha.service.GoodsService;
import com.choi.miaosha.service.MiaoshaService;
import com.choi.miaosha.service.OrderService;
import com.choi.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{

	@Autowired
	private GoodsService goodsService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private MiaoshaService miaoshaService;

	@Autowired
	private RedisService redisService;
	
	@Autowired
	private MQSender mqSender;

	//定义一个map缓存，用户判断redis中的库存量是否小于等于0，以便于减少对redis的访问
	private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

	/**
	 * 系统初始化的时候先把商品库存量先放到redis中
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> list = goodsService.listGoodsVo();
		if(list == null){
			return;
		}
		for(GoodsVo goodsVo : list){
			redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goodsVo.getId(), goodsVo.getStockCount());
			localOverMap.put(goodsVo.getId(), false);
		}
	}

	@RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.POST)
	@ResponseBody
	public Result<Integer> list(Model model, MiaoShaUser user,
			@RequestParam("goodsId") long goodsId,
			@PathVariable("path") String path){
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		model.addAttribute("user", user);
		//验证path
		boolean check = miaoshaService.checkPath(user, goodsId, path);
		if(!check){
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		
		//内存标记，减少对redis的访问
		boolean over = localOverMap.get(goodsId);
		if(over){
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀过该商品了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null){
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		//预减库存
		long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
		if(stock < 0){
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//没有则 入队
		MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
		miaoshaMessage.setUser(user);
		miaoshaMessage.setGoodsId(goodsId);
		mqSender.sendMiaoshaMessage(miaoshaMessage);
		//进行排队操作
		return Result.sucess(0);
		
		

		/*		//判断库存
		GoodsVo goods = goodsService.getGoodVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0){
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null){
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		//减库存 下订单 写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
		return Result.sucess(orderInfo);*/
	}
	
	/**
	 * 返回oderId：成功
	 * 0 排队中
	 * -1 秒杀失败
	 */
	@RequestMapping(value="/result" , method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(Model model, MiaoShaUser user, 
			@RequestParam("goodsId") long goodsId){
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		model.addAttribute("user", user);
		long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
		System.out.println(result);
		return Result.sucess(result);
	}

	/*	@RequestMapping("/do_miaosha")
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
	 */
	
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
	@RequestMapping(value="/path",method=RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoShaUser user, 
			@RequestParam("goodsId")long goodsId,
			@RequestParam(value="verifyCode",defaultValue="0")int verifyCode){
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		boolean check = miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
		if(!check){
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		String path = miaoshaService.crateMiaoshaPath(user,goodsId);
		return Result.sucess(path);
	}
	
	@RequestMapping(value="/verifyCode",method=RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoShaUser user,
			@RequestParam("goodsId")long goodsId){
		if(user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		try {
			BufferedImage image = miaoshaService.createVerifycode(user,goodsId);
			OutputStream outputStream = response.getOutputStream();
			ImageIO.write(image, "JPEG", outputStream);
			outputStream.flush();
			outputStream.close();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
	 		return Result.error(CodeMsg.MIAOSHA_FAIL);
		}
	}
	
	
}


