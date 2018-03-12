package jUnitTests.TwoB;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import devices.TwoB.TwoBChannel;
import devices.TwoB.TwoBMode;
import estimAPI.Channel;

class SettingsTest extends Setup {

	@Test
	void LinkAndUnlinkChannels() {
		assertTrue(twoB.linkChannels(), "Can't send command !");
		assertEquals(1, twoB.getState().getJoinedChannels());
		assertTrue(twoB.unlinkChannels(), "Can't send command !");
		assertEquals(0, twoB.getState().getJoinedChannels());
	}
	
	@Test
	void HighAndLOWPowerTest() {
		assertTrue(twoB.setPowerHIGH(), "Can't send command !");
		assertEquals(twoB.getState().getPower(), "H");
		assertTrue(twoB.setPowerLOW(), "Can't send command !");
		assertEquals(twoB.getState().getPower(), "L");
	}
	
	@Test
	void KillTest() {
		twoB.setMode(TwoBMode.BOUNCE);
		twoB.setChannelOutPut(TwoBChannel.A, 4);
		twoB.setChannelOutPut(TwoBChannel.B, 2);
		twoB.setChannelOutPut(TwoBChannel.C, 48);
		twoB.setChannelOutPut(TwoBChannel.D, 52);
		assertTrue(twoB.kill(), "Can't send command !");
		List<Channel> channels = twoB.getChannels();
		assertEquals(0, channels.get(0).getValue());
		assertEquals(0, channels.get(1).getValue());
		assertEquals(48, channels.get(2).getValue());
		assertEquals(52, channels.get(3).getValue());
		assertEquals(TwoBMode.BOUNCE, twoB.getMode());
		
	}

	
	@Test
	void ResetTest() {
		twoB.setMode(TwoBMode.THRUST);
		twoB.setChannelOutPut(TwoBChannel.A, 4);
		twoB.setChannelOutPut(TwoBChannel.B, 2);
		twoB.setChannelOutPut(TwoBChannel.C, 48);
		//twoB.setChannelOutPut(TwoBChannel.D, 52);
		assertTrue(twoB.reset(), "Can't send command !");
		List<Channel> channels = twoB.getChannels();
		assertEquals(0, channels.get(0).getValue());
		assertEquals(0, channels.get(1).getValue());
		assertEquals(50, channels.get(2).getValue());
		//assertEquals(50, channels.get(3).getValue());
		assertEquals(TwoBMode.PULSE, twoB.getMode());
	}
	
}
