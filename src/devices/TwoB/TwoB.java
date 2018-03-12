package devices.TwoB;

import devices.TwoB.TwoBMode;
import com.fazecast.jSerialComm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import estimAPI.Channel;
import estimAPI.EstimAPI;
import estimAPI.Mode;

public class TwoB implements EstimAPI{

	private static final int BAUD_RATE = 9600;
	private static final int BAUD_RATE_HIGHSPEED = 57600;
	
	private final String device;
	private SerialPort serialPort = null;
	private InputStream inStream = null;
	private OutputStream outStream = null;

	
	private TwoBState state = null;

	public TwoB(String device) {
		this.device = device;
	}
	
	@Override
	public boolean initDevice(boolean enableHighSpeed) {	//HighSpeed Mode is available since version 2.110B
		if (this.serialPort != null && this.serialPort.isOpen() && this.outStream != null && this.inStream != null) {
			return true;
		}
		this.serialPort = SerialPort.getCommPort(device);
		this.serialPort.setComPortParameters(BAUD_RATE, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY); 		//Sets the SerialPort in the right state
		this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 100, 0);
		this.serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		if (!this.serialPort.openPort()) {
			return false;
		}
		this.outStream = this.serialPort.getOutputStream();
		this.inStream = this.serialPort.getInputStream();
		
		//Sets the initial State for the 2B
		List<TwoBChannel> channels = new ArrayList<TwoBChannel>();
		channels.add(TwoBChannel.A);
		channels.add(TwoBChannel.B);
		channels.add(TwoBChannel.C);
		channels.add(TwoBChannel.D);

		this.state = new TwoBState(TwoBMode.PULSE, channels, -1, "error", -1);
		
		if (enableHighSpeed) {
			this.enableHighSpeedState();
		}
				
		return this.sendCommand("");	//Updates the TwoBState

	}

	@Override
	public boolean disconnectDevice() {
		if (this.serialPort == null && this.outStream == null && this.inStream == null) {
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
		for(Mode m :TwoBMode.values()) {
			modes.add(m);
		}
		return  modes;
	}

	@Override
	public boolean setMode(Mode mode) {
		this.send("M" + mode.getID());
		this.updateState();
		return true;
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
		for (Channel c: this.getState().getChannels()) {
			channels.add(c);
		}
		return channels;
	}


	private boolean sendCommand(String msg) {
		if (this.send(msg)) {
			this.updateState();
			return true;
		}
		return false;
	}
	
	private boolean send(String msg) {
		msg+="\n\r";
		try {
			System.out.println("SENDING");
			this.outStream.write(msg.getBytes("UTF-8"));
			TimeUnit.MILLISECONDS.sleep(100);
			System.out.println("SEND");
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
			System.out.println("RECEIVE");
			this.inStream.read(b);
			System.out.println("RECEIVED");
			reply = new String(b);
			System.out.println(reply);
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
	 * Check what baud rate the 2B is actually using and changes it to HighSpeed if needed
	 */
	private boolean enableHighSpeedState() {		
		this.send("Z");
		
		String reply = this.recv();
		
		if (!reply.contains(":") && this.serialPort.getBaudRate() != BAUD_RATE_HIGHSPEED) {
			this.serialPort.setComPortParameters(BAUD_RATE_HIGHSPEED, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		}
		
		return true;
		
	}
}
