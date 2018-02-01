package jUnitTests.TwoB;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import estimAPI.Channel;

class ChannelTest extends Setup {
	
	private static final int VALUE = 42;

	@Test
	void TestAllChannels() {
		twoB.setMode(twoB.getAvailableModes().get(0));	//Sets the 2B in Pulse Mode, which has all 4 Channels available
		for (Channel c: twoB.getChannels()) {
			twoB.setChannelOutPut(c, VALUE); 
			assertEquals(VALUE, c.getValue(), "Output on Channel " + c + "wasn't set to " + VALUE + " !");
			twoB.setChannelOutPut(c, 0); 
		}
	}

}
