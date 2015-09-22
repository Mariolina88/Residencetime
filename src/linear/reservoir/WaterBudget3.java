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
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.*;


public class WaterBudget3 extends JGTModel{

	double J;	
	@Description("Precipitation")
	@In
	public String pathToPrec;

	double Q;
	@Description("Discharge")
	@In
	public String pathToDischarge;

	double ET;
	@Description("ET")
	@In
	public String pathToET;
	
	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inTimevalues;

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
	public static double dt ;

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


	@Description("mode 0:NonLinearReservoir, "
			+ "else:J, Q and ET are external values ")
	@In
	public String mode;
	
	int modeInt;

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
	
	@Description("The matrix of output ET")
	@Out
	public HashMap<Integer, double[]> outHMETout;

	double dischargeQ;

	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	//dimension of the output matrix 
	int dim;
	//injection time
	DateTime StartDate_ti;
	//time
	int t=0;
	DateTime StartDate_t;
	DateTime startDate;


	@Execute
	public void process() throws Exception {
		modeInt=mode.indexOf("Non");
		
		DateTime start = formatter.parseDateTime(tStartDate);
		DateTime end = formatter.parseDateTime(tEndDate);
		dim=Hours.hoursBetween(start, end).getHours()+1;
		double[] resultS = new double[dim];
		double[] resultQ = new double[dim];

		startDate = formatter.parseDateTime(tStartDate);

		Set<Entry<Integer, double[]>> entrySet = inTimevalues.entrySet();
		for( Entry<Integer, double[]> entry : entrySet ) {


			double ti = entry.getValue()[0];

			
			int t_i=(int) ti;


			for (t=0;t<dim-t_i;t++){
				StartDate_ti=startDate.plusHours(t_i);	
				StartDate_t=StartDate_ti.plusHours(t);
				String StartDate=StartDate_t.toString(formatter);		
				InputWB dataInput=new InputWB(pathToPrec,pathToDischarge,pathToET,StartDate,tEndDate,inTimestep,ID);
				J=dataInput.precipitation;
				Q=dataInput.discharge;
				ET=dataInput.ET;
				Storage= compute(J,Q,ET);
				dischargeQ= computeQ(modeInt,S,Q);
				resultS[t_i+t]=Storage;
				resultQ[t_i+t]=dischargeQ;

			}

			storeResult(dim,t,t_i,resultS,resultQ);
			resultS = new double[dim];
			resultQ = new double[dim];

			S_i=0;


		}

	}

	public double compute(double J, double Q, double ET) throws IOException {
		dt=1E-4;

		//FirstOrderIntegrator EI=new EulerIntegrator(1);
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,dt, 1.0e-10, 1.0e-10);
		FirstOrderDifferentialEquations ode = new EquazioneDifferenzialeMode(a, b,nZ,A,J,ET,Q,modeInt,S_i);

		// condizioni iniziali e finali
		double[] y = new double[] { S_i, 0 };

		//EI.integrate(ode, 0.0, y,  time, y);
		dp853.integrate(ode, 0.0, y, dt, y);

		S_i=y[0];
		S=S_i;


		if (modeInt==0) S=S_i*nZ;
		if (S<0) S=0;
		
		System.out.println(S);
		return S;
	}


	public double computeQ(int modeInt,double S,double Q) throws IOException {
		Q = (modeInt==0) ? a*Math.pow(S, b) : Q/A*3.6;	
		return Q;

	}

	private void storeResult(int dim,int t,int t_i,double[]resultS,double[]resultQ) throws SchemaException, IOException {
		outHMSout = new HashMap<Integer, double[]>();
		outHMQout = new HashMap<Integer, double[]>();

		for (int k=0;k<dim;k++){
			outHMSout.put(k, new double[]{resultS[k]});
			outHMQout.put(k, new double[]{resultQ[k]});

		}

	}
}