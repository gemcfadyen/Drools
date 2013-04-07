package gemcfadyen.drools_experimentation.drunkenantics;

public class Woman {
	boolean isLoyal;
	boolean isSingle;
	
	Woman(boolean isSingle){
		this.isSingle = isSingle;
	}
	
	Woman(boolean isSingle, boolean isLoyal){
		this.isSingle = isSingle;
		this.isLoyal = isLoyal;
	}
	
	public boolean getIsLoyal() {
		return isLoyal;
	}
	public void setLoyal(boolean isLoyal) {
		this.isLoyal = isLoyal;
	}
	public boolean getIsSingle() {
		return isSingle;
	}
	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}
}
