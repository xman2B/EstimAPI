package devices.TwoB;

import estimAPI.Map;

public enum TwoBMap implements Map{
		A (0),
	    B (1),
	    C (2);
		
	
		private int id;
		
		TwoBMap(int id) {
			this.id= id;
		}

		@Override
		public int getID() {
			return this.id;
		}
}