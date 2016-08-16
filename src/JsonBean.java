import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 利用Gson解析json字符串
 * 参考 : http://blog.csdn.net/tkwxty/article/details/34474501/
 * 方式1:  (构建json对象解析)根据json串定义jsonbean对象，必须key严格对应，在解析的时候，直接调用元素即可
 * @author zc
 *
 */
public class JsonBean {
	public String a;
	public List<B> b;
	public C c;

	public class B {

		public String b1;
		public String b2;
	}

	public class C {
		public String c1;
		public String c2;
	}
	
	public static void main(String[] args) {
		/** *------方式1--------------------------------- */
		String json = "{\"a\":\"100\",\"b\":[{\"b1\":\"b_value1\",\"b2\":\"b_value2\"},{\"b1\":\"b_value3\",\"b2\":\"b_value4\"}],\"c\": {\"c1\":\"c_value1\",\"c2\":\"c_value2\"}}";
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<JsonBean>(){}.getType();
        JsonBean jsonbean = gson.fromJson(json, type);
        System.out.println("--------a-----------");
        System.out.println(jsonbean.a);
        System.out.println("--------list<B>------------");
        for (int i = 0; i < jsonbean.b.size(); i++) {
        	System.out.println(jsonbean.b.get(i).b1);
        	System.out.println(jsonbean.b.get(i).b2);
		}
        System.out.println("--------c-----------");
        System.out.println(jsonbean.c.c1);
		
	}	
	
}

