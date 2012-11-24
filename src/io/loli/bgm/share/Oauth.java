package io.loli.bgm.share;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/*
 * 与新浪微博认证有关的方法类
 * @author choco(uzumakitenye@gmail.com)
 */
public class Oauth {
	//你的应用的appkey
	public static final String APPKEY = "3616446582";
	//你的应用的app_secret
	public static final String APPSECRET = "165f979663b6c178033a52ef0305a817";
	//几个URL
	public static final String AUTHORSIZE_URL = "https://api.weibo.com/oauth2/authorize";
	public static final String ACCESS_TOKEN_URL = "https://api.weibo.com/oauth2/access_token";
	public static final String REDIRECTED_URL = "http://bgm-rss.loli.io/code.jsp";
	public static final String UPDATE_URL = "https://api.weibo.com/2/statuses/update.json";
	/*
	 * 将list参数post提交到指定url
	 * @param url 请求地址
	 * @param list post提交的参数
	 * @return String 服务器返回值
	 */
	public static String getString(String url , List<NameValuePair> list){
		HttpClient httpclient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
		//url就是post提交的网址
		HttpPost httppost = new HttpPost(url);
		//设置UA防止被认为是手机访问的
		httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/18.0.966.0 Safari/535.12"); 
		UrlEncodedFormEntity uefEntity = null;//表单对象
		try {
			uefEntity = new UrlEncodedFormEntity(list,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
