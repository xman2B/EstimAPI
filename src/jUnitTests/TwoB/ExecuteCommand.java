package jUnitTests.TwoB;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import devices.TwoB.TwoB;
import estimAPI.EstimAPI;

class ExecuteCommand {
	
	static final String DEVICE = "/dev/ttyUSB0";
	EstimAPI api = new TwoB(DEVICE);

	@Test
	void executeTest() {
		assertFalse(api.execute("xyz@42!!##??"));
		assertTrue(api.execute("initDevice=false&setMode=THROB"));
		assertTrue(api.execute("disconnectDevice=true"));
	}

}
