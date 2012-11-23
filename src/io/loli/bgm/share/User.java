package io.loli.bgm.share;

/* 
 * 认证后返回的access_token以及其他信息
 * @author choco(uzumakitenye@gmail.com)
 */
public class User {
	private String access_token;
	private String expires_in;
	private String remind_in;
	private String uid;
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	public String getRemind_in() {
		return remind_in;
	}
	public void setRemind_in(String remind_in) {
		this.remind_in = remind_in;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
}
