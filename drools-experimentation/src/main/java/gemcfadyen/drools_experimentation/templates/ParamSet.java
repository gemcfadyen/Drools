package gemcfadyen.drools_experimentation.templates;

public class ParamSet {
	
	private String type;
	private int limit;
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

	public void setIsWord(boolean isWord) {
		this.isWord = isWord;
	}

}
