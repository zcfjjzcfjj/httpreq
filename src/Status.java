import java.util.List;

import com.google.gson.Gson;

public class Status 
{
        private String error;
        private String status;
        private String date;
        private List<Results> results;
		public String getError() 
		{
			return error;
		}
		public void setError(String error) 
		{
			this.error = error;
		}
		
		public String getStatus() 
		{
			return status;
		}
		public void setStatus(String status) 
		{
			this.status = status;
		}
		public String getDate() 
		{
			return date;
		}
		public void setDate(String date) 
		{
			this.date = date;
		}
		public List<Results> getResults() 
		{
			return results;
		}
		public void setResults(List<Results> results) 
		{
			this.results = results;
		}
		@Override
		public String toString() 
		{
			
			return "Status [error=" + error + ", status=" + status
					+ ", date=" + date + ", results=" + results + "]";
		}
		
		public static void main(String[] args) {
			
			String json = "{"
				+ "\"error\": 0,"
				+ "\"status\": \"success\","
				+ "\"date\": \"2014-05-10\","
				+ "\"results\": ["
				            + "{"
				            + "\"currentCity\": \"南京\","
				            + "\"weather_data\": ["
				            	                + "{"
				            	                	+"\"date\": \"周六(今天, 实时：19℃)\","
				            	                	+" \"dayPictureUrl\": \"http://api.map.baidu.com/images/weather/day/dayu.png\","
				            	                	+" \"nightPictureUrl\": \"http://api.map.baidu.com/images/weather/night/dayu.png\","
				            	                	+"\"weather\": \"大雨\"," 
				            	                	+" \"wind\": \"东南风5-6级\","
				            	                	+" \"temperature\": \"18℃\""
				            	                 + "},"
				            	                	
				            	                + "{"
				            	                	+"\"date\": \"周日\","
				            	                	+" \"dayPictureUrl\": \"http://api.map.baidu.com/images/weather/day/zhenyu.png\","
				            	                	+" \"nightPictureUrl\": \"http://api.map.baidu.com/images/weather/night/duoyun.png\","
				            	                	+"\"weather\": \"阵雨转多云\"," 
				            	                	+" \"wind\": \"西北风4-5级\","
				            	                	+" \"temperature\": \"21 ~ 14℃\""
				            	                 + "}"
				            	                 + "]"
				            + "}"
				            + "]"		
			+"}";
			
//		String json = "{\"a\":\"100\",\"b\":[{\"b1\":\"b_value1\",\"b2\":\"b_value2\"},{\"b1\":\"b_value3\",\"b2\":\"b_value4\"}],\"c\": {\"c1\":\"c_value1\",\"c2\":\"c_value2\"}}";
//        Gson gson = new Gson();
//        java.lang.reflect.Type type = new TypeToken<JsonBean>(){}.getType();
//        JsonBean jsonbean = gson.fromJson(json, type);
			
        System.out.println("json字符串-------------------------------------");  
		System.out.println(json);  
        System.out.println("构建gson对象-------------------------------------");  
        Gson gson = new Gson();
//        java.lang.reflect.Type type = new TypeToken<JsonBean>(){}.getType();
        Status status = gson.fromJson(json, Status.class);  
        System.out.println("获取gson对象,tostring-------------------------------------");  
        System.out.println("status="+status);  
        System.out.println("获取list元素,tostring-------------------------------------");  
        List<Results> result = status.getResults();  
        System.out.println("result="+result);  
        System.out.println("获取相应元素(深层),tostring-------------------------------------");  
        String result1 = status.getResults().get(0).getWeather_data().get(0).getDayPictureUrl();  
        System.out.println("result1="+result1);  
        System.out.println("设定相应元素(深层),tostring-------------------------------------");  
        status.getResults().get(0).getWeather_data().get(0).setDayPictureUrl("!!!modifyed");  
        result1 = status.getResults().get(0).getWeather_data().get(0).getDayPictureUrl();  
        System.out.println("result1="+result1);  
        System.out.println("修改后，获取gson对象-------------------------------------");  
        System.out.println("status="+status);  
			
		}
          
}

