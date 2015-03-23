package integrali;


import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;


public class EquazioneDifferenziale implements FirstOrderDifferentialEquations{

	public static double a;
	public static double b;
	public static double S;
	public static double Q;
	public static double J;
	public static double ET;

		public EquazioneDifferenziale(double a, double b, double J,double ET) {
		this.a=a;
		this.b=b;
		this.J=J;
		this.ET=ET;
	}
	
		public int getDimension() {
			return 2;
		}
		public void computeDerivatives(double t, double[] y, double[] yDot)
				throws MaxCountExceededException, DimensionMismatchException {
			Q=a*(Math.pow(y[0],b));
			yDot[0]=J-Q-ET;
		}
	}
	
	
	
