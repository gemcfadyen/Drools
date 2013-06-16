package gemcfadyen.drools_experimentation.functions;

public class FunctionResult {
	private StringBuffer message = new StringBuffer();

	public String getMessage() {
		return message.toString();
	}

	public void setMessage(String message){
		this.message.append(message);
	}
}
