package gemcfadyen.drools_experimentation.templates;

public class ParamSet {
	
	private String type;
	public int limit;
	private boolean isWord;

	public ParamSet(String type, int limit, boolean isWord){
		this.type = type;
		this.limit = limit;
		this.isWord = isWord;
				
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean getIsWord() {
		return isWord;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isWord ? 1231 : 1237);
		result = prime * result + limit;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ParamSet other = (ParamSet) obj;
		if (isWord != other.isWord)
			return false;
		if (limit != other.limit)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public void setIsWord(boolean isWord) {
		this.isWord = isWord;
	}

}
