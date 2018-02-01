package devices.TwoB;

import estimAPI.Mode;

public enum TwoBMode implements Mode{
		PULSE (0),
	    BOUNCE (1),
	    CONTINUOUS (2),
	    A_SPLIT (3),
	    B_SPLIT (4),
	    WAVE (5),
	    WATERFALL (6),
	    SQUEEZE (7),
	    MILK (8),
	    THROB (9),
	    THRUST (10),
	    RANDOM (11),
	    STEP (12),
	    TRAINING (13);
	
		//You can't set these modes yet
		/*MICROPHONE (14),
		STEREO (15),
		TICKLE (16),
		POWER_LEVEL (17),
		AB_CHANNEL_LINK (18),
		BACKLIGHT (19),
		FULL_RESET (20),
		COMMANDER (22);*/
		
		
	
		private int id;
		
		TwoBMode(int id) {
			this.id= id;
		}

		@Override
		public int getID() {
			return this.id;
		}
}