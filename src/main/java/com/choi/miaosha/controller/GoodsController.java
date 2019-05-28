
package com.choi.miaosha.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.choi.miaosha.domain.MiaoShaUser;
import com.choi.miaosha.redis.GoodsKey;
import com.choi.miaosha.redis.RedisService;
import com.choi.miaosha.service.GoodsService;
import com.choi.miaosha.service.MiaoShaUserService;
import com.choi.miaosha.vo.GoodsVo;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	private MiaoShaUserService userService;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	
	@Autowired
	ApplicationContext applicationContext;
	
	/**
	 * 25000个线程
	 * (加了缓存）QPS:1228.1
	 * @param request
	 * @param response
	 * @param model
	 * @param user
	 * @return
	 */
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request,HttpServletResponse response,Model model,MiaoShaUser user) {
    	model.addAttribute("user", user);
    	//查询缓存
    	String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
    	if(!StringUtils.isEmpty(html)){
    		return html;
    	}
    	//查询商品列表
    	List<GoodsVo> goodsList = goodsService.listGoodsVo();
    	model.addAttribute("goodsList", goodsList);
       // return "goods_list";
    	SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(),
    			request.getLocale(), model.asMap(),applicationContext);
    	//手动渲染
    	html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
    	if(!StringUtils.isEmpty(html)){
    		redisService.set(GoodsKey.getGoodsList, "", html);
    	}
    	return html;
    }
    
	/**
	 * 25000个线程，没加缓存
	 * QPS:343.0
	 * @param request
	 * @param response
	 * @param model
	 * @param user
	 * @return
	 */
 /*   @RequestMapping(value = "/to_list")
    public String list(HttpServletRequest request,HttpServletResponse response,Model model,MiaoShaUser user) {
    	model.addAttribute("user", user);
    	//查询商品列表
    	List<GoodsVo> goodsList = goodsService.listGoodsVo();
    	model.addAttribute("goodsList", goodsList);
    	 return "goods_list";
    }   */
    
    
    @RequestMapping(value="/to_detail/{goodsId}",produces="text/html")
    @ResponseBody
    public String detail(HttpServletRequest request,HttpServletResponse response,Model model, MiaoShaUser user,@PathVariable("goodsId")long goodsId){
    	model.addAttribute("user", user);
    	
    	//取缓存
    	String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
    	if(!StringUtils.isEmpty(html)){
    		return html;
    	}
    	GoodsVo goods = goodsService.getGoodVoByGoodsId(goodsId);
    	model.addAttribute("goods", goods);
    	
    	long startAt = goods.getStartDate().getTime();
    	long endAt = goods.getEndDate().getTime();
    	long now = System.currentTimeMillis();
    	
    	int miaoshaStatus = 0;
    	int remainSeconds = 0;
    	
    	if(now < startAt){ //秒杀还没开始，倒计时
    		miaoshaStatus = 0;
    		remainSeconds = (int) ((startAt - now) / 1000);
    	}else if(now > endAt){
    		//秒杀结束
    		miaoshaStatus = 2;
    		remainSeconds = -1;
    	}else{
    		//秒杀当中
    		miaoshaStatus = 1;
    		remainSeconds = 0;
    	}
    	model.addAttribute("miaoshaStatus", miaoshaStatus);
    	model.addAttribute("remainSeconds", remainSeconds);
    	//return "goods_detail";
    	
      	SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(),
    			request.getLocale(), model.asMap(),applicationContext);
    	//手动渲染
    	html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
    	if(!StringUtils.isEmpty(html)){
    		redisService.set(GoodsKey.getGoodsList, "" + goodsId, html);
    	}
    	return html;
    }
}
