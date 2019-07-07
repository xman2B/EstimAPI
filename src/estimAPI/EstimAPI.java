package estimAPI;

import java.util.List;
import java.util.Map;

public interface EstimAPI {
	
	/**
	 * Initialize the estim device
	 */
	public boolean initDevice();

	/**
	 * Initialize the estim device
	 */
	public boolean initDevice(boolean highspeed);

	/**
	 * Disconnect the device
	 */
	public boolean disconnectDevice();
	
	/**
	 * Get the current state of the device
	 */
	public State getState();
	
	/**
	 * Get the current mode of the device
	 */
	public Mode getMode();
	
	/**
	 * Set the device to a specific mode
	 */
	public boolean setMode(Mode mode);
	
	/**
	 * Get a list of available channels for the device
	 */
	public List<Channel> getChannels();

	/**
	 * Get a list of available modes for the device
	 */
	public List<Mode> getAvailableModes();

	/**
	 * Set the channel output to value
	 */
	public boolean setChannelOutPut(Channel channel, int value);
	
	/**
	 * Links the output of channels together
	 */
	public boolean linkChannels();

	/**
	 * Unlink channels output
	 */
	public boolean unlinkChannels();	
	
	/**
	 * Set the high power mode
	 */
	public boolean setPowerHIGH();

	/**
	 * Set the high power mode
	 */
	public boolean setPowerLOW();
	
	/**
	 * Resets channels A and B to 0
	 */
	public boolean kill();
	
	/**
	 * Resets all channels to 0
	 */
	public boolean reset();
	
	/**
	 * Executes the given command
	 */
	public boolean execute(String command);
	
	/**
	 * Executes the given parameters
	 */
	public boolean execute(Map<String, String[]> parameters);

}
