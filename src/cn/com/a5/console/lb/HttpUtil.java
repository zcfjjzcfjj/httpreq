package cn.com.a5.console.lb;
import org.apache.commons.io.IOUtils; 
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 请求工具类
 *
 * @author : 
 * @version : 1.0.0
 * @date : 2015/7/21
 * @see : TODO
 *  http://blog.csdn.net/happylee6688/article/details/47148227 <br>
 *  问题：
 *  1 get与post方法-----需要对异常进行添加(断网，网络不通，延时，地址错误等底层错误，逻辑错误不再此内判定)???? 考虑各种异常情况，这里捕获异常打印日志?(开发包，本身有日志，需要打印到一起)
 *  2 MAX_TIMEOUT 最大超时时间,设定多少合适????a5get与a5post函数是否需要对异常进行分情况处理
 *  
 */
public class HttpUtil {
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;    // 请求配置
    private static final int MAX_TIMEOUT = 1000;   // connect,socket,request 请求超时时间

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    
    /**
     * 发送 a5console专属的的GET 请求（HTTP）;
     * NOTE:与普通get请求比较：<br>
     * 已经设定super_auth权限;<br>
     * @param url
     * @return
     */
    public static HttpReqResponse a5Get(String url,Map<String, Object> params) {
    	String key =  URLLoadbalance.authstr.split("=")[0];
    	String value =  URLLoadbalance.authstr.split("=")[1];
    	params.put(key,value); 					// 添加super_auth权限
//    	params.put("super_auth", "1"); 			// 添加super_auth权限
        return doGet(url, params);
    }
    
    
    
    
    /**
     * 发送 GET 请求（HTTP），不带输入数据
     * @param url
     * @return
     */
    public static HttpReqResponse doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    
    
    /**
     * 发送 GET 请求（HTTP），K-V形式
     * @param url
     * @param params
     * @return
     */
    public static HttpReqResponse doGet(String url, Map<String, Object> params) {
    	HttpReqResponse  rlt = new HttpReqResponse();
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        
        for (String key : params.keySet()) {
            if (i == 0)
                param.append("?");
            else
                param.append("&");
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        
        apiUrl += param;
        
        Map<String, String> head = new HashMap<String,String>();  // http response head
        String body = null;										  // http response body
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(apiUrl);
            HttpResponse response = httpclient.execute(httpget);
            
            // 获取response 头  map<string,string>,以及主要参数值
            rlt.setReasonphrase(response.getStatusLine().getReasonPhrase());
            rlt.setStatuscode(response.getStatusLine().getStatusCode());
            
//          System.out.println("headers status line  : " + response.getStatusLine().toString());  //  HTTP/1.1 200 Okay
            org.apache.http.Header[] headers = response.getAllHeaders();
            org.apache.http.Header objtmp; 
            for (int j = 0; j < headers.length; j++) {
            	objtmp = headers[j];
            	String name  = objtmp.getName();
            	String value = objtmp.getValue();
            	head.put(name, value);
            	if ( name.equals("Content-Length")) {
		            rlt.setContentlength(Integer.parseInt(value));
				}
//            	System.out.println(objtmp.getName()+ "=>"+objtmp.getValue());
			}
            
            rlt.setHead(head);

            // 获取response body string
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                body = IOUtils.toString(instream, "UTF-8");
                rlt.setBody(body);
            }
            //-----(后续)需要对异常进行添加???? 考虑各种异常情况，这里捕获异常打印日志?
//        }catch (ConnectException e) {
//            System.out.println("Error : http request connect execption");
//            System.out.println(e.getMessage());
//        }catch (NoRouteToHostException e) {    //异常： 网线断开，或者无法与web服务器
//            System.out.println("Error :" + e.getMessage());
//        }catch (UnknownHostException e) {      //异常： ip不合法
//            System.out.println("Error :" + e.getMessage());
//        } catch (IOException e) {
//            System.out.println("Error :" + e.getMessage());
//            e.printStackTrace();
        } catch (Exception e) { //   处理方式：直接打印异常原因,不必分类
            System.out.println("Error :" + e.getMessage());
            e.printStackTrace();
		}
        return rlt;
        
    }

    
    
    /**
     * 发送 POST 请求（HTTP），不带输入数据
     * @param apiUrl
     * @return
     */
    public static String doPost(String apiUrl){
        return doPost(apiUrl, new HashMap<String, Object>());
    }
    
    
    /**
     * 发送 POST 请求（HTTP），K-V形式
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPost(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            System.out.println(response.toString());
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    
    /** 
     * 发送 POST 请求（HTTP），JSON形式 
     * @param apiUrl 
     * @param json json对象 
     * @return 
     */  
    public static String doPost(String apiUrl, Object json) {  
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        String httpStr = null;  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            StringEntity stringEntity = new StringEntity(json.toString(),"UTF-8");//解决中文乱码问题  
            stringEntity.setContentEncoding("UTF-8");  
            stringEntity.setContentType("application/json");  
            httpPost.setEntity(stringEntity);  
            response = httpClient.execute(httpPost);  
            HttpEntity entity = response.getEntity();  
            System.out.println(response.getStatusLine().getStatusCode());  
            httpStr = EntityUtils.toString(entity, "UTF-8");  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }      
    
    /**
     * 发送 POST 请求（HTTP），JSON形式<br>
     * NOTE: 与普通的post请求相比<br>
     * 加入super_auth=1<br>
     * 加入URL编码,<br>
     * 空格替换为~(防止空格编码为+),实际post的json串为"content=jsonstr(URL编码)"<br>
     * @param apiUrl
     * @param json json对象(object.tostring为json字符串)
     * @return
     */
    
    public static HttpReqResponse a5Post(String apiUrl, Object json) {
//    	apiUrl = apiUrl + "?" + "super_auth=1";   // 加入权限
    	apiUrl = apiUrl + "?" + URLLoadbalance.authstr;   // 加入权限
    	HttpReqResponse  rlt = new HttpReqResponse();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String jsonstr  = json.toString();

        jsonstr= jsonstr.replaceAll("\\s", "~");				 // 空格替换外为～，与web协商一致，防止空格被解码为+  NOTE:不要随意加空格
        String jsonstrcode = null;
        
		try {
			jsonstrcode = URLEncoder.encode(jsonstr, "UTF-8");   // URL编码  NOTE：仅post的content=后边的内容编码
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		
        jsonstr = "content=" + jsonstrcode;
			
        try {
        	// post设置
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(jsonstr,"UTF-8");// 解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");                     // post请求设置
            stringEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.setEntity(stringEntity);
        	// post请求
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            
        	// 获取post响应
	        Map<String, String> head = new HashMap<String,String>();  // http response head
	        String body = null;										  // http response body
	        // -- 获取响应头
            rlt.setReasonphrase(response.getStatusLine().getReasonPhrase());
            rlt.setStatuscode(response.getStatusLine().getStatusCode());
            org.apache.http.Header[] headers = response.getAllHeaders();
            org.apache.http.Header objtmp; 
            for (int j = 0; j < headers.length; j++) {
            	objtmp = headers[j];
            	String name  = objtmp.getName();
            	String value = objtmp.getValue();
            	head.put(name, value);
            	if ( name.equals("Content-Length")) {
		            rlt.setContentlength(Integer.parseInt(value));
				}
			}
            
            rlt.setHead(head);
            httpStr = EntityUtils.toString(entity, "UTF-8");
	        // -- 获取响应体
            body = httpStr;
            rlt.setBody(body);
        } catch (Exception e) {
        	System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                	System.out.println("Error : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return rlt;
    }
    
    public static void printHttpRes(HttpReqResponse res){
    	System.out.println("head => "+ res.getHead());
    	System.out.println("body => "+ res.getBody());
    	System.out.println("status code => " + res.getStatuscode());
    	System.out.println("reasonphrase => "+res.getReasonphrase());
    	System.out.println("contentlength=> "+res.getContentlength());
	}
    
    

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPostSSL(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
			e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     * @param apiUrl API接口URL
     * @param json JSON对象
     * @return
     */
    public static String doPostSSL(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(),"UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 创建SSL安全连接
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }


    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) throws Exception {
    	
    	String url;
    	Map<String, Object> params  =  new HashMap();        // GET请求：url 参数 map: key:value
    	Object json; 										 // POST json对象;要求 tostring函数可以输出所有的json字符串
    	HttpReqResponse res;								 // res http请求响应
    	
    	System.out.println("-----测试：1 a5get----------------------------");
    	url = "http://10.9.1.5:8080/manage/services/list";
    	params  =  new HashMap();        // url 参数 map: key:value
    	res = HttpUtil.a5Get(url,params);
    	HttpUtil.printHttpRes(res);
    	
    	
//    	System.out.println("-----测试：2 a5post----------------------------");
//    	url ="http://10.9.1.5:8080/manage/services/update";
//    	json = "{\"records\":"
//	    			+ "["
//		    			+ "{"
//		    			+ "\"type\":\"add\","
//		    			+ "\"server_info\":"
//			    			+ "{"
//			    			+ "\"address\":\"127.0.0.1:8080\","
//			    			+ "\"name\":\"service1\","
//			    			+ "\"description\":\"service-1\","
//			    			+ "\"weight\":1"
//			    			+ "}"
//			    		+ "}"
//		    		+ "]"
//    			+ "}";
//    	
//    	res = HttpUtil.a5Post(url, json);
//    	HttpUtil.printHttpRes(res);
//
    }
}