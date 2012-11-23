package io.loli.bgm.share;

import io.loli.bgm.rome.RssFactory;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class UserAction {
	private static Logger logger=LogManager.getLogger(UserAction.class);
	private static Gson gson=new GsonBuilder().create();
	private User user;
	private String rss;
	private SyndFeed feed;
	private String email;
	private String lastUpdate;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}		
	public String getRss() {
		return rss;
	}
	public void setRss(String rss) {
		this.rss = rss;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	private SyndFeed oldFeed;
	@SuppressWarnings("unchecked")
	public void execute(){
		feed=RssFactory.getUpdatedFeed(rss);
		List<SyndEntry> entries = feed.getEntries();
		List<SyndEntry> tempEntries = new ArrayList<SyndEntry>();
		Iterator<SyndEntry> itr = entries.iterator();
		int count=0;
		while(itr.hasNext()){
			SyndEntry se = itr.next();
			count++;
			if(lastUpdate!=null&&lastUpdate.trim().equals(se.getTitle().trim())){
				while(itr.hasNext()){
					tempEntries.add(itr.next());
				}
			}
			if(count==entries.size()){
				tempEntries=entries;
			}
		}
		if(oldFeed!=null){
		List<SyndEntry> oldEntries = oldFeed.getEntries();
		for(int i=0;i<tempEntries.size();i++){
			if(!tempEntries.get(i).getTitle().equals(oldEntries.get(0).getTitle())){
				if(i!=0){
					try {
						TimeUnit.SECONDS.sleep(120);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				logger.info(email+":"+update("#"+feed.getTitle().trim()+"#"+tempEntries.get(i).getTitle()+" "+tempEntries.get(i).getLink()));
			}else{
				break;
			}
		}
		}else{
			for(int i=0;i<tempEntries.size();i++){
				if(i!=0){
					try {
						TimeUnit.SECONDS.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.lastUpdate=tempEntries.get(i).getTitle();
				updateXml(lastUpdate,email);
				logger.info(email+":"+update("#"+feed.getTitle().trim()+"#"+tempEntries.get(i).getTitle()+" "+tempEntries.get(i).getLink()));
			}
		}
		
		oldFeed=feed;
	}
	public String update(String content){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("access_token",user.getAccess_token()));
		list.add(new BasicNameValuePair("status",content));
		return Oauth.getString(Oauth.UPDATE_URL, list);
	}
	private static JAXBContext context=null;
	private static Unmarshaller u =null;
	private static Marshaller m =null;
	private static File uf =  new File("/home/choco/soft/bangumi/bgm-users.xml");
	public void updateXml(String content,String email){
		UserInfoList uil=null;
		try{
			context = JAXBContext.newInstance(UserInfoList.class);
			u = context.createUnmarshaller();
			m = context.createMarshaller();
		}catch(JAXBException e){
			e.printStackTrace();
		}
		try {
			uil = (UserInfoList) u.unmarshal(uf);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Iterator<UserInfo> itr=uil.getUserList().iterator();
		while(itr.hasNext()){
			UserInfo ui=itr.next();
			if(ui.getEmail().equals(email.trim())){
				ui.setLastUpdate(lastUpdate);
				try {
					m.marshal(uil, uf);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	public User getNewUser(String code){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("client_id",Oauth.APPKEY));
		list.add(new BasicNameValuePair("client_secret",Oauth.APPSECRET));
		list.add(new BasicNameValuePair("grant_type","authorization_code"));
		list.add(new BasicNameValuePair("code",code));
		list.add(new BasicNameValuePair("redirect_uri",Oauth.REDIRECTED_URL));
		String json=Oauth.getString(Oauth.ACCESS_TOKEN_URL, list);
		Type type=new TypeToken<User>(){}.getType();//用作参数传递给gson.fromJson()方法
		return (User)(gson.fromJson(json,type));
	}
	
	@Override
	public int hashCode(){
		return String.valueOf(email).hashCode();
	}
	@Override
	public boolean equals(Object obj){
		if(this.email.equals(((UserAction)obj).email)){
			return true;
		}
		return false;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
