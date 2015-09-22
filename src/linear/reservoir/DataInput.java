package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.ArrayList;
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


public class DataInput extends JGTModel{


	@Description("Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;

	double J;


	@Description("Input Q ")
	@In
	public HashMap<Integer, double[]> inDischargeInputvalues;

	double Qinput;

	@Description("Input ET ")
	@In
	public HashMap<Integer, double[]> inETvalues;

	double ET;

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

	@Description("The matrix of the inut precipitation")
	@Out
	public HashMap<Integer, double[]> outHMJout;

	@Description("The matrix of the input discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;

	@Description("The matrix of the input ET")
	@Out
	public HashMap<Integer, double[]> outHMETout;


	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	//dimension of the output matrix 
	int dim;
	//injection time
	ArrayList<Double> valueP = new ArrayList<Double>();
	ArrayList<Double> valueQ = new ArrayList<Double>();
	ArrayList<Double> valueET = new ArrayList<Double>();
	Double[] vectorJ;
	Double[] vectorQ;
	Double[] vectorET;
	int t_i;


	@Execute
	public void process() throws Exception {
		DateTime start = formatter.parseDateTime(tStartDate);
		DateTime end = formatter.parseDateTime(tEndDate);
		dim=Hours.hoursBetween(start, end).getHours()+1;

		
		Set<Entry<Integer, double[]>> entrySet = inTimevalues.entrySet();
		for( Entry<Integer, double[]> entry : entrySet ) {


			double ti = entry.getValue()[0];


			t_i=(int) ti;
			
			//for (int t=0;t<dim;t++){

			J =inPrecipvalues.get(ID)[0];
			if (isNovalue(J)) {
				J= 0;
			} else {
				J = inPrecipvalues.get(ID)[0];
			}


			Qinput =inDischargeInputvalues.get(ID)[0];
			if (isNovalue(Qinput)) {
				Qinput= 0;
			} else {
				Qinput = inDischargeInputvalues.get(ID)[0];
			}


			ET = inETvalues.get(ID)[0];
			if (isNovalue(ET)) {
				ET= 0;
			} else {
				ET = inETvalues.get(ID)[0];
			}

			valueP.add(J);
			valueQ.add(Qinput);
			valueET.add(ET);
			
			
		}
		vectorJ=toArray(valueP, dim);
		vectorQ=toArray(valueQ, dim);
		vectorET=toArray(valueET, dim);
		storeResult(t_i,vectorJ,vectorQ,vectorET);

	}


	public Double[] toArray(ArrayList<Double> value, int dim) {
		Double[] vector = new Double[dim];
		value.toArray(vector);	
		return vector;
	}



	private void storeResult(int t_i,Double[]vectorJ,Double[]vectorQ,Double[]vectorET ) throws SchemaException, IOException {
		outHMJout = new HashMap<Integer, double[]>();
		outHMQout = new HashMap<Integer, double[]>();
		outHMETout= new HashMap<Integer, double[]>();

		for (int k=0;k<t_i+1;k++){
			outHMJout.put(k, new double[]{vectorJ[k]});
			outHMQout.put(k, new double[]{vectorQ[k]});
			outHMETout.put(k, new double[]{vectorET[k]});
		}

	}
}