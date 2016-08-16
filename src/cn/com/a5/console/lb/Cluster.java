package cn.com.a5.console.lb;

/***
 * 
 * @author zc
 * 集群节点
 * id	集群id
 * name	集群名
 * created_at	创建时间戳
 * updated_at	最近更新时间戳
 * version	版本号 
 */
public class Cluster {
	private String id;           // 集群id
	private String name;		 // 集群名称
	private String created_at ;  // 创建时间戳
	private String updated_at ;  // 最近更新时间戳
	private String version;      // 版本号 
	
//	public static void main(String[] args) {
//		Cluster cluster =  new Cluster();
//		cluster.setId("111");
//		cluster.setName("cluster_1");
//		cluster.setCreated_at("00:00");
//		cluster.setCreated_at("11:11");
//		cluster.setVersion("v0.1");
//		System.out.println(cluster);
//	}
	
	@Override
	public String toString() {
		String rlt = null;
		rlt = "{"
				+ "\"id\":\"" + id + "\","
				+ "\"name\":\"" + name + "\","
				+ "\"created_at\":\"" + created_at + "\","
				+ "\"updated_at\":\"" + updated_at + "\","
				+ "\"version\":\"" + version + "\""
				+ "}";
				
		return rlt;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}