package io.loli.bgm.share;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

/*
 * @author choco(uzumakitenye@gmail.com)
 */
@SuppressWarnings("serial")
public class AddAction extends ActionSupport{
	//log4j输出信息
	private static Logger logger=LogManager.getLogger(AddAction.class);
	//用户的bangumi帐号id
	private String id;
	//新浪认证返回的code
	private String code;
	//用户的email
	private String email;
	//微博前缀
	private String prefix;
	
	/*
	 * 添加一个用户
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	public String execute(){
		//判断是否已经存在此用户
		Iterator<UserAction> itr = UserService.getUsers().iterator();
		while(itr.hasNext()){
			UserAction ua = itr.next();
			if(ua.getEmail().equals(email.trim())){
				this.addActionMessage("此邮箱已存在");
				return ERROR;
			}
		}
		//如果不存在此用户
		logger.info(email + "已添加");
	  	UserAction ua = new UserAction();
	  	ua.setRss(id);
	  	ua.setUser(ua.getNewUser(code));
	  	ua.setEmail(email);
	  	ua.setPrefix(prefix);
	  	UserService.addUser(ua);
	  	UserService.saveUsers(UserService.getUsers());
	  	return SUCCESS;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/*
	 * 根据email删除一个用户
	 * @param email 
	 */
	public String remove(){
		Set<UserAction> users = UserService.readUsers();
		Iterator<UserAction> itr = users.iterator();
		while(itr.hasNext()){
			UserAction ua = itr.next();
			if(ua.getEmail().equals(email.trim())){
				users.remove(ua);
				UserService.saveUsers(users);
				logger.info(email+":删除");
				this.addActionMessage("已删除");
				return "error";
			}
		}
		this.addActionMessage("此邮箱不存在");
		return "error";
	}
}