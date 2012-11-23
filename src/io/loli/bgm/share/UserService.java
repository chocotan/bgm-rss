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

public class UserService {
	private static Logger logger=LogManager.getLogger(UserService.class);

	private static Set<UserAction> users=null;
	public void addUser(UserAction ua){
		users.add(ua);
	}
	public String removeUser(String email){
		Iterator<UserAction> itr=users.iterator();
		while(itr.hasNext()){
			UserAction ua=itr.next();
			if(ua.getEmail().equals(email.trim())){
				users.remove(ua);
				return "SUCCESS";
			}
		}
		return "ERROR";
	}
	private int count=0;
	public void execute(){
		while(true){
			logger.info("第"+count+++"次更新");
			Iterator<UserAction> itr=users.iterator();
			
			while(itr.hasNext()){
				final UserAction ua=itr.next();
				new Thread(){
					public void run(){
						ua.execute();
					}
				}.start();
			}
			try {
				if(users.size()!=0){
					TimeUnit.SECONDS.sleep(2400);
				}else{
					TimeUnit.SECONDS.sleep(60);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
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
	private static File uf =  new File("/home/choco/soft/bangumi/bgm-users.xml");
	private void initUsers(){
		users=new HashSet<UserAction>();
		if(uf.exists()){
			JAXBContext context;
			UserInfoList uil=null;
			try{
				context=JAXBContext.newInstance(UserInfoList.class);
				Unmarshaller u = context.createUnmarshaller();
				uil = (UserInfoList) u.unmarshal(uf);
			}catch(JAXBException e){
				e.printStackTrace();
			}
			Iterator<UserInfo> itr=uil.getUserList().iterator();
			while(itr.hasNext()){
				UserInfo ui=itr.next();
				UserAction ua=new UserAction();
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