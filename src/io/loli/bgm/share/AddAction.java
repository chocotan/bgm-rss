package io.loli.bgm.share;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
	//用户信息保存的文件
	private static File uf = new File("/home/choco/soft/bangumi/bgm-users.xml");
	private static UserService us = UserServiceFactory.getUserService();
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
	  	us.addUser(ua);
	  	
	  	UserInfo ui = new UserInfo();
	  	ui.setAccess_token(ua.getUser().getAccess_token());
	  	ui.setEmail(ua.getEmail());
	  	ui.setId(ua.getRss());
	  	ui.setPrefix(prefix);
	  	//读取xml文件将新建的用户保存进去
	  	JAXBContext context;
		UserInfoList uil = null;
		Unmarshaller u = null;
		Marshaller m = null;
		try{
			context=JAXBContext.newInstance(UserInfoList.class);
			u = context.createUnmarshaller();
			m = context.createMarshaller();
		}catch(JAXBException e){
			e.printStackTrace();
		}
		if(uf.exists()){
			try {
				uil = (UserInfoList) u.unmarshal(uf);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			uil.getUserList().add(ui);
		}else{
			uil = new UserInfoList();
			List<UserInfo> uilist = new ArrayList<UserInfo>();
			uilist.add(ui);
			uil.setUserList(uilist);
		}
		try {
			m.marshal(uil, uf);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	  	return SUCCESS;
	}
	/*
	 * 删除指定email的用户
	 * @return String
	 */
	public String remove(){
		Iterator<UserAction> itr = UserService.getUsers().iterator();
		while(itr.hasNext()){
			UserAction ua = itr.next();
			if(ua.getEmail().equals(email.trim())){
				UserService.getUsers().remove(ua);
				this.addActionMessage("删除成功");
				logger.info(email + ":删除成功");
				return ERROR;
			}
		}
		this.addActionMessage("删除错误,请联系管理员:uzumakitenye@gmail.com");
		return ERROR;
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
}