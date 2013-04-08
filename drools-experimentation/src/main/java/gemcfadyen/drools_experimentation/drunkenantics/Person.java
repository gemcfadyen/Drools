package gemcfadyen.drools_experimentation.drunkenantics;



public class Person {
	public enum PersonType {
		MAN(20), WOMAN(15), GEORDIE(50), BOUNCER(0);

		private int maxUnits;

		PersonType(int maxUnits) {
			this.maxUnits = maxUnits;
		}

		public int getMaxUnits() {
			return maxUnits;
		}
	}

	private PersonType type;
	private int numberOfDrinksConsumed;

	Person(PersonType type) {
		this.type = type;
		this.numberOfDrinksConsumed = 0;
	}

	public PersonType getType() {
		return type;
	}

	public void setType(PersonType type) {
		this.type = type;
	}

	public int getNumberOfDrinksConsumed() {
		return numberOfDrinksConsumed;
	}

	public void setNumberOfDrinksConsumed(int numberOfDrinksConsumed) {
		this.numberOfDrinksConsumed = numberOfDrinksConsumed;
	}
	
	public void addToTotalNumberOfDrinks(){
		this.numberOfDrinksConsumed++;
	}
}
