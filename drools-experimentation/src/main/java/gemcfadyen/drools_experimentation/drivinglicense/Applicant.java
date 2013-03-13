package gemcfadyen.drools_experimentation.drivinglicense;

public class Applicant {
	private String name;
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

	public boolean isEligible() {
		return isEligible;
	}

	public void setEligible(boolean isEligible) {
		this.isEligible = isEligible;
	}

	private Integer age;
	private boolean isEligible;

	Applicant(String name, Integer age) {
		this.name = name;
		this.age = age;
		this.isEligible = false;
	}
}
