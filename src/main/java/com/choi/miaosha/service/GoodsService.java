package com.choi.miaosha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.choi.miaosha.dao.GoodsDao;
import com.choi.miaosha.domain.MiaoshaGoods;
import com.choi.miaosha.vo.GoodsVo;

@Service
public class GoodsService {
	
	@Autowired
	private GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}
	
	public GoodsVo getGoodVoByGoodsId(long goodsId){
		return goodsDao.getGoodVoByGoodsId(goodsId);
	}
	
	public boolean reduceStock(GoodsVo goodsVo){
		MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
		miaoshaGoods.setGoodsId(goodsVo.getId());
		int ret = goodsDao.reduceStock(miaoshaGoods);
		return ret > 0;
	}

}
