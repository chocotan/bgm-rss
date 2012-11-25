package io.loli.bgm.share;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
	public static void addUser(UserAction ua){
		users.add(ua);
	}
	
	//计数器
	private int count = 0;
	/*
	 * 每有一个用户就新建一个线程
	 */
	private int activeSize(){
		Iterator<UserAction> itr = users.iterator();
		int count = 0;
		while(itr.hasNext()){
			if(itr.next().isIsdelete()){
				count ++;
			}
		}
		return count;
	}
	
	public static Set<UserAction> getActiveUsers(){
		Set<UserAction> set = readUsers();
		Iterator<UserAction> itr = set.iterator();
		Set<UserAction> newSet = new HashSet<UserAction>();
		while(itr.hasNext()){
			UserAction ua = itr.next();
			if(!ua.isIsdelete()){
				newSet.add(ua);
			}
		}
		return newSet;
	}
	class MyThread extends Thread{
		private UserAction ua;
		
		public UserAction getUa() {
			return ua;
		}

		public void setUa(UserAction ua) {
			this.ua = ua;
		}

		public void run(){
			ua.execute();
		}
	}
	private final static List<MyThread> tl = new ArrayList<MyThread>();
	public void execute(){
		while(true){
			initUsers();
			
			logger.info("第" + count++ + "次更新:目前列表中个数" + (users.size() - activeSize()));
			Iterator<UserAction> itr = users.iterator();
			
			while(itr.hasNext()){
				final UserAction ua = itr.next();
				if(!ua.isIsdelete()){
					MyThread mt=new MyThread();
					mt.setUa(ua);
					mt.start();
					tl.add(mt);
				}
			}
			try {
				if(users.size() != 0){
					//新浪限制一小时30条微博
					TimeUnit.SECONDS.sleep(2500);
					for(int i=0;i<tl.size();i++){
						if(tl.get(i).isAlive()||!tl.get(i).isInterrupted()){
							tl.get(i).interrupt();
						}
					}
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
		logger.info("程序开始运行");
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
		users = readUsers();
	}
	
	private static JAXBContext context = null;
	private static Marshaller m = null;
	
	public static void saveUsers(Set<UserAction> ual){
		UserInfoList uil = new UserInfoList();
		List<UserInfo> list = new ArrayList<UserInfo>();
		try{
			context = JAXBContext.newInstance(UserInfoList.class);
			m = context.createMarshaller();
		}catch(JAXBException e){
			e.printStackTrace();
		}
		Iterator<UserAction> itr = ual.iterator();
		while(itr.hasNext()){
			UserInfo ui = new UserInfo();
			UserAction ua = itr.next();
			ui.setAccess_token(ua.getUser().getAccess_token());
			ui.setEmail(ua.getEmail());
			ui.setId(ua.getRss());
			ui.setLastUpdate(ua.getLastUpdate());
			ui.setPrefix(ua.getPrefix());
			ui.setIsdelete(ua.isIsdelete());
			list.add(ui);
		}
		uil.setUserList(list);
		try {
			m.marshal(uil, uf);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	public static Set<UserAction> readUsers(){
		Set<UserAction> uas = new HashSet<UserAction>();
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
			if(uil.getUserList()!=null){
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
					ua.setPrefix(ui.getPrefix());
					ua.setIsdelete(ui.isIsdelete());
					uas.add(ua);
				}
			}else{
				uil.setUserList(new ArrayList<UserInfo>());
			}
		}
		return uas;
	}
}