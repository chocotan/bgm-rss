package io.loli.bgm.rome;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.WireFeedOutput;
import com.sun.syndication.io.XmlReader;

public class RssFactory {
	private static String content_encoding = null;
	//获取已经更改后的feed
	public static SyndFeed getUpdatedFeed(String id){
		SyndFeed feed = getFeed(id);
		@SuppressWarnings("unchecked")
		List<SyndEntry> entries = feed.getEntries();
		Iterator<SyndEntry> itr = entries.iterator();
		while(itr.hasNext()){
			SyndEntry temp = itr.next();
			//更新description;
			updateEntry(temp);
		}
		return feed;
	}
	//获取更新前的feed
	public static SyndFeed getFeed(String id){
		SyndFeedInput input = new SyndFeedInput();        
		SyndFeed feed = null;
		URLConnection conn = null;
		try {
			conn = new URL("http://bgm.tv/feed/user/" + id + "/timeline").openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		content_encoding = conn.getHeaderField("Content-Encoding");        
		try {
			feed = input.build(new XmlReader(conn.getInputStream()));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (FeedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.reverse(feed.getEntries());
		return feed;
	}
	//获取更新后的channel
	public static Channel getChannel(String id){
		SyndFeed feed=getFeed(id);
		Channel channel = new Channel("rss_2.0");
		channel.setTitle(feed.getTitle());
		channel.setDescription(feed.getDescription());
		channel.setEncoding(content_encoding);
		channel.setCopyright(feed.getCopyright());
		channel.setLanguage(feed.getLanguage());
		channel.setTtl(720);
		channel.setLink(feed.getLink());
		channel.setPubDate(feed.getPublishedDate());
 	    //SyndImage和Image类没有关系，所以将属性复制到image中去
		Image image = new Image();
		SyndImage oldImage = feed.getImage();
		image.setDescription(oldImage.getDescription());
		image.setLink(oldImage.getLink());
		image.setTitle(oldImage.getTitle());
		image.setUrl(oldImage.getUrl());
		
		channel.setImage(image);
		channel.setGenerator("rome");
		
		List<Item> items = new ArrayList<Item>();
		@SuppressWarnings("unchecked")
		List<SyndEntry> entries = feed.getEntries();
		Iterator<SyndEntry> itr = entries.iterator();
		while(itr.hasNext()){
			SyndEntry temp = itr.next();
			//更新description;
			updateEntry(temp);
			//channel没有setEntries方法，所以将entries的属性复制到items中
			Item item = new Item();
			item.setAuthor(temp.getAuthor());
			Description des = new Description();
			des.setValue(temp.getDescription().getValue());
			item.setDescription(des);
			item.setPubDate(temp.getPublishedDate());
			item.setLink(temp.getLink());
			item.setTitle(temp.getTitle());
			Guid guid = new Guid();
			guid.setPermaLink(false);
			guid.setValue(temp.getUri());
			item.setGuid(guid);
			items.add(item);
			
		}
		channel.setItems(items);
		return channel;
	}
	//更新entries
	public static SyndEntry updateEntry(SyndEntry entry){
		String description = entry.getDescription().getValue();
		String newDes = "";
		//判断是否是ep的形式
		if(description.contains("ep")){
			//截取epid
			String epid = description.substring(description.indexOf("subject/ep") + 11, description.indexOf("class") - 2);
			try {
				//将动画名加入description里
				newDes = "看过"
						+ " "
						+ new String(connect(epid).getBytes("UTF8"),"UTF8")
						+ " "
						+ description.substring(description.indexOf("看过") + 2).trim();
				//将动画名加入title中,用正则去除html超链接
				entry.setTitle(newDes.replaceAll("<.*?>", ""));
				entry.getDescription().setValue(newDes);
			//	//替换link
			//	entry.setLink(description.substring(description.indexOf("a href=") + 8, description.indexOf("class") - 2));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			newDes = description;
		}
		
		return entry;
	}
	
	//获取更新后的channel的string
	public static String getRssString(String id) throws IllegalArgumentException, MalformedURLException, FeedException, IOException{
		WireFeedOutput out = new WireFeedOutput();
		return out.outputString(getChannel(id));
	}
	
	public static String connect(String epid) throws IOException{
		final String BASEURL = "http://bgm.tv/ep/";
		//连接到url并获取页面源代码
		HttpURLConnection huc;
		URL url = new URL(BASEURL+epid);
		huc = (HttpURLConnection)url.openConnection();  
	    BufferedReader in;  
	    in = new BufferedReader(new InputStreamReader(huc.getInputStream(),"UTF8"));  
	    String line;
	    StringBuilder sb=new StringBuilder();
	    while ((line = in.readLine()) != null) {  
	    	sb.append(line);
	    }
	    //页面源代码
	    String html=sb.toString();
	    //截取动画名并删除两端空格
	    return html.substring(html.indexOf("nameSingle") +15, html.indexOf("</h1>")).trim();
	}
}
