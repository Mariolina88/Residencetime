package linear.reservoir;

public class OdeMode implements Strategy {
	public int t; 

	@Override
	public int checkMode(int mode) {
		if (mode == 1) {
			t=1;
		}
		if (mode==2){
			t=10000;
		}
		return t; 
	}

}
