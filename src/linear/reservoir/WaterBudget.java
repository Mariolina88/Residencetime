package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.jgrasstools.gears.libs.modules.JGTModel;

import java.io.IOException;

import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.*;


public class WaterBudget extends JGTModel{
	@Description("Water storage")
	@Out
	public static double S;
	

	@Description("Precipitation")
	@In
	public static double J;

	@Description("Discharge")
	@In
	public static double Q;
	

	@Description("ET")
	@In
	public static double ET;

	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;

	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;

	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;


	@Description("Integration time")
	@In
	public static double time ;

	@Description("Area of the basin")
	@In
	public static double A ;

	@Description("Parameter of linear Reservoir")
	@In
	public static double a ;

	@Description("Parameter of ET")
	@In
	public static double b;

	@Description("mode 1: J, Q and ET are external values, "
				+ "mode=2 Q is simualted withe the Linear Reservoir"
				+ "mode=3 Q and ET are simualted ")
	@In
	public int mode;


	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;



	@Execute
	public void process() throws Exception {
		checkNull(inPrecipvalues,inDischargevalues,inETvalues);
		outHMQout = new HashMap<Integer, double[]>();

		Set<Entry<Integer, double[]>> entrySet = inPrecipvalues.entrySet();
		for( Entry<Integer, double[]> entry : entrySet ) {

			Integer basinId = entry.getKey();

			double precipitation = entry.getValue()[0];	
			if (isNovalue(precipitation)) {
				precipitation = 0;
			} 


			double discharge =inDischargevalues.get(basinId)[0];
			if (isNovalue(discharge)) {
				discharge= 0;
			} else {
				discharge = inDischargevalues.get(basinId)[0];
			}

			double ET = inETvalues.get(basinId)[0];
			if (isNovalue(ET)) {
				ET= 0;
			} else {
				ET = inETvalues.get(basinId)[0];
			}


			double waterStorage= compute(precipitation,discharge,ET);
			outHMQout.put(basinId, new double[]{waterStorage});

		}

	}



	public double compute(double J, double Q, double ET) throws IOException {
		time=a/1000;
		
		//FirstOrderIntegrator EI=new EulerIntegrator(1);
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,time, 1.0e-10, 1.0e-10);
		FirstOrderDifferentialEquations ode = new EquazioneDifferenzialeMode(a, b,A,J,ET,Q,mode,S);

		// condizioni iniziali e finali
		double[] y = new double[] { S, 0 };
		
		//EI.integrate(ode, 0.0, y,  time, y);
		dp853.integrate(ode, 0.0, y, time, y);
		
		S=y[0];
		System.out.println(S);
		return S;
		// AGGIUNGI Q!

	}
}
