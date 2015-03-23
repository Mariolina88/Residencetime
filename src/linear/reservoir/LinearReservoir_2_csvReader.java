package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.doubleNovalue;
import integrali.EquazioneDifferenziale;
import integrali.TimeStepIntegrator;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

import com.opencsv.CSVWriter;



public class LinearReservoir_2_csvReader extends JGTModel {


	@Description("The first day of the simulation.")
	@In
	public String tStartDate = null;

	@Description("The last day of the simulation.")
	@In
	public String tEndDate = null;
	@Description("The time step in minutes of the measurement.")
	@In
	public int inTimestep;

	@Description("")
	@In
	public boolean doDaily;

	@Description("Water storage")
	@Out
	public static double S;

	@Description("Precipitation")
	@In
	public static Double[] J;

	@Description("Discharge")
	@Out
	public static Double[] Q;

	@Description("ET")
	@In
	public static double ET=0;

	@Description("Path to the precipitation file in input.")
	@In
	public String inPathToPrec = null;

	@Description("Path to the discharge file in input.")
	@In
	public String inPathToDischarge = null;

	@Description("Path to the net radiation file in input.")
	@In
	public String inPathToNetRadiation = null;

	@Description("Path to the air temperature file in input.")
	@In
	public String inPathToAirTemperature = null;

	@Description("Path to the pressure file in input.")
	@In
	public String inPathToPressure = null;
	
	public String pathToOutput = null;


	@Description("Injection time")
	@In
	public static double tin = 0;

	@Description("Exit time")
	@In
	public static double tex ;

	@Description("Travel time")
	@In
	public static double t = tex - tin;

	@Description("Area")
	@In
	public static double a = 2;

	@Description("Parameter of linear Reservoir")
	@In
	public static double b = 2;

	@Description("Parameter alpha Priestly Taylor")
	@In
	public static double alpha = 1.26;

	@Description("Parameter ggm Priestly Taylor")
	@In
	public static double ggm = 0.35;

	@Description("Parameter ggn Priestly Taylor")
	@In
	public static double ggn = 0.75;

	public int nrow;


	public void process() throws IOException {
		LetturaInput_csvReader matriciP=new LetturaInput_csvReader(inPathToPrec);
		String [][] precipitation_matrix=matriciP.matrix; 
		
		int nrow=precipitation_matrix.length;
		this.nrow=nrow;
		Double J[]=new Double [nrow];

		
		for (int i = 0; i < precipitation_matrix.length; i++) {
			J[i]=Double.parseDouble(precipitation_matrix[i][2]);
			//System.out.print(J[i]);
		}

		this.J=J;
		
		LetturaInput_csvReader matriciD=new LetturaInput_csvReader(inPathToDischarge);
		String [][] discharge_matrix=matriciD.matrix; 
		
		Double Q[]=new Double [nrow];
		
		this.Q=Q;
		
		for (int i = 0; i < discharge_matrix.length; i++) {
			Q[i]=Double.parseDouble(discharge_matrix[i][2]);
			//System.out.print(Q[i]);
		}

		LetturaInput_csvReader matriciAT=new LetturaInput_csvReader(inPathToAirTemperature);
		String [][] airT_matrix=matriciAT.matrix; 
		
		Double airT[]=new Double [nrow];
		
		for (int i = 0; i < airT_matrix.length; i++) {
			airT[i]=Double.parseDouble(airT_matrix[i][2]);
			//System.out.print(Q[i]);
		}

		LetturaInput_csvReader matriciNR=new LetturaInput_csvReader(inPathToNetRadiation);
		String [][] netradiation_matrix=matriciNR.matrix; 
		
		Double netRad[]=new Double [nrow];
		
		for (int i = 0; i < netradiation_matrix.length; i++) {
			netRad[i]=Double.parseDouble(netradiation_matrix[i][2]);
			//System.out.print(Q[i]);
		}

		LetturaInput_csvReader matriciPR=new LetturaInput_csvReader(inPathToPressure);
		String [][] pressure_matrix=matriciPR.matrix; 
		
		Double pressure[]=new Double [nrow];
		
		for (int i = 0; i < pressure_matrix.length; i++) {
			pressure[i]=Double.parseDouble(pressure_matrix[i][2]);
			//System.out.print(Q[i]);
		}

		tStartDate=precipitation_matrix[0][1];
		tEndDate=precipitation_matrix[nrow-1][1];

		DateTimeFormatter formatter = DateTimeFormat.forPattern(
				"yyyy-MM-dd HH:mm").withZone(DateTimeZone.UTC);
		DateTime startcurrentDatetime = formatter.parseDateTime(tStartDate);
		System.out.println(startcurrentDatetime);

		DateTime endcurrentDatetime = formatter.parseDateTime(tEndDate);

		long diff = (endcurrentDatetime.getMillis() - startcurrentDatetime
				.getMillis()) / (1000 * 60 * 60);

		DateTime array[] = new DateTime[(int) diff];

		if (doDaily == false) {
			for (int i = 0; i < array.length; i++) {
				array[i] = startcurrentDatetime.plusHours(i);
			}
		}
		if (doDaily == true) {
			for (int i = 0; i < array.length; i++) {
				array[i] = startcurrentDatetime.plusDays(i);
			}
		}
		

		double [] timeIntegral = new double [nrow];
		double [] S = new double [nrow];
		double [] pE = new double [nrow];
		double [] theta = new double [nrow];
		double [] pT = new double [nrow];
		double [] Qout = new double [nrow];

		for (int i = 1; i < nrow; i++) {
		SimpsonIntegrator simpson = new SimpsonIntegrator();
		TimeStepIntegrator timef = new TimeStepIntegrator();
		timeIntegral[i]= simpson.integrate(10, timef, tin, i);
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,1000.0, 1.0e-10, 1.0e-10);
		double c=J[i];
		FirstOrderDifferentialEquations ode = new EquazioneDifferenziale(a, b, c,ET);
		// condizioni iniziali e finali
		this.t=nrow-tin;
		double[] y = new double[] { 0.0, 10000.0 };
		dp853.integrate(ode, 0.0, y, t, y);
		S[i]=y[0];
		
		Q[i] = a * (Math.pow(S[i], b));
		System.out.print(Q[i]+ "\t");
		
		pE [i]= Math.exp(-Q[i] / S[i] * timeIntegral[i]);
		theta[i] = (Q[i] / S[i] * timeIntegral[i]) * pE [i];
		pT[i] = Q[i] / (theta[i] * S[i]) * pE [i];
		Qout[i]= J[i] * pT[i] * timeIntegral[i];
		
	    
		
		}


	}
		
	
	
}


