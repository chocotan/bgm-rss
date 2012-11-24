package io.loli.bgm.share;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/*
 * 添加用户，删除用户，以及应用初始化
 * @author choco(uzumakitenye@gmail.com)
 */
public class UserService {
	private static Logger logger = LogManager.getLogger(UserService.class);
	//此list就是需要同步到微博的帐号list
	private static Set<UserAction> users = null;
	/* 
	 * 添加一个用户
	 * @param ua
	 */
	public void addUser(UserAction ua){
		users.add(ua);
	}
	/*
	 * 根据email删除一个用户
	 * @param email 
	 */
	public String removeUser(String email){
		Iterator<UserAction> itr = users.iterator();
		while(itr.hasNext()){
			UserAction ua = itr.next();
			if(ua.getEmail().equals(email.trim())){
				users.remove(ua);
				return "SUCCESS";
			}
		}
		return "ERROR";
	}
	
	//计数器
	private int count = 0;
	/*
	 * 每有一个用户就新建一个线程
	 */
	public void execute(){
		while(true){
			logger.info("第"+count+++"次更新");
			Iterator<UserAction> itr = users.iterator();
			
			while(itr.hasNext()){
				final UserAction ua = itr.next();
				new Thread(){
					public void run(){
						ua.execute();
					}
				}.start();
			}
			try {
				if(users.size() != 0){
					//新浪限制一小时30条微博，此程序是20条/2400秒
					TimeUnit.SECONDS.sleep(2400);
				}else{
					//如果users为空则每过60秒就检测一次是否有用户
					TimeUnit.SECONDS.sleep(60);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	//构造器, 用于初始化
	public UserService(){
		initUsers();
		new Thread(){public void run(){execute();}}.start();
	}
	public static Set<UserAction> getUsers() {
		return users;
	}
	public static void setUsers(Set<UserAction> users) {
		UserService.users = users;
	}
	//用户列表文件
	private static File uf = new File("/home/choco/soft/bangumi/bgm-users.xml");
	//根据xml文件初始化users
	private void initUsers(){
		users = new HashSet<UserAction>();
		if(uf.exists()){
			JAXBContext context;
			UserInfoList uil = null;
			try{
				context = JAXBContext.newInstance(UserInfoList.class);
				Unmarshaller u = context.createUnmarshaller();
				uil = (UserInfoList) u.unmarshal(uf);
			}catch(JAXBException e){
				e.printStackTrace();
			}
			Iterator<UserInfo> itr = uil.getUserList().iterator();
			while(itr.hasNext()){
				UserInfo ui = itr.next();
				UserAction ua = new UserAction();
				ua.setEmail(ui.getEmail());
				User user = new User();
				user.setAccess_token(ui.getAccess_token());
				ua.setLastUpdate(ui.getLastUpdate());
				ua.setUser(user);
				ua.setRss(ui.getId());
				users.add(ua);
			}
		}
	}
}