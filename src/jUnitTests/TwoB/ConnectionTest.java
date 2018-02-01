package jUnitTests.TwoB;


import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;


class ConnectionTest extends Setup {
	

	@Test
	void SecondInit() {
		assertTrue(twoB.initDevice(), "Can't establish connection !");
	}
	
	@Test
	void SecondDisconnect() {
		assertTrue(twoB.disconnectDevice(), "Can't terminate connection !");
		assertTrue(twoB.disconnectDevice(), "Can't terminate connection !");
	}
	
}
