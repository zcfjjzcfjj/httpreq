import java.util.List;
/**
 * 
 * @author zc
 *  
 * 参考 : http://blog.csdn.net/tkwxty/article/details/34474501/
 * (选)http://blog.csdn.net/tkwxty/article/details/34474501/
 *(选) http://jingyan.baidu.com/article/17bd8e521f1cf385ab2bb819.html
 * http://jingyan.baidu.com/article/e8cdb32b619f8437042bad53.html
 * http://blog.sina.com.cn/s/blog_64e467d60101ibpd.html
 * 
 *
 */

public class Results 
{	
	private String currentCity;
	private List<Weather> weather_data;
	public String getCurrentCity() 
	{
		return currentCity;
	}
	public void setCurrentCity(String currentCity) 
	{
		this.currentCity = currentCity;
	}
	public List<Weather> getWeather_data() 
	{
		return weather_data;
	}
	public void setWeather_data(List<Weather> weather_data) 
	{
		this.weather_data = weather_data;
	}
	@Override
	public String toString() 
	{
		return "Results [currentCity=" + currentCity + ", weather_data="
				+ weather_data + "]";
	}
}
