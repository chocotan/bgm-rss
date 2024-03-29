package io.loli.bgm.share;

import javax.xml.bind.annotation.XmlRootElement;

/*
 * @author choco(uzumakitenye@gmail.com)
 */
@XmlRootElement
public class UserInfo {
	private String email;
	private String access_token;
	private String id;
	private String lastUpdate;
	private String prefix = "";
	private boolean isdelete = false;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
}
