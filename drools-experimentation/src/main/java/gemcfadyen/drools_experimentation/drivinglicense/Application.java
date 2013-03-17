package gemcfadyen.drools_experimentation.drivinglicense;

import java.util.Date;

public class Application {
	private Date dateOfApplication;
	private Boolean isEligable;

	Application(Date applicationDate, Boolean isEligable) {
		this.dateOfApplication = applicationDate;
		this.isEligable = isEligable;
	}

	public Date getDateOfApplication() {
		return dateOfApplication;
	}

	public void setDateOfApplication(Date dateOfApplication) {
		this.dateOfApplication = dateOfApplication;
	}

	public Boolean getIsEligable() {
		return isEligable;
	}

	public void setIsEligable(Boolean isEligable) {
		this.isEligable = isEligable;
	}

}
