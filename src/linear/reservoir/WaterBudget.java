package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import linear.reservoir.*;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.jgrasstools.gears.libs.modules.JGTModel;

import java.io.IOException;

import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.*;


public class WaterBudget extends JGTModel{
	
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

	@Description("Parameter of non-linear reservoir")
	@In
	public static double b;
	
	@Description("Pore volume in the root zone")
	@In
	public static double nZ;
	

	@Description("mode 1: J, Q and ET are external values, "
				+ "mode=2 Q is simualted withe the Linear Reservoir"
				+ "mode=3 Non-linear reservoir ")
	@In
	public int mode;

	@Description("Water storage")
	@Out
	public static double S;
	public static double S_i;
	

	@Description("The matrix of output S")
	@Out
	public HashMap<Integer, double[]> outHMSout;

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;


	@Execute
	public void process() throws Exception {
		checkNull(inPrecipvalues,inDischargevalues,inETvalues);
		outHMSout = new HashMap<Integer, double[]>();
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


			double Storage= compute(precipitation,discharge,ET);
			double dischargeQ= computeQ(mode,S,discharge);
			outHMSout.put(basinId, new double[]{Storage});
			outHMQout.put(basinId, new double[]{dischargeQ});
			
			

		}

	}

	public double compute(double J, double Q, double ET) throws IOException {
		time=a/1000;
		
		//FirstOrderIntegrator EI=new EulerIntegrator(1);
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,time, 1.0e-10, 1.0e-10);
		FirstOrderDifferentialEquations ode = new EquazioneDifferenzialeMode(a, b,nZ,A,J,ET,Q,mode,S);

		// condizioni iniziali e finali
		double[] y = new double[] { S_i, 0 };
		
		//EI.integrate(ode, 0.0, y,  time, y);
		dp853.integrate(ode, 0.0, y, time, y);
		
		S_i=y[0];
		System.out.println(S_i);
		S=S_i;
		if (mode==3){
			S=S_i*nZ;
		     }
		System.out.println(S);
		return S;
	}

	
	public double computeQ(int mode,double S,double Q) throws IOException {
		if (mode==1){
			Q=Q/A*3.6;
		}
		if (mode==2){
		    Q=a*S;
	     }
		
		if (mode==3){
			Q=a*Math.pow(S, b);
		     }
		return Q;

	}
}