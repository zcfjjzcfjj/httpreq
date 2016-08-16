package cn.com.a5.console.lb;
import java.util.List;
import java.util.Map;

/**
 * HttpReaRespose:<br>
 * http请求返回数据对象：包含http请求返回head和body<br>
 * head: map ,key即便返回的http头的各信息名称,value即值
 * body: http body 字符串
 * @author zc
 *
 */
public class HttpReqResponse {
	
	private Map<String, String> head ;          //  http请求返回headmap
	private String body;					    //  http请求返回body 
	
	                                            //  从head中提出来的常用内容
	private int statuscode;    	                //  状态码  
	private String reasonphrase;                //  body长度
	private int contentlength;                  
	
	public int getContentlength() {
		return contentlength;
	}
	
	public void setContentlength(int contentlength) {
		this.contentlength = contentlength;
	}
	
	public int getStatuscode() {
		return statuscode;
	}
	public void setStatuscode(int statuscode) {
		this.statuscode = statuscode;
	}
	
	public String getReasonphrase() {
		return reasonphrase;
	}
	public void setReasonphrase(String reasonphrase) {
		this.reasonphrase = reasonphrase;
	}
	

	public void setHead(Map<String, String> head) {
		this.head = head;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Map<String, String> getHead() {
		return head;
	}
	public String getBody() {
		return body;
	}
	
}
	