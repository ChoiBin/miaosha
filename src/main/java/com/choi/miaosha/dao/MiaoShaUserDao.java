package com.choi.miaosha.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.choi.miaosha.domain.MiaoShaUser;

@Mapper
public interface MiaoShaUserDao {

	@Select("select * from miaosha_user where id = #{id}")
	public MiaoShaUser getById(@Param("id") Long id);
	
	@Update("update miaosha_user set password = #{password} where id = #{id}")
	public void update(MiaoShaUser toBeUpdate);
	
}
