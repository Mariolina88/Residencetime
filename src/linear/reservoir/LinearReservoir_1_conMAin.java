package linear.reservoir;

import oms3.annotations.Description;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.analysis.integration.*;


import integrali.*;

public class LinearReservoir_1_conMAin {

	@Description("Injection time")
	@In
	public static double tin = 0;

	@Description("Exit time")
	@In
	public static double tex = 2;

	@Description("Travel time")
	@In
	public static double t = tex - tin;

	@Description("Area")
	@In
	public static double a = 2;

	@Description("Parameter")
	@In
	public static double b = 2;

	@Description("Precipitation")
	@In
	public static double J = 500;


	@Description("Water storage")
	@Out
	public static double S;

	@Description("Discharge")
	@Out
	public static double Q;
	
	public static double ET=2;


	public static void main(String[] args) {
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-3,
				10.0, 1.0e-3, 1.0e-3);
		FirstOrderDifferentialEquations ode = new EquazioneDifferenziale(a, b,
				J,ET);
		// condizioni iniziali e finali
		double[] y = new double[] { 0.0, 10000.0 };
		dp853.integrate(ode, 0, y, 1, y);
		
		S = y[0];
		Q = a * (Math.pow(S, b));
		System.out.println("S at time " + t + " is: " + S);
		System.out.println("Q at time " + t + " is: " + Q);

		SimpsonIntegrator simpson = new SimpsonIntegrator();
		TimeStepIntegrator timef = new TimeStepIntegrator();

		double timeIntegral = simpson.integrate(10, timef, tin, tex);

		double pE = Math.exp(-Q / S * timeIntegral);
		System.out.println("pE at time " + t + " is: " + pE);
		double theta = (Q / S * timeIntegral) * pE;
		double pT = Q / (theta * S) * pE;
		System.out.println("theta at time " + t + " is: " + theta);
		System.out.println("pT at time " + t + " is: " + pT);
		double Qout = J * pT * timeIntegral;
		System.out.println("Qout at time " + t + " is: " + Qout);

	}

}
