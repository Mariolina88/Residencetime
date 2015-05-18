package linear.reservoir;

public class OdeMode implements Strategy {
	public double t;
	

	@Override
	public double checkMode(int mode, double a) {
		if (mode == 1) {
			t=0.0001;
			
		}
		if (mode==2){
			t=a/1000;
			
		}
		return t; 
	}

}
