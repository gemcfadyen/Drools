package gemcfadyen.drools_experimentation.templates;

public class Result {
	private String message = new String();

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		// this.message.concat(message).con
		this.message = this.message + message + "*";
		//this.message += message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Result other = (Result) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

}
