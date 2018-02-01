package jUnitTests.TwoB;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import estimAPI.Mode;

class ModesTest extends Setup {

	@Test
	void SetAllModes() {
		for (Mode m: twoB.getAvailableModes()) {
			twoB.setMode(m);
			assertEquals(m, twoB.getMode(), "Mode "+ m +" was not set !");
			
			/*System.out.println(m);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		//System.out.println(this.twoB.getState());
	}

}
