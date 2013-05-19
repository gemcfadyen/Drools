package gemcfadyen.drools_experimentation.agenda;

public class Task {
	private PriorityEnum priority;
	private String managedBy;
	private int deadlineForEvaluation;
	private int deadlineChecked;
	private int managerChecked;

	public PriorityEnum getPriority() {
		return priority;
	}

	public void setPriority(PriorityEnum priority) {
		this.priority = priority;
	}

	public String getManagedBy() {
		return managedBy;
	}

	public void setManagedBy(String managedBy) {
		this.managedBy = managedBy;
	}

	public int getDeadlineForEvaluation() {
		return deadlineForEvaluation;
	}

	public void setDeadlineForEvaluation(int deadlineForEvaluation) {
		this.deadlineForEvaluation = deadlineForEvaluation;
	}

	public int getDeadlineChecked() {
		return deadlineChecked;
	}

	public void setDeadlineChecked(int deadlineChecked) {
		this.deadlineChecked = deadlineChecked;
	}

	public int getManagerChecked() {
		return managerChecked;
	}

	public void setManagerChecked(int managerChecked) {
		this.managerChecked = managerChecked;
	}

}
