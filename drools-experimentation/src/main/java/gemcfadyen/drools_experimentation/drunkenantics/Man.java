package gemcfadyen.drools_experimentation.drunkenantics;

public class Man {

	private boolean isLoyal;
	private boolean isSingle;
	
	Man(boolean isSingle){
		this.isSingle = isSingle;
	}
	
	Man(boolean isSingle, boolean isLoyal){
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
