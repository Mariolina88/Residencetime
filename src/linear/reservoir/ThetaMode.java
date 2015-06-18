package linear.reservoir;

public class ThetaMode implements StrategyTheta {
	double theta;

	@Override
	public double checkMode(int mode, double Q, double ET, double S) {
		
		if (mode == 1) {
			;
			
		}
		if (mode==2){
			S=S;	
		}
		
	
		return S; 
	}

}
