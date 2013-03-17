package gemcfadyen.drools_experimentation.firealarm;

public class Sprinkler {
	private Boolean isOn;
	private Room room;

	Sprinkler(Room room, Boolean isOn){
		this.room = room;
		this.isOn = isOn;
	}
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Boolean getIsOn() {
		return isOn;
	}

	public void setIsOn(Boolean isOn) {
		this.isOn = isOn;
	}
}
