package io.loli.bgm.share;

import io.loli.bgm.rome.RssFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

/*
 * 用户的Bean类...(类名有些误导人)
 * @author choco(uzumakitenye@gmail.com)
 */
public class UserAction {
	private static Logger logger = LogManager.getLogger(UserAction.class);
	//解析json所用的
	private static Gson gson = new GsonBuilder().create();
	private boolean isdelete;
	private User user;
	//用户的bangumi帐号id
	private String rss;
	//接收过来的feed
	private SyndFeed feed;
	//用户的email
	private String email;
	//微博前缀
	private String prefix;
	//用户最近一次发布的内容
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
	/*
	 * 发布一条微博
	 */
	@SuppressWarnings("unchecked")
	public void execute(){
		logger.info(email + ": start");
		//获取已经更改过标题和内容的rss
		feed = RssFactory.getUpdatedFeed(rss);
		//items列表
		List<SyndEntry> entries = feed.getEntries();
		//临时存储的列表，根据lastUpdate来确定哪些是已经发不过的微博
		List<SyndEntry> tempEntries = null;
		Iterator<SyndEntry> itr = entries.iterator();
		int count=0;
		//遍历items
		while(itr.hasNext()){
			SyndEntry se = itr.next();
			count++;
			//如果count和entires的size相等，则所有的items都是未发布过的
			if(count == entries.size()){
				tempEntries = entries;
			}
			//如果标题相等，则在这之后的所有items都是未发布的
			if(lastUpdate != null && lastUpdate.trim().equals(se.getTitle().trim())){
				tempEntries = new ArrayList<SyndEntry>();
				while(itr.hasNext()){
					tempEntries.add(itr.next());
				}
			}
		}
		for(int i = 0; i<tempEntries.size(); i++){
			if(isdelete==true){
				logger.info(email+": stop");
				return;
			}
			if(i != 0){
				try {
					//线程休眠120秒, 新浪微博的限制是一小时30条
					TimeUnit.SECONDS.sleep(120);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			String temp = prefix;
			if(prefix == null || prefix.trim().equals("")){
				temp = "#" + feed.getTitle()+"#";
			}else{
				temp = "#" + prefix.trim()+"#";
			}
		
			String response="";
			String t =shortUrl(tempEntries.get(i).getLink());
			if(t==null||t.contains("null")||t.equals(null)){
				t=tempEntries.get(i).getLink();
			}
			if(email.contains("uzumakitenye")){
				
				updateTwitter("#bangumi " + "天羽ちよこ " +tempEntries.get(i).getTitle() + " " + t);
				
				response = update("#bangumi#"+ "天羽ちよこ " + tempEntries.get(i).getTitle() + t);
			}else{
				//发布微博并将返回值记录到log
				response = update(temp + tempEntries.get(i).getTitle() + tempEntries.get(i).getLink());
			}
			
			logger.info(email + ":" + response);
			//根据指定email更新xml文件中的lastUpdate
			if(response.contains("created_at")||response.contains("repeat content")){
				//将发布的content赋值给lastUpdate
				this.lastUpdate = tempEntries.get(i).getTitle();
				updateXml(lastUpdate, email);
			}else{
				try {
					TimeUnit.SECONDS.sleep(120);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i-=1;
			}
		}
	}
	
	/*
	 * 根据content发布一条微博
	 * @param content 发布微博的内容
	 * @return String 服务器返回的内容
	 */
	public String update(String content){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("access_token",user.getAccess_token()));
		list.add(new BasicNameValuePair("status",content));
		return Oauth.getString(Oauth.UPDATE_URL, list);
	}
	
	public void updateTwitter(String content){
		ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey("mVwLkzmd9OfN6qhh7bjqvg")
          .setOAuthConsumerSecret("1Cmb8UcWR341d5umHVO9wxAf7wlM9UKB8659vDHMghY")
          .setOAuthAccessToken("201129422-EktZ2sUx8vkQQGa6piNYj74q2gBfJeXTABNNlRCw")
          .setOAuthAccessTokenSecret("KPAro8tLbW70Mw0u1mwWKwHnlwQ1rgQH3JdflAIEiXE");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
			@SuppressWarnings("unused")
			Status status = twitter.updateStatus(content);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
        logger.info("twitter update succeed , content: " + content);
	}
	/*
	 * 根据指定email更新xml文件中的lastUpdate
	 * @param content lastUpdate的内容
	 * @param email 指定的email
	 */
	public void updateXml(String content,String email){
		Set<UserAction> ual = UserService.readUsers();
		Set<UserAction> temp = UserService.getActiveUsers();
		Iterator<UserAction> itr = ual.iterator();
		if(temp.size()==0){
			isdelete=true;
		}
		int count= 0;
		while(itr.hasNext()){
			UserAction ui = itr.next();
			if(ui.getEmail().equals(email.trim())){
				ui.setLastUpdate(lastUpdate);
				break;
			}
			if(++count==ual.size()){
				isdelete = true;
			}
		}
		UserService.saveUsers(ual);
	} 
	/*
	 * 根据认证code获取access_token等信息
	 * @param code 认证code
	 * @return User
	 */
	public User getNewUser(String code){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("client_id",Oauth.APPKEY));
		list.add(new BasicNameValuePair("client_secret",Oauth.APPSECRET));
		list.add(new BasicNameValuePair("grant_type","authorization_code"));
		list.add(new BasicNameValuePair("code",code));
		list.add(new BasicNameValuePair("redirect_uri",Oauth.REDIRECTED_URL));
		String json = Oauth.getString(Oauth.ACCESS_TOKEN_URL, list);
		Type type = new TypeToken<User>(){}.getType();//用作参数传递给gson.fromJson()方法
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
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public boolean isIsdelete() {
		return isdelete;
	}
	public void setIsdelete(boolean isdelete) {
		this.isdelete = isdelete;
	}
	
	private String shortUrl(String url){
		HttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
		//url就是post提交的网址
		HttpPost httppost = new HttpPost("https://www.googleapis.com/urlshortener/v1/url");
		//设置UA防止被认为是手机访问的
		httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/18.0.966.0 Safari/535.12"); 
		StringEntity uefEntity = null;//表单对象
	//	List<NameValuePair> list = new ArrayList<NameValuePair>();
	//	list.add(new BasicNameValuePair("key", "AIzaSyDWINK2MYMSlePiSbA4YDp7PHD5vhm2pBk"));
		try {
			uefEntity = new StringEntity("{\"longUrl\":\""+url+"\"}");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		uefEntity.setContentType("application/json");
		httppost.setEntity(uefEntity);
		//提交
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		//result就是服务器返回的值了
		String result = null;
		try {
			result = EntityUtils.toString(entity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Type type = new TypeToken<UrlShort>(){}.getType();
		return ((UrlShort)(gson.fromJson(result,type))).getId();
	}
	class UrlShort{
		private String kind;
		private String id;
		private String longUrl;
		public String getKind() {
			return kind;
		}
		public void setKind(String kind) {
			this.kind = kind;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getLongUrl() {
			return longUrl;
		}
		public void setLongUrl(String longUrl) {
			this.longUrl = longUrl;
		}
	}
	public static void main(String []args){
		String s=null;
		if(s==null)
		System.out.println(s);
	}
}