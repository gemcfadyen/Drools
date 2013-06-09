package gemcfadyen.drools_experimentation.stateless;

public class BirthdayResult {
	private StringBuffer message = new StringBuffer();

	public String getBirthdayMessage() {
		return message.toString();
	}

	public void setBirthdayMessage(String message){
		this.message.append(message);
	}
}
