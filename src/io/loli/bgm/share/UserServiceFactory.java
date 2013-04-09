package io.loli.bgm.share;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UserServiceFactory {
	//log4j输出信息
	private static Logger logger=LogManager.getLogger(UserServiceFactory.class);
	//classloader加载UserServiceFactory时初始化，整个程序开始
	private final static UserService us = new UserService();
	public static UserService getUserService(){
		logger.info("获取UserService");
		return us;
	}
	public static void start(){}
}