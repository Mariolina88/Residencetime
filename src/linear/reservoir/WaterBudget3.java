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

import org.geotools.feature.SchemaException;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.*;


public class WaterBudget3 extends JGTModel{

	@Description("Precipitation")
	@In
	public static double J;
	public String pathToPrec;

	@Description("Discharge")
	@In
	public static double Q;
	public String pathToDischarge;

	@Description("ET")
	@In
	public static double ET;
	public String pathToET;
	
	@Description("First date of the simulation")
	@In
	public String tStartDate;
	
	@Description("Last date of the simulation")
	@In
	public String tEndDate;
	
	@Description("time step of the simulation")
	@In
	public int inTimestep;
	
	@Description("Station ID")
	@In
	public int ID;

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
	
	@Description("soil moisture")
	@Out
	public static double S_i;


	@Description("The matrix of output S")
	@Out
	public HashMap<Integer, double[]> outHMSout;
	double Storage;

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;
	double dischargeQ;
	
	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;
	
	//dimension of the output matrix 
	int dim;
	//injection time
	int t_i=0;
	DateTime StartDate_ti;
	//time
	int t=0;
	DateTime StartDate_t;
	DateTime startDate;


	@Execute
	public void process() throws Exception {
		DateTime start = formatter.parseDateTime(tStartDate);
		DateTime end = formatter.parseDateTime(tEndDate);
		dim=Hours.hoursBetween(start, end).getHours()+1;
		double[] []resultS = new double[dim][dim];
		double[][] resultQ = new double[dim][dim];
		startDate = formatter.parseDateTime(tStartDate);


		for (t=0;t<dim-t_i;t++){
			StartDate_ti=startDate.plusHours(t_i);	
			StartDate_t=StartDate_ti.plusHours(t);
			String StartDate=StartDate_t.toString(formatter);		
			InputWB dataInput=new InputWB(pathToPrec,pathToDischarge,pathToET,StartDate,tEndDate,inTimestep,ID);
			J=dataInput.precipitation;
			Q=dataInput.discharge;
			ET=dataInput.ET;
			Storage= compute(J,Q,ET);
			dischargeQ= computeQ(mode,S,Q);
			resultS[t_i][t_i+t]=Storage;
			resultQ[t_i][t_i+t]=dischargeQ;
		}

		t=0;
		t_i=t_i+1;
		S_i=0;
		if(t_i>dim-1){
			System.exit(0);
		}

		storeResult(dim,t,t_i,resultS,resultQ);


	}

	public double compute(double J, double Q, double ET) throws IOException {
		time=a/1000;

		//FirstOrderIntegrator EI=new EulerIntegrator(1);
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,time, 1.0e-10, 1.0e-10);
		FirstOrderDifferentialEquations ode = new EquazioneDifferenzialeMode(a, b,nZ,A,J,ET,Q,mode,S_i);

		// condizioni iniziali e finali
		double[] y = new double[] { S_i, 0 };

		//EI.integrate(ode, 0.0, y,  time, y);
		dp853.integrate(ode, 0.0, y, time, y);

		S_i=y[0];

		S=S_i;
		//System.out.println(S_i);
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

	private void storeResult(int dim,int t,int t_i,double[][] resultS,double[][] resultQ) throws SchemaException {
		outHMSout = new HashMap<Integer, double[]>();
		outHMQout = new HashMap<Integer, double[]>();

		for (int k=0;k<dim-1;k++){
			outHMSout.put(k, new double[]{resultS[t_i-1][k]});
			outHMQout.put(k, new double[]{resultQ[t_i-1][k]});
		}

	}
}