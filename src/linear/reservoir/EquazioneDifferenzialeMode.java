package linear.reservoir;


import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;


public class EquazioneDifferenzialeMode implements FirstOrderDifferentialEquations{

	public static double a;
	public static double A;
	public static double b;
	public static double S;
	public static double Q;
	public static double J;
	public static double ET;
	public static int mode;

	public EquazioneDifferenzialeMode(double a, double b,double A, double J,double ET, double Q, int mode,double S) {
		this.a=a;
		this.b=b;
		this.J=J;
		this.ET=ET;
		this.Q=Q;
		
		this.mode=mode;
		this.A=A;
		this.S=S;
		
		
		
	}

	public int getDimension() {
		return 2;
	}
	public void computeDerivatives(double t, double[] y, double[] yDot)
			throws MaxCountExceededException, DimensionMismatchException {
		
		if (mode==1){
			//3.6 is to convert m3/s in mm
			yDot[0]=J-Q/A*3.6-ET;
		}
		if (mode==2){
		yDot[0]=J-a*S-ET;
	     }
		
		if (mode==3){
			yDot[0]=J-a*S-b*J;
		     }
		
		/*if (mode==2 & ET<S){
			yDot[0]=J-a*S-ET;
		}else {
			yDot[0]=J-a*S;
		}
		*/
	
	}
}



