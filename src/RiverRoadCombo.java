// Author: Aidan Fisher

public class RiverRoadCombo {

	public int riverType;
	public int roadType;
	public int roadRotation;

	public RiverRoadCombo(int riverType, int roadType, int rotation) {
		this.riverType = riverType;
		this.roadType = roadType;
		this.roadRotation = rotation;
		if (this.riverType == 8) {
			this.riverType = -1;
		}
		if (this.roadType == 8) {
			this.roadType = -1;
		}
	}

}
