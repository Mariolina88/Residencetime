package linear.reservoir;


import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;


public class EquazioneDifferenzialeMode implements FirstOrderDifferentialEquations{

	public static double a;
	public static double A;
	public static double b;
	public static double nZ;
	public static double S;
	public static double Q;
	public static double J;
	public static double ET;
	public static int mode;

	public EquazioneDifferenzialeMode(double a, double b,double nZ,double A, double J,double ET, double Q, int mode,double S) {
		this.a=a;
		this.b=b;
		this.nZ=nZ;
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
		//STAI ATTENTA A QUESTA CONDIZIONE
		if (ET>S) ET=0;
					
		yDot[0] = (mode==0) ? 1/nZ*(J-a*Math.pow(S, b)-ET) : J-Q/A*3.6-ET;
		

	
	}
}
