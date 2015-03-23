package linear.reservoir;

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


public class LinearReservoir_3_OmsConInput extends JGTModel {

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
	public static double J;

	@Description("Discharge")
	@Out
	public static double Q;

	@Description("ET")
	@In
	public static double ET;

	@Description("Path to the precipitation file in input.")
	@In
	public String inPathToPrec = null;

	@Description("Path to the discharge file in input.")
	@In
	public String inPathToDischarge = null;

	@Description("Path to the ET file in input.")
	@In
	public String inPathToET = null;

	/*
	@Description("Path to the net radiation file in input.")
	@In
	public String inPathToNetRadiation = null;

	@Description("Path to the air temperature file in input.")
	@In
	public String inPathToAirTemperature = null;

	@Description("Path to the pressure file in input.")
	@In
	public String inPathToPressure = null;
	 */

	public HashMap<Integer, double[]> precipvalues;

	public HashMap<Integer, double[]> dischargevalues;

	public HashMap<Integer, double[]> ETvalues;


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

	@Description("Id field in the csv file")
	@In
	String idStation = "ID";

	@Description("Id of the station")
	@In
	public int id;

	@Description("Path to the output file.")
	@Out
	public String pathToQout = null;

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;

	public DateTime array[] ;

	double timeIntegral;

	public void process() throws Exception {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(
				"yyyy-MM-dd HH:mm").withZone(DateTimeZone.UTC);
		DateTime startcurrentDatetime = formatter.parseDateTime(tStartDate);
		System.out.println(startcurrentDatetime);

		DateTime endcurrentDatetime = formatter.parseDateTime(tEndDate);

		long diff = (endcurrentDatetime.getMillis() - startcurrentDatetime
				.getMillis()) / (1000 * 60 * 60);

		this.array = new DateTime[(int) diff];
		

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

		OmsTimeSeriesIteratorReader reader_precipitation = new OmsTimeSeriesIteratorReader();

		if (!((inPathToPrec == null))) {

			reader_precipitation.initProcess();
			reader_precipitation.file = inPathToPrec;
			reader_precipitation.idfield = idStation;
			reader_precipitation.tStart = tStartDate;
			reader_precipitation.tEnd = tEndDate;
			reader_precipitation.fileNovalue = "-9999";
			reader_precipitation.tTimestep = inTimestep;
		}

		OmsTimeSeriesIteratorReader reader_discharge = new OmsTimeSeriesIteratorReader();

		if (!((inPathToDischarge == null))) {

			reader_discharge.initProcess();
			reader_discharge.file = inPathToDischarge;
			reader_discharge.idfield = idStation;
			reader_discharge.tStart = tStartDate;
			reader_discharge.tEnd = tEndDate;
			reader_discharge.fileNovalue = "-9999";
			reader_discharge.tTimestep = inTimestep;

		}

		OmsTimeSeriesIteratorReader reader_ET = new OmsTimeSeriesIteratorReader();

		if (!((inPathToET == null))) {

			reader_ET.initProcess();
			reader_ET.file = inPathToET;
			reader_ET.idfield = idStation;
			reader_ET.tStart = tStartDate;
			reader_ET.tEnd = tEndDate;
			reader_ET.fileNovalue = "-9999";
			reader_ET.tTimestep = inTimestep;

		}

		OmsTimeSeriesIteratorWriter writer = new OmsTimeSeriesIteratorWriter();

		writer.file = pathToQout;
		writer.tStart = tStartDate;
		writer.tTimestep = inTimestep;
		writer.fileNovalue="-9999";


		for (int i = 0; i < array.length; i++) {
			this.tex=i+1;
			outHMQout = new HashMap<Integer, double[]>();


			if (!(inPathToPrec == null)) {
				reader_precipitation.nextRecord();
				precipvalues = reader_precipitation.outData;
			}

			if (!(inPathToDischarge == null)) {
				reader_discharge.nextRecord();
				dischargevalues = reader_discharge.outData;
			}

			if (!(inPathToET == null)) {
				reader_ET.nextRecord();
				ETvalues = reader_ET.outData;
			}

			double precipitation = doubleNovalue;
			if (precipvalues != null) {
				precipitation = precipvalues.get(id)[0];
			}


			double discharge = doubleNovalue;
			if (dischargevalues != null) {
				discharge = dischargevalues.get(id)[0];
			}

			double ET = doubleNovalue;
			if (ETvalues != null) {
				ET = ETvalues.get(id)[0];
			}


			double pdf= compute(precipitation,discharge,ET);

			outHMQout.put(id, new double[]{pdf});

			writer.inData = outHMQout;
			writer.writeNextLine();

			if (pathToQout != null) {
				writer.close();
			}


		}
	}



	  public double computeIntegral (int tin, int tex){	
			SimpsonIntegrator simpson = new SimpsonIntegrator();
			TimeStepIntegrator timef = new TimeStepIntegrator();
			this.timeIntegral = simpson.integrate(10, timef, tin, tex);		
			return timeIntegral;
		}


		public double compute(double J, double Q, double ET) throws IOException {
				FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,1000.0, 1.0e-10, 1.0e-10);
				FirstOrderDifferentialEquations ode = new EquazioneDifferenziale(a, b, J,ET);
				// condizioni iniziali e finali
				double[] y = new double[] { 0.0, 10000.0 };
				dp853.integrate(ode, 0.0, y, tex-tin, y);
				S=y[0];
				Q = a * (Math.pow(S, b));
				System.out.print(Q);
				double p = Math.exp(-(Q+ET) / S );		
				double theta = ((Q / S) * timeIntegral) * p;
				double pT = Q / (theta * S) * p;
				double pET = ET / ((1-theta) * S) * p;
				double Qout = J * pT*timeIntegral;					
				return Qout;

		}



}


