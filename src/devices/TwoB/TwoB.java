package devices.TwoB;

import devices.TwoB.TwoBMode;
import com.fazecast.jSerialComm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import estimAPI.Channel;
import estimAPI.EstimAPI;
import estimAPI.Mode;

public class TwoB implements EstimAPI {
	
	private static final Logger LOGGER = Logger.getLogger(TwoB.class.getName());
	private static final Level LOGGER_LEVEL = Level.WARNING;

	private static final int BAUD_RATE = 9600;
	private static final int BAUD_RATE_HIGHSPEED = 57600;
	private static final String LINUX_PATH = "/dev/ttyUSB0";
	private static final String WINDOWS_PATH = "COM3";

	private final String device;
	private SerialPort serialPort = null;
	private InputStream inStream = null;
	private OutputStream outStream = null;

	private TwoBState state = new TwoBState(TwoBMode.PULSE, -1, "error", -1);
	private boolean highSpeed = false;

	public TwoB(String device) {
		LOGGER.setLevel(LOGGER_LEVEL);
		this.device = device;
	}

	public TwoB() {
		LOGGER.setLevel(LOGGER_LEVEL);
		if (System.getProperty("os.name").startsWith("Windows")) {
			LOGGER.log(Level.INFO, "Windows detected");
			this.device = WINDOWS_PATH;
		} else {
			LOGGER.log(Level.INFO, "Linux detected");
			this.device = LINUX_PATH;
		}
	}

	public boolean isInitialized() {
		if (this.serialPort != null && this.serialPort.isOpen() && this.outStream != null && this.inStream != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean initDevice() {
		return this.initDevice(this.highSpeed);
	}

	@Override
	public boolean initDevice(boolean enableHighSpeed) { // HighSpeed Mode is available since version 2.110B
		if (this.isInitialized()) {
			LOGGER.log(Level.WARNING, "TwoB is already initialized");
			return true;
		}

		this.serialPort = SerialPort.getCommPort(device);
		this.serialPort.setComPortParameters(BAUD_RATE, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY); // Sets the serial port in the right state
		this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 100, 0);
		this.serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		if (!this.serialPort.openPort()) {
			this.serialPort.closePort();
			if (!this.serialPort.openPort()) {
				LOGGER.log(Level.WARNING, "Cannot initialize serial port " + device);
				return false;
			}
		}
		this.outStream = this.serialPort.getOutputStream();
		this.inStream = this.serialPort.getInputStream();

		if (enableHighSpeed) {
			this.enableHighSpeedState();
		}

		return this.sendCommand(""); // Updates the TwoBState

	}

	@Override
	public boolean disconnectDevice() {
		if (!this.isInitialized()) {
			LOGGER.log(Level.WARNING, "TwoB is already disconnected");
			return true;
		}

		try {
			this.inStream.close();
			this.outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		this.inStream = null;
		this.outStream = null;
		this.serialPort.closePort();
		this.serialPort = null;
		
		// Reset state
		this.state = new TwoBState(TwoBMode.PULSE, -1, "error", -1);

		return true;
	}

	@Override
	public TwoBState getState() {
		return this.state;
	}

	@Override
	public Mode getMode() {
		return this.getState().getMode();
	}

	@Override
	public List<Mode> getAvailableModes() {
		List<Mode> modes = new ArrayList<Mode>();
		for (Mode m : TwoBMode.values()) {
			modes.add(m);
		}
		return modes;
	}

	@Override
	public boolean setMode(Mode mode) {
		return this.sendCommand("M" + mode.getID());
	}

	@Override
	public boolean setChannelOutPut(Channel channel, int value) {
		return this.sendCommand(channel.toString() + value);
	}

	@Override
	public boolean linkChannels() {
		return this.sendCommand("J1");
	}

	@Override
	public boolean unlinkChannels() {
		return this.sendCommand("J0");
	}

	@Override
	public boolean setPowerHIGH() {
		return this.sendCommand("H");
	}

	@Override
	public boolean setPowerLOW() {
		return this.sendCommand("L");
	}

	@Override
	public boolean kill() {
		return this.sendCommand("K");
	}

	@Override
	public boolean reset() {
		return this.sendCommand("E");
	}

	@Override
	public List<Channel> getChannels() {
		List<Channel> channels = new ArrayList<Channel>();
		for (Channel c : this.getState().getChannels()) {
			channels.add(c);
		}
		// Force a state update to get the actual values of the channels
		this.sendCommand("");
		return channels;
	}

	private boolean sendCommand(String msg) {
		if (!this.isInitialized()) {
			LOGGER.log(Level.WARNING, "TwoB is not initialized");
			if (!this.initDevice(this.highSpeed))
				return false;
		}
		if (this.send(msg)) {
			this.updateState();
			return true;
		}
		return false;
	}

	private boolean send(String msg) {
		msg += "\n\r";
		try {
			LOGGER.log(Level.FINER, "SENDING '" + msg + "'");
			this.outStream.write(msg.getBytes("UTF-8"));
			TimeUnit.MILLISECONDS.sleep(100);
			LOGGER.log(Level.FINER, "SEND '" + msg + "'");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String recv() {
		String reply = "";
		byte[] b = new byte[1000];
		try {
			LOGGER.log(Level.FINER, "RECEIVE");
			this.inStream.read(b);
			LOGGER.log(Level.FINER, "RECEIVED");
			reply = new String(b);
			LOGGER.log(Level.FINER, reply);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reply;
	}

	private void updateState() {
		String reply = this.recv();
		this.getState().parseReply(reply);
	}

	/*
	 * Check what baud rate the 2B is actually using and changes it to HighSpeed if
	 * needed
	 */
	private boolean enableHighSpeedState() {
		this.send("Z");

		String reply = this.recv();

		if (!reply.contains(":") && this.serialPort.getBaudRate() != BAUD_RATE_HIGHSPEED) {
			this.serialPort.setComPortParameters(BAUD_RATE_HIGHSPEED, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		}

		this.highSpeed = true;
		return true;

	}

	@Override
	public boolean execute(String command) {
		LOGGER.log(Level.FINE, command);
		Map<String, String[]> query_pairs = new LinkedHashMap<String, String[]>();
		String[] pairs = command.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			try {
				query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
						URLDecoder.decode(pair.substring(idx + 1), "UTF-8").split(","));
			} catch (StringIndexOutOfBoundsException e) {
				try {
					query_pairs.put(URLDecoder.decode(pair, "UTF-8"), new String[0]);
				} catch (Exception e1) {
					LOGGER.log(Level.SEVERE, command);
					e1.printStackTrace();
					return false;
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, command);
				e.printStackTrace();
				return false;

			}
		}
		return this.execute(query_pairs);

	}

	@Override
	public boolean execute(Map<String, String[]> parameters) {

		boolean error = false;
		String answer = "";

		LOGGER.log(Level.FINE, parameters.toString());

		for (String parameter : parameters.keySet()) {
			String[] values = parameters.get(parameter);
			switch (parameter) {
			case "initDevice":
				if ("true".equals(values.length == 1 ? values[0]: ""))
					this.initDevice(true);
				else {
					this.initDevice();
				}
				break;
			case "disconnectDevice":
				this.disconnectDevice();
				break;
			case "setMode":
				error = true;
				for (Mode mode : TwoBMode.values()) {
					if (mode.toString().equals(values[0])) {
						this.setMode(mode);
						error = false;
						break;
					}
				}
				break;
			case "setChannelOutputA":
				this.setChannelOutPut(TwoBChannel.A, Integer.parseInt(values[0]));
				break;
			case "setChannelOutputB":
				this.setChannelOutPut(TwoBChannel.B, Integer.parseInt(values[0]));
				break;
			case "setChannelOutputC":
				this.setChannelOutPut(TwoBChannel.C, Integer.parseInt(values[0]));
				break;
			case "setChannelOutputD":
				this.setChannelOutPut(TwoBChannel.D, Integer.parseInt(values[0]));
				break;
			case "linkChannels":
				this.linkChannels();
				break;
			case "unlinkChannels":
				this.unlinkChannels();
				break;
			case "setPowerHIGH":
				this.setPowerHIGH();
				break;
			case "setPowerLOW":
				this.setPowerLOW();
				break;
			case "kill":
				this.kill();
				break;
			case "reset":
				this.reset();
				break;
			case "getState":
				answer += this.getState().toString();
				break;
			case "getMode":
				answer += this.getMode().toString();
				break;
			case "getChannels":
				answer += this.getChannels().toString();
				break;
			case "getAvailableModes":
				answer += this.getAvailableModes().toString();
				break;
			default:
				error = true;
				break;
			}
		}

		// TODO Return answer
		LOGGER.log(Level.INFO, answer);
		return !error;
	}
}
