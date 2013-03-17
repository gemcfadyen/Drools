package gemcfadyen.drools_experimentation.drivinglicense;

public class Driver {
	private String name;
	private Integer age;
	
	Driver(String name, Integer age){
		this.name=name;
		this.age=age;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
}
