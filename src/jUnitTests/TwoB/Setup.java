package jUnitTests.TwoB;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import devices.TwoB.TwoB;
import estimAPI.EstimAPI;

public class Setup {

	static EstimAPI twoB = null;
	
	
	@BeforeAll
	static void setUp() throws Exception {
		twoB = new TwoB();
		assertTrue(twoB.initDevice(false), "Can't establish Connection");
	}

	@AfterAll
	static void tearDown() throws Exception {
		assertTrue(twoB.disconnectDevice(), "Can't terminate connection !");
		twoB = null;
	}
	
	
}
