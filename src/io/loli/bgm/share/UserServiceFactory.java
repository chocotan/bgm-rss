package io.loli.bgm.share;

public class UserServiceFactory {
	//classloader加载UserServiceFactory时初始化，整个程序开始
	private static UserService us = new UserService();
	public static UserService getUserService(){
		return us;
	}
}
