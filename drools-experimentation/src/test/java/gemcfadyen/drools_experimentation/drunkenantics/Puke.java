package gemcfadyen.drools_experimentation.drunkenantics;

public class Puke {
	private Person drunkPerson;
	
	public Puke(){
		
	}
	
	public Puke(Person drunkPerson) {
		this.drunkPerson = drunkPerson;
	}

	public Person getDrunkPerson() {
		return drunkPerson;
	}
	public void setDrunkPerson(Person drunkPerson) {
		this.drunkPerson = drunkPerson;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((drunkPerson == null) ? 0 : drunkPerson.hashCode());
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
		Puke other = (Puke) obj;
		if (drunkPerson == null) {
			if (other.drunkPerson != null)
				return false;
		} else if (!drunkPerson.equals(other.drunkPerson))
			return false;
		return true;
	}



}
