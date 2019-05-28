package com.choi.miaosha.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.choi.miaosha.domain.MiaoshaGoods;
import com.choi.miaosha.vo.GoodsVo;

@Mapper
public interface GoodsDao {

	@Select("select g.*,mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id")
	public List<GoodsVo> listGoodsVo();
	
	@Select("select g.*,mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
	public GoodsVo getGoodVoByGoodsId(@Param("goodsId") long goodsId);
	
	@Update("update miaosha_goods set stock_count = stock_count - 1 where goods_Id = #{goodsId}")
	public int reduceStock(MiaoshaGoods g);
}
