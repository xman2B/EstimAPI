package jUnitTests.TwoB;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import devices.TwoB.TwoB;
import estimAPI.EstimAPI;

class ExecuteCommand {
	
	EstimAPI api = new TwoB();

	@Test
	void executeTest() {
		assertFalse(api.execute("xyz@42!!##??"));
		assertTrue(api.execute("initDevice=false&setMode=THROB"));
		assertTrue(api.execute("disconnectDevice=true"));
	}

}
