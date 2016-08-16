package cn.com.a5.console.lb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.StaticBucketMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 集群列表:获取集群列表接口的返回值  <<2.1 获取服务集群列表>>
 * @author zc
 *  结构如下：
 *	err_code	正确时，返回0，错误时，按错误原因返回相应错误码
	msg			正确时，不返回，错误时，返回相应错误原因
	clusters	cluster list
 */
public class ClusterList {
	private String err_code ;           // 集群id
	private String msg;		 			// 集群名称
	private List<Cluster>  clusters ;    // Cluster list
	
	public static String getClusterList() {
		String rlt = null;
		String url;
		// 1 get请求
    	Map<String, Object> params  =  new HashMap();        // GET请求：url 参数 map: key:value
    	params  =  new HashMap();        // url 参数 map: key:value
    	System.out.println(URLLoadbalance.url_getclusterlist);
    	HttpReqResponse res = HttpUtil.a5Get(URLLoadbalance.url_getclusterlist,params);
    	HttpUtil.printHttpRes(res);
    	
		// 2 生成clusterlist对象
//    	String json = "{\"a\":\"100\",\"b\":[{\"b1\":\"b_value1\",\"b2\":\"b_value2\"},{\"b1\":\"b_value3\",\"b2\":\"b_value4\"}],\"c\": {\"c1\":\"c_value1\",\"c2\":\"c_value2\"}}";
    	String json = res.getBody();
    	Gson gson = new Gson();
    	java.lang.reflect.Type type = new TypeToken<ClusterList>(){}.getType();
    	ClusterList clusterlist = gson.fromJson(json, type);
    	System.out.println("response json : clusterlist : ----------");
    	System.out.println(clusterlist);
		return rlt;
		
	}
	
	public static void main(String[] args) {
		getClusterList();
//		ClusterList clusters = new ClusterList();  // 创建cluster list
//		clusters.setErr_code("0");
//		clusters.setMsg("successfully");
//		clusters.setClusters(new ArrayList<Cluster>()); // NOTE:创建成员 cluster list
//		
//		Cluster cluster =  new Cluster();   // 创建cluser，并添加至cluster list
//		cluster.setId("111");
//		cluster.setName("cluster_1");
////		cluster.setCreated_at("00:00");
////		cluster.setCreated_at("11:11");
//		cluster.setVersion("v0.1");
//		
//		System.out.println(cluster);
//		
//		clusters.getClusters().add(cluster);
//		
//		System.out.println(clusters);
	}
	
	@Override
	public String toString() {
		String rlt = null;
		rlt = "{"
				+ "\"err_code\":\"" + err_code + "\","
				+ "\"msg\":\"" + msg + "\","
//				+ "\"clusters\":[" + clusters + "]"
				+ "\"clusters\":" + clusters
				+ "}";
				
		return rlt;
	}
	public String getErr_code() {
		return err_code;
	}
	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<Cluster> getClusters() {
		return clusters;
	}
	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	

}
