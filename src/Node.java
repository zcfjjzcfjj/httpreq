import java.awt.List;
import java.util.ArrayList;

import com.google.gson.Gson;
/**
 * 
 * @author zc
 * 参考：
 * (选)http://blog.csdn.net/tkwxty/article/details/34474501/
 *(选) http://jingyan.baidu.com/article/17bd8e521f1cf385ab2bb819.html
 * http://jingyan.baidu.com/article/e8cdb32b619f8437042bad53.html
 * http://blog.sina.com.cn/s/blog_64e467d60101ibpd.html
 *
 */

class Record {
	
	private String execmode ; //add del mdf

	public String getExecmode() {
		return execmode;
	}

	public void setExecmode(String execmode) {
		this.execmode = execmode;
	}
	
}

public class Node {

	private String name;
	private int age;
	private ArrayList<Record> records;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return name + ":" + age;
	}

	public static void main(String[] args) {
		Gson gson = new Gson();
		Node p = new Node();
		p.setAge(22);
		p.setName("Curry");
		String str = gson.toJson(p);
		System.out.println(str);

	}
	public ArrayList<Record> getRecords() {
		return records;
	}
	public void setRecords(ArrayList<Record> records) {
		this.records = records;
	} 
	
	public void	addRecord(Record record) {
		records.add(record);
	}
	
}

