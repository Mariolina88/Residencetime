package linear.reservoir;


import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

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


public class Probabilities4 extends JGTModel{

	@Description("Input Q ")
	@In
	public HashMap<Integer, double[]> inDischargevalues;
	double Q;

	@Description("Input ET ")
	@In
	public HashMap<Integer, double[]> inETvalues;
	double ET;

	@Description("Input S")
	@In
	public HashMap<Integer, double[]> inWaterStoragevalues;
	double S;


	@Description("Input injection time ")
	@In
	public HashMap<Integer, double[]> inTimevalues;

	@Description("mode 1: Mean Theta  (Botter et al.) "
			+ "mode=2 Theta=Q(t)/[Q(t)+ET(t)]")
	@In
	public int mode;
	double theta;

	@Description("Station ID")
	@In
	public int ID;

	@Description("First date of the simulation")
	@In
	public String tStartDate;

	@Description("Last date of the simulation")
	@In
	public String tEndDate;

	@Description("time step of the simulation")
	@In
	public int inTimestep;

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;
	double p_ti;

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMTout;

	//integration time
	double dt;


	double integrale;

	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	//dimension of the output matrix 
	int dim;
	int t;

	@Execute
	public void process() throws Exception {
		//integration time
		dt=1E-4;

		DateTime start = formatter.parseDateTime(tStartDate);
		DateTime end = formatter.parseDateTime(tEndDate);
		dim=Hours.hoursBetween(start, end).getHours()+1;
		double[]PT = new double[dim];
		double[]ThetaT = new double[dim];


		for (t=0;t<dim;t++){

			Integer basinId = t;

			Q =inDischargevalues.get(basinId)[0];
			if (isNovalue(Q)) {
				Q= 0;
			} else {
				Q = inDischargevalues.get(basinId)[0];
			}

			S =inWaterStoragevalues.get(basinId)[0];
			if (isNovalue(S)) {
				S= 0;
			} else {
				S = inWaterStoragevalues.get(basinId)[0];
			}

			ET = inETvalues.get(ID)[0];
			if (isNovalue(ET)||ET>S) {
				ET= 0;
			} else {
				ET = inETvalues.get(ID)[0];
			}
		
			double pdfs= compute();	
			double theta= computeTheta();
			PT[t]=pdfs;
			ThetaT[t]=theta;
		}


		t=0;
		integrale=0;
		theta=0;

		storeResult(dim,t,PT,ThetaT);
		PT = new double[dim];
		ThetaT = new double[dim];
	}


	public double compute() throws IOException {

		if(S==0) p_ti=0;
		else{
			integrale+=((Q+ET)/S)*dt;
			p_ti=Math.exp(-integrale); }

		System.out.println(p_ti);
		return p_ti;
	}

	public double computeTheta() throws IOException {
		
		if(S==0) theta=0;
		if (mode == 1) theta+=Q/S*p_ti*dt;
		else if (mode == 2) theta=Q/(Q+ET);
				
		return theta;
	}


	private void storeResult(int dim,int t,double[]PT,double[]ThetaT) throws SchemaException {
		outHMQout = new HashMap<Integer, double[]>();
		outHMTout = new HashMap<Integer, double[]>();
		for (int k=0;k<dim;k++){
			outHMQout.put(k, new double[]{PT[k]});
			outHMTout.put(k, new double[]{ThetaT[k]});
		}

	}

}

