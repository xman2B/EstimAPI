package devices.TwoB;

import estimAPI.Mode;

public enum TwoBMode implements Mode{
		PULSE (0),
	    BOUNCE (1),
	    CONTINUOUS (2),
	    FLO (3),
	    A_SPLIT (4),
	    B_SPLIT (5),
	    WAVE (6),
	    WATERFALL (7),
	    SQUEEZE (8),
	    MILK (9),
	    THROB (10),
	    THRUST (11),
	    CYCLE (12),
	    TWIST (13),
	    RANDOM (14),
	    STEP (15),
	    TRAINING (16);
	
		//You can't set these modes yet
		/*MICROPHONE (15),
		STEREO (16),
		TICKLE (17),
		POWER_LEVEL (18),
		AB_CHANNEL_LINK (19),
		BACKLIGHT (20),
		FULL_RESET (21),
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