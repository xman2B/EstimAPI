package estimAPI;

/*
 * This interface saves the current state of the device
 */
public interface State {
	
	/**
	 * Get the current mode of the device
	 */
	//public Mode getMode();
	
	/**
	 * Get a list of available channels for the device
	 */
	//public List<Channel> getChannels();

	/**
	 * Get the current value of the battery
	 */
	public int getBattery();
	
	/**
	 * Get the current power mode
	 */
	public String getPower();

	/**
	 * Get the current firmware version
	 */
	public String getVersion();
	
	/**
	 * Get the joined value (indicates if channel A and B are linked or not)
	 */
	public int getJoinedChannels();
	
	/**
	 * Get the used map
	 */
	public Map getMap();
	
}
