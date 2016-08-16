
import java.util.ArrayList;

import com.google.gson.Gson;
/**
 * 
 * @author zc
 * 参考：
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

