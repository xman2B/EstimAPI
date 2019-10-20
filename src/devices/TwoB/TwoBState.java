package devices.TwoB;

import java.util.Arrays;
import java.util.List;

import estimAPI.Channel;
import estimAPI.Map;
import estimAPI.Mode;

public class TwoBState implements estimAPI.State {
	private Mode mode = null;
	private List<TwoBChannel> channels = Arrays.asList(TwoBChannel.A, TwoBChannel.B, TwoBChannel.C, TwoBChannel.D);
	private int battery = -1;
	private String power = "";
	private int joinedChannels = -1;
	private Map map = null;
	private String version = "";

	public TwoBState(Mode mode, int battery, String power, int joinedChannels) {
		this.mode = mode;
		this.battery = battery;
		this.power = power;
		this.joinedChannels = joinedChannels;
	}

	@Override
	public int getBattery() {
		return battery;
	}

	private void setBattery(int battery) {
		this.battery = battery;
	}

	@Override
	public String getPower() {
		return power;
	}

	private void setPower(String power) {
		this.power = power;
	}

	@Override
	public int getJoinedChannels() {
		return joinedChannels;
	}

	private void setJoinedChannels(int joinedChannels) {
		this.joinedChannels = joinedChannels;
	}
	
	@Override
	public Map getMap() {
		return this.map;
	}

	private void setMap(Map map) {
		this.map  = map;
		
	}
	
	@Override
	public String getVersion() {
		return this.version;
	}

	private void setVersion(String version) {
		this.version  = version;
		
	}
	
	public Mode getMode() {
		return this.mode;
	}

	private void setMode(Mode mode) {
		this.mode = mode;
	}

	public List<TwoBChannel> getChannels() {
		return this.channels;
	}
	
	private void setChannels(List<TwoBChannel> channels) {
		this.channels = channels;
	}

	boolean parseReply (String reply) {
		String[] replyArray = reply.split(":");
		this.setBattery(Integer.parseInt(replyArray[0]));
		
		List<TwoBChannel> channels = this.getChannels();
		for (TwoBChannel c: channels) {
			switch (c.getID()) {
				case 0: c.setValue(Integer.parseInt(replyArray[1])/2);
						break;
				case 1: c.setValue(Integer.parseInt(replyArray[2])/2);
						break;
				case 2: c.setValue(Integer.parseInt(replyArray[3])/2);
						break;
				case 3: c.setValue(Integer.parseInt(replyArray[4])/2);
						break;
				default: return false;
				
			}
		}
		this.setMode(TwoBMode.values()[Integer.parseInt(replyArray[5])]);
		this.setPower(replyArray[6]);
		this.setJoinedChannels(Integer.parseInt(replyArray[7]));
		
		if (replyArray.length == 10) {
			// new format with additional map field
			this.setMap(TwoBMap.values()[Integer.parseInt(replyArray[8])]);
			this.setVersion(replyArray[9]);
		} else {
			this.setVersion(replyArray[8]);
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		int Aout = -1;
		int Bout = -1;
		int Cout = -1;
		int Dout = -1;
		List<TwoBChannel> channels = this.getChannels();
		for (Channel c: channels) {
			switch (c.getID()) {
				case 0: Aout = c.getValue();
						break;
				case 1: Bout = c.getValue();
						break;
				case 2: Cout = c.getValue();
						break;
				case 3: Dout = c.getValue();
						break;
				
			}
		}
		return "----------------------------" +
		       "\n---  Battery : "+ this.battery +" ---" +
		       "\n---  A       : "+ Aout +" ---" +
		       "\n---  B       : "+ Bout +" ---" +
		       "\n---  C       : "+ Cout +" ---" +
		       "\n---  D       : "+ Dout +" ---" + 
		       "\n---  mode    : "+ this.getMode() +" ---" +
		       "\n---  power   : "+ this.getPower() +" ---" +
		       "\n---  joined  : "+ this.getJoinedChannels() +" ---" +
		       "\n---  Version  : "+ this.getVersion() +" ---" +
		       "\n----------------------------";
	}
	

}
