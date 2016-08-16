import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.NonWritableChannelException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.text.AbstractDocument.Content;

import org.omg.PortableInterceptor.ServerIdHelper;

import cn.com.a5.console.lb.HttpReqResponse;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**
 * 
 * @author zc
 * 问题：
 * 1 实现http请求的方式
 * 2 http请求异常的判定和提示： 打印控制台？日志？;对于状态码的判定，带来不同的返回值，或者 表现方式
 * 3 考虑返回多个值 : 操作状态（状态码），以及 返回的内容  ===> 返回数据对象
 * --------------------开始封装：
 * 1 http请求类：需要整理post和get的方法返回值 和输入url与params    ===> 标准化  封装为get post方法 ，重新定义返回和输入
 * 2 数据类:如何更有效的组合数据json 
 * 3 是否应当单独开一个package用来定义这些操作，如何归纳为模块
 * 4 考虑所有的节点共用一份全集的数据，不同的操作仅仅在于不同的接口
 * ------检查代码可能出错的地方------url请求
 * 1 url字符串不能有空格，
 * 2 super_auth=1必须指定 不能写错
 */



public class HttpRequest {
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL<br>
     *            如：http://127.0.0.1:8080/a5cluster/topo/get
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。<br>
     *            如：user_key=...&a5cluster_id=”1234”
     * @return HttpReqResponse 所代表远程资源的响应结果对象
     */
    public static HttpReqResponse sendGet(String url, String param) {
        String body = "";
        BufferedReader in = null;
        HttpReqResponse response =  new HttpReqResponse();
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            //1 打开和URL之间的连接
//            URLConnection connection = realUrl.openConnection();
              HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            
            //2 设置通用的请求属性  ???????设置那些？？？
            connection.setRequestMethod("GET"); // 设置请求方式  
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式  
            
            //3 建立实际的连接
            connection.connect(); //请求异常？？？？？？？？？？？？？/
            
            // 获取所有响应头字段  ? 不许要设置头？
//            response.setHead(connection.getHeaderFields());
            
            // --- 打印head：遍历所有的响应头字段,对返回字段作处理
//            Map<String, List<String>> map = response.getHead();
            
//            System.out.println("------------GET request : header");
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            
            //4 获取body:定义 BufferedReader输入流来读取URL的响应
//            System.out.println("------------GET request : body");
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                body += line;
            }
            response.setBody(body);
            
        } catch (Exception e) {
            System.out.println("Http Get request exceptional" + e);  //请求出现异常
//            e.printStackTrace();
        }
        // 5 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        
        return response;
    }

    /** 
     * 发送HttpPost请求 
     *  
     * @param strURL 
     *            服务地址 
     * @param params 
     *            json字符串,例如: "{ \"id\":\"12345\" }" ;其中属性名必须带双引号<br/> 
     *            
     * @return 成功:返回json字符串<br/> 
     *  如：url = "";  
     *  params = "{\"id\":\"12345\"}";  
     */  
    public static String sendPost(String strURL, String params) {  
    	String strhead="content=";
		try {
			params = params.replaceAll("\\s", "~");				 // 空格替换外为～，与web协商一致，防止空格被解码为+  NOTE:不要随意加空格
	    	String strcode = URLEncoder.encode(params, "UTF-8"); // URL编码  NOTE：仅post的content=后边的内容编码
			params = strhead + strcode;
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        try {  
            URL url = new URL(strURL);// 创建连接  
            HttpURLConnection connection = (HttpURLConnection) url  
                    .openConnection();  
            // 配置
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setUseCaches(false);  
            connection.setInstanceFollowRedirects(true);  
            connection.setRequestMethod("POST"); // 设置请求方式  
            //???需要加
//            ["Accept"] = "application/json, text/javascript, */*; q=0.01";
//            ["Content-Type"] = "application/x-www-form-urlencoded; charset=UTF-8";
            

            connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01"); // 设置接收数据的格式  
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // 设置发送数据的格式  
            connection.setRequestProperty("Content-Length", String.valueOf(params.length())); // 设置发送数据的格式   ????
            connection.connect();  
            // 追加body
            OutputStreamWriter out = new OutputStreamWriter(  
                    connection.getOutputStream(), "UTF-8"); // utf-8编码  
            out.append(params);  
            out.flush();  
            out.close();  
            // 读取响应  
            int length = (int) connection.getContentLength();// 获取长度  
            InputStream is = connection.getInputStream();  
            if (length != -1) {  
                byte[] data = new byte[length];  
                byte[] temp = new byte[512];  
                int readLen = 0;  
                int destPos = 0;  
                while ((readLen = is.read(temp)) > 0) {  
                    System.arraycopy(temp, 0, data, destPos, readLen);  
                    destPos += readLen;  
                }  
                String result = new String(data, "UTF-8"); // utf-8编码  
                System.out.println(result);  
                return result;  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return "error"; // 自定义错误信息  
    }  
    
    public static void testJson() {
    	
    	//-------------方式1????
//        long taskid = 192L;  
//        String tableName = "tablename";  
//  
//        JSONObject jsonStatus = new JSONObject();  
//        jsonStatus.put("code", 0);  
//        jsonStatus.put("msg", "succ");  
//        // ===========================  
//        JSONObject jsonData = new JSONObject();  
//        jsonData.put("taskid", taskid);  
//        jsonData.put("tableName", tableName);  
//        // ===========================  
//        JSONObject objData = new JSONObject();  
//        objData.put("status", jsonStatus);  
//        objData.put("data", jsonData);  
//  
//        System.out.println(objData.toString());  	
    	
//     *  如：url = "";  
//     *  params = "{\"id\":\"12345\"}";  
    	
    	//-------------方式2???
    	
//    	params = "content:{ records:[
//    		{"type":”add”,
//    		server_info:{
//    		address:”127.0.0.1:8080”,
//    		 name:”service1”,
//    		 description:”service-1”,
//    		 weight:1}
//    		}]";
    	
	      // body
    	  JSONObject body = new JSONObject();  
    	  // content
    	  JSONObject content = new JSONObject();  
    	  body.put("content" ,  content );  
    	  // records
    	  ArrayList<JSONObject> records = new ArrayList<JSONObject>();
    	  content.put("records", records);
    	  // record[1]
    	  JSONObject rec_1 = new JSONObject();  
    	  records.add(rec_1);
    	  // rec_1
    	  // rec_1  type
    	  rec_1.put("type", "add");
    	  // rec_1  server_info 
    	  JSONObject server_info = new JSONObject();  
    	  server_info.put("address", "127.0.0.1:8000");
    	  server_info.put("name", "service1");
    	  server_info.put("description", "service-1");
    	  server_info.put("weight", "1");
    	  rec_1.put("server_info", server_info);
    	  
			
//			body.put("Max.score" ,  new  Integer( 100 ));  
//			body.put("Min.score" ,  new  Integer( 50 ));  
//			body.put("nickname" ,  "picglet" );   
			
		System.out.println(body.get("content"));
	}
    
    public static void main(String[] args) {
    	
    	HttpReqResponse response;
    	String params;
    	String url;
    	
    	/**--------------------------------- 1 获取服务器列表接口文档：<<服务列表接口>>，1.1.1.1 进展：完成前两节--------------------------- */
    	
    	System.out.println("--------1. get server list ");
    	response = HttpRequest.sendGet("http://10.9.1.19:8080/manage/services/list", "super_auth=1");// 所有服务列表
//    	response = HttpRequest.sendGet("http://10.9.1.19:8080/manage/services/list", "super_auth=1&server_name=service1");  // 获取指定名称server
//    	response = HttpRequest.sendGet("http://10.9.1.19:8080/manage/services/list", "status=1&super_auth=1");  		// 获取status的server
//    	response = HttpRequest.sendGet("http://10.9.1.19:8080/manage/services/list", "super_auth=1&cluster_name=test_A5");  // 获取指定名称cluster下的server
//    	response = HttpRequest.sendGet("http://10.9.1.19:8080/manage/services/list", "super_auth=1&pool_name=????");  		// 获取指定name的pool内的server ????bug:打印所有servers
    	System.out.println(response.getBody().toString());
    	
    	
    	/**--------------------------------- 2 修改服务器列表接口(文档：<<服务列表接口>>,1.1.1.2)--------------------------- */
    	
    	System.out.println("--------2. operate server list  ");
    	// 2.1 添加服务器(作用：创建服务器节点)
    	System.out.println("===2.1 add server (create server)");
//    	url ="http://10.9.1.19:8080/manage/services/update?super_auth=1";
    	// NOTE:records操作组是必须的，修改的仅仅时增加减少里边的操作record项 
//    	params = "{\"records\":"
//	    			+ "["
//		    			+ "{"
//		    			+ "\"type\":\"add\","
//		    			+ "\"server_info\":"
//			    			+ "{"
//			    			+ "\"address\":\"127.0.0.4:8080\","
//			    			+ "\"name\":\"service4\","
//			    			+ "\"description\":\"service-4\","
//			    			+ "\"weight\":1"
//			    			+ "}"
//			    		+ "}"
//		    		+ "]"
//    			+ "}";
//		HttpRequest.sendPost(url, params);
		
		
//   	// 2.2 删除服务器
    	System.out.println("===2.2 destory server");
//    	url ="http://10.9.1.19:8080/manage/services/update?super_auth=1";
//    	params = "{\"records\":"
//	    			+ "["
//		    			+ "{"
//		    			+ "\"type\":\"del\","
//		    			+ "\"server_info\":"
//			    			+ "{"
//			    			+ "\"address\":\"127.0.0.4:8080\""    //NOTE：凭借ipport删除，仅有一项参数，没有逗号
//			    			+ "}"
//			    		+ "}"
//		    		+ "]"
//    			+ "}";
//    	
//		HttpRequest.sendPost(url, params);
		
//    	// 2.3 修改服务器
    	System.out.println("===2.3 modify server");
//    	url ="http://10.9.1.19:8080/manage/services/update?super_auth=1";
//    	params = "{\"records\":"
//	    			+ "["
//		    			+ "{"
//		    			+ "\"type\":\"mod\","
//		    			+ "\"server_info\":"
//			    			+ "{"
//			    			+ "\"address\":\"127.0.0.1:8080\","    //以ip:port为基准修改
//			    			+ "\"name\":\"service1\","
//			    			+ "\"description\":\"service-11\","
//			    			+ "\"weight\":2"
//			    			+ "}"
//			    		+ "}"
//		    		+ "]"
//    			+ "}";
//    	
//		HttpRequest.sendPost(url, params);
    	
    	
    	/**--------------------------------- 3 获取服务集群拓扑结构(文档：<<服务集群相关接口>>)--------------------------- */
    	 
    	/**
    	 * id生成： 
    	 * poolid  ServerId  Groupid
    	 * 拓扑中有3层结构
    	 * cluster 不要制定type类型
    	 * Pool（资源池节点），类型type=1；
    	 * Group（组节点），类型type=4;
    	 * Server（服务节点），类型type=2 
    	 **/
    	
    	// 3.1 获取服务集群列表(<<2.1>>)
    	System.out.println("--------3 get cluster info ");
    	System.out.println("===3.1 get cluster list ");
    	response = HttpRequest.sendGet("http://10.9.1.19:8080/cluster/list", "super_auth=1");   //bug???? 500 内部错误
    	System.out.println(response.getBody().toString());
    	
    	// 3.1 获取服务集群拓扑结构(<<2.2>>)
    	System.out.println("===3.2 get cluster topo ");
    	response = HttpRequest.sendGet("http://10.9.1.19:8080/cluster/topo", "cid=51&super_auth=1");
    	System.out.println(response.getBody().toString());
    	
    	/**--------------------------------- 3! 修改节点起停(文档：<<服务集群相关接口>>)--------------------------- */
    	
    	//???? 没有测试
    	System.out.println("===3! modify node status : start or stop "); // <<2.4>>
//    	url = "http://10.9.1.19:8080/cluster/isactive?super_auth=1";
//    	params = "{"
//    				+ "\"version\":\"0\","  // 需要获取
//    				+ "\"cid\":\"51\","     // 集群id
//    				+ "\"nid\":\"51\","     // node id
//    				+ "\"isactive\":\"1\""  // 0,1 
//    			+ "}";
//		
//    	HttpRequest.sendPost(url, params);
    	
    	/**--------------------------------- 4 操作服务集群(文档：<<服务集群相关接口>>)--------------------------- */
    	
    	System.out.println("--------4 operate cluster ");
    	System.out.println("===4.1 add cluster "); // <<2.5>>
//    	url = "http://10.9.1.19:8080/cluster/add?super_auth=1";
//    	String root_id = "1234567890";   // 自己生成
    			
//    	params = "{"
//    				+ "\"cname\":\"web2_a5console\","     // 名字不能重复????
//    				+ "\"description\":\"this is cluster web for a5console test\","
//    				+ "\"root_id\":\"2234567890\""  // 根节点 自己生成 随机10位数字,note：逗号
//    			+ "}";
//		
//    	HttpRequest.sendPost(url, params);
    	
    	
    	//???? 未测试
    	System.out.println("===4.2 del cluster "); // <<2.6>>
//    	url = "http://10.9.1.19:8080/cluster/del?super_auth=1";
//    	String id = "54";   // 集群id，获取
//    	params = "{"
//    				+ "\"id\":\"54\""  // 集群id
//    			+ "}";
//    	HttpRequest.sendPost(url, params);
    	
    	
    	System.out.println("===4.3 mod cluster "); //<<2.7>>
//    	url = "http://10.9.1.19:8080/cluster/mod?super_auth=1";
//    	String id = "1234567890";   // 集群id，获取
//    	params = "{"
//    				+ "\"id\":\"51\","  // 待修改的集群id
//    				+ "\"cname\":\"test_console_mod\","  // 集群名称
//    				+ "\"description\":\"modify cluster \""  //  集群信息
//    			+ "}";
//    	HttpRequest.sendPost(url, params);
    	
    	
//    	//???? 未测试
    	System.out.println("===4.4 get cluster status");//<<2.8>>
    	url = "http://10.9.1.19:8080/manage/services/status";
    	response = HttpRequest.sendGet(url, "cid=36&super_auth=1");
    	System.out.println(response.getBody().toString());
    	
    	
    	/**--------------------------------- 5 操作服务集群(文档：<<服务集群相关接口>>)--------------------------- */
    	
    	/**--------------------------------- 5.1 pool 添加 修改 删除(文档：<<服务集群相关接口>>)--------------------------- */
    	//???? 未验证
    	System.out.println("--------5 operate top : pool,group,server");
//    	// 5.1 (1) cluster 中添加 pool (<<2.3>>) 
    	System.out.println("===5.1 operate pool");
    	System.out.println("===5.1.1 add pool to cluster = create + add to");
    	url = "http://10.9.1.19:8080/cluster/save?super_auth=1";
    	
    	params = "{"
    				+ "\"cid\":\"51\","
    				+ "\"version\":\"0\","   //每次操作会变，需要重新获取 
	    			+ "\"records\":"
	    			+ "["
		    			+ "{"
		    			+ "\"type\":\"add\","
		    			+ "\"node_info\":"
			    			//----------------cluster 添加pool, 配置pool--
			    			+ "{"
			    			+ "\"parent_id\":\"13737241060\","                  // ===> cluster root id(cluster内的root节点),cluster 添加 pool
			    			+ "\"id\":\"1111111111\","                          // ===> poolid,cluster 添加 pool  NOTE: 集群创建自动root节点，添加cluster需要在root节点下
			    			+ "\"type\":\"1\","    		                        // [pgs] 1 pool 2 server 3 root 4 group
			    			+ "\"name\":\"pool_1\","                            // [pgs] 
			    			+ "\"description\":\"pool_1 test for a5 console\"," // [pgs] 
			    			+ "\"layer\":\"4\","	                            // [p]   4/7层
			    			+ "\"protocol\":\"1\","                             // [p]   1 tcp 2 udp 仅pool节点有
			    			+ "\"address\":\"10.1.1.1:100\","                   // [ps]
			    			+ "\"a5_priority_list\":\"????\","	 				// [p]   A5 ip数组，各a5按优先级先后排列 
			    			+ "\"lbmethod\":\"0\","                             // [p]   0 随机 1 轮循
			    			+ "\"keepalive\":\"0\","                            // [p]   是否为长链接
			    			+ "\"snat\":\"0\","		                            // [p]   开启源地址转换 (0,1) ????名称不一致
			    			+ "\"max_connections\":\"10\","	 					// [pgs] 最大连接数限制
		    			//------------------
//			    			+ "\"isactive\":\"1\"," 					   		// [pgs] 是否开启(0,1)  == !!!！单独的接口
			    			+ "\"group_least_server_num\":\"6\","	            // [g]   当组中可用服务器数少于该阈值时，默认该组异常，切换到备用组
			    			+ "\"group_priority\":\"5\","						// [g]   组优先级
			    			+ "\"group_auto_shift_back\":\"5\","	 			// [g]   组自动回切
			    			+ "\"snat_ip\":\"????\","	                        // [p]   snat ip列表          ????名称不一致 source_ip
			    			+ "\"healthcheck\":\"????\","                       // [p]   健康检查数组列表????
			    			+ "\"weight_auto\":\"0\","                          // [p]   自动权重
			    			+ "\"source_ip\":\"192.168.0.111\","	 			// [p]   源ip
			    			+ "\"weight\":\"1\","								// [p]   权重
			    			+ "\"end\":\"end\""								    // [end] 结束防止json串错误
			    			+ "}"
			    		+ "}"
		    		+ "]"
    			+ "}";
    	
//    	HttpRequest.sendPost(url, params);
    	
    	
    	//???? 未验证
//    	// 5.1 (2) 修改 pool (<<2.3>>)
    	System.out.println("===5.1.2 modify pool ");
    	url = "http://10.9.1.19:8080/cluster/save?super_auth=1";
    	
//    	 type:”mod”,
//    	 node_info:{
//    	  id:”2”
//    	  name:”service1”,
//    	  type:”service”,
//    	 parent_id:”1”,
//    	  description:”service-1”,
//    	 address:”127.0.0.1:8080”,
//    	  weight:1
//    	 }
    	
    	params = "{"
    				+ "\"cid\":\"51\","
    				+ "\"version\":\"0\","   //每次操作会变，需要重新获取 
	    			+ "\"records\":"
	    			+ "["
		    			+ "{"
		    			+ "\"type\":\"mod\","
		    			+ "\"node_info\":"
			    			//----------------cluster 添加pool, 配置pool--
			    			+ "{"
			    			+ "\"parent_id\":\"13737241060\","                  // ===> cluster root id(cluster内的root节点),cluster 添加 pool
			    			+ "\"id\":\"1111111111\","                          // ===> poolid,cluster 添加 pool  NOTE: 集群创建自动root节点，添加cluster需要在root节点下
			    			+ "\"name\":\"pool_1\","                            // [pgs] 
			    			+ "\"type\":\"1\","    		                        // [pgs] 1 pool 2 server 3 root 4 group
			    			+ "\"description\":\"pool_1 test for a5 console\"," // [pgs] 
			    			
			    			+ "\"layer\":\"4\","	                            // [p]   4/7层
			    			+ "\"protocol\":\"1\","                             // [p]   1 tcp 2 udp 仅pool节点有
			    			+ "\"lbmethod\":\"0\","                             // [p]   0 随机 1 轮循
			    			+ "\"keepalive\":\"0\","                            // [p]   是否为长链接
			    			+ "\"snat\":\"0\","		                            // [p]   开启源地址转换 (0,1) ????名称不一致
			    			+ "\"max_connections\":\"10\","	 					// [pgs] 最大连接数限制
		    			//------------------
//			    			+ "\"isactive\":\"1\"," 					   		// [pgs] 是否开启(0,1)  == !!!！单独的接口
			    			+ "\"group_least_server_num\":\"6\","	            // [g]   当组中可用服务器数少于该阈值时，默认该组异常，切换到备用组
			    			+ "\"group_priority\":\"5\","						// [g]   组优先级
			    			+ "\"group_auto_shift_back\":\"5\","	 			// [g]   组自动回切
			    			+ "\"address\":\"10.1.1.1:100\","                   // [ps]
			    			+ "\"snat_ip\":\"????\","	                        // [p]   snat ip列表          ????名称不一致 source_ip
			    			+ "\"healthcheck\":\"????\","                       // [p]   健康检查数组列表????
			    			+ "\"weight_auto\":\"0\","                          // [p]   自动权重
			    			+ "\"a5_priority_list\":\"????\","	 				// [p]   A5 ip数组，各a5按优先级先后排列 
			    			+ "\"source_ip\":\"192.168.0.111\","	 			// [p]   源ip
			    			+ "\"weight\":\"1\","								// [p]   权重
			    			+ "\"end\":\"end\""								    // [end] 结束防止json串错误
			    			+ "}"
			    		+ "}"
		    		+ "]"
    			+ "}";
    	
//    	HttpRequest.sendPost(url, params);
    	
    	//???? 未验证
//    	// 5.1 (3) 删除 pool (<<2.3>>) 需要查看是否需要哪些必要参数
    	System.out.println("===5.1.3 del pool from cluster  =  del from + destory");
    	url = "http://10.9.1.19:8080/cluster/save?super_auth=1";
    	
//    	 type:”mod”,
//    	 node_info:{
//    	  id:”2”
//    	  name:”service1”,
//    	  type:”service”,
//    	 parent_id:”1”,
//    	  description:”service-1”,
//    	 address:”127.0.0.1:8080”,
//    	  weight:1
//    	 }
    	
    	params = "{"
    				+ "\"cid\":\"51\","
    				+ "\"version\":\"0\","   //每次操作会变，需要重新获取 
	    			+ "\"records\":"
	    			+ "["
		    			+ "{"
		    			+ "\"type\":\"del\","
		    			+ "\"node_info\":"
			    			//----------------cluster 删除pool, 配置pool--
			    			+ "{"
			    			+ "\"id\":\"1111111111\","                          // ===> poolid,cluster 添加 pool  NOTE: 集群创建自动root节点，添加cluster需要在root节点下
			    			+ "\"name\":\"pool_1\","                            // [pgs] 
			    			//------------------
			    			+ "\"parent_id\":\"13737241060\","                  // ===> cluster root id(cluster内的root节点),cluster 添加 pool
			    			+ "\"type\":\"1\","    		                        // [pgs] 1 pool 2 server 3 root 4 group
			    			+ "\"description\":\"pool_1 test for a5 console\"," // [pgs] 
			    			
			    			+ "\"layer\":\"4\","	                            // [p]   4/7层
			    			+ "\"protocol\":\"1\","                             // [p]   1 tcp 2 udp 仅pool节点有
			    			+ "\"lbmethod\":\"0\","                             // [p]   0 随机 1 轮循
			    			+ "\"keepalive\":\"0\","                            // [p]   是否为长链接
			    			+ "\"snat\":\"0\","		                            // [p]   开启源地址转换 (0,1) ????名称不一致
			    			+ "\"max_connections\":\"10\","	 					// [pgs] 最大连接数限制
//			    			+ "\"isactive\":\"1\"," 					   		// [pgs] 是否开启(0,1)  == !!!！单独的接口
			    			+ "\"group_least_server_num\":\"6\","	            // [g]   当组中可用服务器数少于该阈值时，默认该组异常，切换到备用组
			    			+ "\"group_priority\":\"5\","						// [g]   组优先级
			    			+ "\"group_auto_shift_back\":\"5\","	 			// [g]   组自动回切
			    			+ "\"address\":\"10.1.1.1:100\","                   // [ps]
			    			+ "\"snat_ip\":\"????\","	                        // [p]   snat ip列表          ????名称不一致 source_ip
			    			+ "\"healthcheck\":\"????\","                       // [p]   健康检查数组列表????
			    			+ "\"weight_auto\":\"0\","                          // [p]   自动权重
			    			+ "\"a5_priority_list\":\"????\","	 				// [p]   A5 ip数组，各a5按优先级先后排列 
			    			+ "\"source_ip\":\"192.168.0.111\","	 			// [p]   源ip
			    			+ "\"weight\":\"1\","								// [p]   权重
			    			+ "\"end\":\"end\""								    // [end] 结束防止json串错误
			    			+ "}"
			    		+ "}"
		    		+ "]"
    			+ "}";
    	
//    	HttpRequest.sendPost(url, params);
    	
    	/**--------------------------------- 5.2 group 添加 修改 删除(文档：<<服务集群相关接口>>)--------------------------- */
    	
    	System.out.println("===5.2 operate group");
//    	// 5.2 pool 中添加 group (<<2.3>>)
    	System.out.println("===5.2.1 add group to pool =  create + add to ");
    	url = "http://10.9.1.19:8080/cluster/save?super_auth=1";
    	params = "{"
    				+ "\"cid\":\"51\","
    				+ "\"version\":\"0\","   //每次操作会变，需要重新获取 
	    			+ "\"records\":"
	    			+ "["
		    			+ "{"
		    			+ "\"type\":\"add\","
		    			+ "\"node_info\":"
		    			//--------------pool添加group,即配置group node----
			    			+ "{"
			    			+ "\"parent_id\":\"1111111111\","                   // ===> poolid,pool 添加 group 
			    			+ "\"id\":\"1234567890\","                          // [pgs] ===> serverid ????具体获取
			    			+ "\"type\":\"4\","    		                        // [pgs] 1 pool 2 server 3 root 4 group
			    			+ "\"name\":\"pool_1\","                            // [pgs] 
			    			+ "\"description\":\"pool_1 test for a5 console\"," // [pgs] 
			    			+ "\"max_connections\":\"10\","	 					// [pgs] 最大连接数限制
//			    			+ "\"isactive\":\"1\"," 					   		// [pgs] 是否开启(0,1)  == !!!！单独的接口
			    			+ "\"group_least_server_num\":\"6\","	            // [g]   当组中可用服务器数少于该阈值时，默认该组异常，切换到备用组
			    			+ "\"group_priority\":\"5\","						// [g]   组优先级
			    			+ "\"group_auto_shift_back\":\"5\","	 			// [g]   组自动回切
		    			//------------------
			    			+ "\"address\":\"10.1.1.1:100\","                   // [ps]
			    			+ "\"layer\":\"????\","	                            // [p]   4/7层
			    			+ "\"protocol\":\"0\","                             // [p]   1 tcp 2 udp 仅pool节点有
			    			+ "\"lbmethod\":\"0\","                             // [p]   0 随机 1 轮循
			    			+ "\"keepalive\":\"0\","                            // [p]   0 随机 1 轮循
			    			+ "\"snat\":\"0\","		                            // [p]   开启源地址转换 (0,1) ????名称不一致
			    			+ "\"snat_ip\":\"????\","	                        // [p]   snat ip列表          ????名称不一致 source_ip
			    			+ "\"healthcheck\":\"????\","                       // [p]   健康检查数组列表????
			    			+ "\"weight_auto\":\"0\","                          // [p]   自动权重
			    			+ "\"a5_priority_list\":\"????\","	 				// [p]   A5 ip数组，各a5按优先级先后排列 
			    			+ "\"source_ip\":\"192.168.0.111\","	 			// [p]   源ip
			    			+ "\"weight\":\"1\","								// [p]   权重
			    			+ "\"end\":\"end\""								    // [end] 结束防止json串错误
			    			+ "}"
			    		+ "}"
		    		+ "]"
    			+ "}";
//    	HttpRequest.sendPost(url, params);
    	
    	
    	/**--------------------------------- 6 服务集群(文档：<<服务集群相关接口>>)--------------------------- */
    	// 6.1 获取集群pool（服务组）节点列表 <<2.9>>  即pool 列表
    	url = "http://10.9.1.5:8080/cluster/pools";
    	params= "super_auth=1&cid=51";
    	response = HttpRequest.sendGet(url,params);  		// 获取指定name的pool内的server ????bug:打印所有servers
//    	System.out.println(response.getBody().toString());
    	
    	
    	// 6.2 获取集群pool（服务组）历史流量<<2.10>>
    	//    	type=0/1/2	（0：最近一小时bit/min；1：最近一天bit/hour；2：最近一周bit/day）
    	
    	url = "http://10.9.1.5:8080/cluster/pool/traffic/history";
    	params= "super_auth=1&address=10.1.3.100:8000&type=2";
    	response = HttpRequest.sendGet(url,params);  		// 获取指定name的pool内的server ????bug:打印所有servers
//    	System.out.println(response.getBody().toString());
    	
    	
    	// 6.3 获取集群pool（服务组）实时流量<<2.11>>
    	// NOTE：time_interval（两次获取的时间间隔单位s）
    	
    	url = "http://10.9.1.19:8080/cluster/pools/traffic/current";
    	params= "super_auth=1&cluster_id=51&time_interval=1";
    	response = HttpRequest.sendGet(url,params);  		// 获取指定name的pool内的server ????bug:打印所有servers
//    	System.out.println(response.getBody().toString());
    	
	}
}