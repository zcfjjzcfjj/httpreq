package cn.com.a5.console.lb;

/**
 * 所有的loadalance配置，所用到的URL
 * @author zc
 *
 */
public class URLLoadbalance {
	
	public static String webip = "10.9.1.5";
	public static String webport = "8080";
	public static String authstr = "super_auth=1";    // 权限验证
	public static String url_getclusterlist = "http://"+ webip + ":"+ webport + "/cluster/list";  // URL:获取集群列表
	

}
