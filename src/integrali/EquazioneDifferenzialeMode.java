package integrali;


import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;


public class EquazioneDifferenzialeMode implements FirstOrderDifferentialEquations{

	public static double a;
	public static double b;
	public static double S;
	public static double Q;
	public static double J;
	public static double ET;
	public static int mode;

	public EquazioneDifferenzialeMode(double a, double b, double J,double ET, double Q, int mode) {
		this.a=a;
		this.b=b;
		this.J=J;
		this.ET=ET;
		this.Q=Q;
		this.mode=mode;
	}

	public int getDimension() {
		return 2;
	}
	public void computeDerivatives(double t, double[] y, double[] yDot)
			throws MaxCountExceededException, DimensionMismatchException {
		
		if (mode==1){
			yDot[0]=J-Q/116000-ET;
		}
		
		if (mode==2){
			yDot[0]=J-a*(Math.pow(y[0],b))-ET;
		}		
		
	}
}



