package devices.TwoB;

public enum TwoBChannel implements estimAPI.Channel {

	A (0),
	B (1),
	C (2),
	D (3);
	
	
	private final int id;
	private int value = -1;
	
	
	TwoBChannel(int id) {
		this.id = id;
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public int getValue() {
		return this.value;
	}

	void setValue(int value) {
		this.value = value;
	}

}
