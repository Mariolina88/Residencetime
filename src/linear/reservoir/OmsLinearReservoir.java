package linear.reservoir;
import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_AUTHORCONTACTS;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_AUTHORNAMES;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_KEYWORDS;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_LABEL;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_LICENSE;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_NAME;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_STATUS;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_UI;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_defaultPressure_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_defaultTemp_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_doHourly_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_inNetradiation_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_inPressure_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_inTemp_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_outPTEtp_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_pAlpha_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_pDailyDefaultNetradiation_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_pGmorn_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_pGnight_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_pHourlyDefaultNetradiation_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSPRESTEYTAYLORETPMODEL_time_DESCRIPTION;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.UI;
import oms3.annotations.Unit;

import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import static org.jgrasstools.gears.libs.modules.JGTConstants.doubleNovalue;
import integrali.EquazioneDifferenziale;
import integrali.TimeStepIntegrator;

import java.io.IOException;
import java.util.HashMap;

import oms3.annotations.Description;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.analysis.integration.*;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

public class OmsLinearReservoir extends JGTModel{
	
	@Description("Water storage")
	@Out
	public static double S;

	@Description("Precipitation")
	@In
	public static double J;

	@Description("Discharge")
	@Out
	public static double Q;

	@Description("ET")
	@In
	public static double ET;

	public HashMap<Integer, double[]> inPrecipvalues;

	public HashMap<Integer, double[]> inDischargevalues;

	public HashMap<Integer, double[]> inETvalues;


	@Description("Injection time")
	@In
	public static double tin = 0;

	@Description("Exit time")
	@In
	public static double tex ;


	@Description("Area")
	@In
	public static double a = 2;

	@Description("Parameter of linear Reservoir")
	@In
	public static double b = 1;

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;

	double integralp;
	double integralp1=0;
	double integralt1=0;
	double integralj1=0;
	double integralS1=0;
	double integralSO1=0;
	double p;
	double theta;
	double pT;
	double pET;
	double Qout;
	double integralt;
	int t1=0;
	double integralS;
	
	
    @Execute
    public void process() throws Exception {
        checkNull(inPrecipvalues,inDischargevalues,inETvalues);
        outHMQout = new HashMap<Integer, double[]>();
        
        Set<Entry<Integer, double[]>> entrySet = inPrecipvalues.entrySet();
        for( Entry<Integer, double[]> entry : entrySet ) {
        	
            Integer basinId = entry.getKey();
                  
            double precipitation = entry.getValue()[0];		
           
			double discharge =inDischargevalues.get(basinId)[0];
			
			double ET = inETvalues.get(basinId)[0];
		
					
			double pdf= compute(precipitation,discharge,ET);
			outHMQout.put(basinId, new double[]{pdf});
			
        }
        
    }
    


	public double compute(double J, double Q, double ET) throws IOException {
			FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,1000.0, 1.0e-10, 1.0e-10);
			FirstOrderDifferentialEquations ode = new EquazioneDifferenziale(a, b, J,ET);
			// condizioni iniziali e finali
			double[] y = new double[] { integralS, 10000.0 };
			int t=t1+1;
			t1=t;		
			dp853.integrate(ode, 0.0, y, t, y);
			S=y[0];
			integralS=integralS1+S;
			integralS1=integralS;
			
			Q = a * (Math.pow(S, b));
			integralp=integralp1+(Q+ET)/S;
			integralp1=integralp;
			integralt=integralt1+Q/S*integralp;
			integralt1=integralt;
			
			p = Math.exp(-integralp);
			System.out.print(Q+ "\t");	
			theta = integralt;
			pT = Q / (theta * S) * p;
			pET = ET / ((1-theta) * S) * p;
			Qout=integralj1+J*p;
			integralj1=Qout;
			System.out.print(Qout+ "\t");
		
			return Qout;

	}
    
    
}
