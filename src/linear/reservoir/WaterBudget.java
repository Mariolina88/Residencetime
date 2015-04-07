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

import integrali.EquazioneDifferenzialeMode;

import java.io.IOException;

import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;


public class WaterBudget extends JGTModel{
	@Description("Water storage")
	@Out
	public static double S=0;

	@Description("Precipitation")
	@In
	public static double J;

	@Description("Discharge")
	@Out
	public static double Q;

	@Description("ET")
	@In
	public static double ET;

	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;

	@Description("InputDischarge")
	@Out
	public HashMap<Integer, double[]> inDischargevalues;

	@Description("Input ET")
	@Out
	public HashMap<Integer, double[]> inETvalues;


	@Description("Integration time")
	@In
	public static double time ;


	@Description("Area")
	@In
	public static double a = 2;

	@Description("Parameter of linear Reservoir")
	@In
	public static double b = 1;
	
	@Description("mode 1: J, Q and ET are external values, "
			   + "mode=2 Q is simualted withe the Linear Reservoir")
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
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,1000.0, 1.0e-10, 1.0e-10);;
			FirstOrderDifferentialEquations ode = new EquazioneDifferenzialeMode(a, b, J,ET,Q,mode);
			// condizioni iniziali e finali
			
			Strategy modeStrategy = new OdeMode();
			Context context = new Context(mode, modeStrategy);
			time=context.getResult();
			
			double[] y = new double[] { 0, 10000.0 };				
			dp853.integrate(ode, 0.0, y, time, y);
			S=y[0];
			System.out.println(S);
			return S;

}
}
