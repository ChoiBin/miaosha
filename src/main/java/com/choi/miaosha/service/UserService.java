package com.choi.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choi.miaosha.dao.UserDao;
import com.choi.miaosha.domain.User;

@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;
	
	public User getById(int id){
		return userDao.getById(id);
	}
	
	@Transactional
	public boolean tx(){
		User u1 = new User();
		u1.setId(2);
		u1.setName("binbin");
		userDao.insert(u1);
		
		User u2 = new User();
		u1.setId(2);
		u1.setName("bin");
		userDao.insert(u2);
		User u3 = new User();
		u1.setId(5);
		u1.setName("bin");
		userDao.insert(u3);
		
		return true;
		
	}
}
