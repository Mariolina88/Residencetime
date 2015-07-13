package linear.reservoir;


import java.util.HashMap;

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


public class Probabilities2 extends JGTModel{

	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;
	double J;

	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;
	double ET;
	public String pathToET;

	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;
	double Q;
	public String pathToDischarge;

	@Description("InputWaterStorage")
	@In
	public HashMap<Integer, double[]> inWaterStoragevalues;
	double S;
	public String pathToS;
	
	@Description("mode 1: Mean Theta  (Botter et al.) "
			+ "mode=2 Theta=Q(t)/[Q(t)+ET(t)]")
	@In
	public int mode;
	

	@Description("Station ID")
	@In
	public int ID;
	
	@Description("Parameter of linear Reservoir")
	@In
	public static double a ;
	
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

	double dt;
	double inte;
	double inte2=0;
	double theta;
	
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
	int endStore;


	@Execute
	public void process() throws Exception {
		DateTime start = formatter.parseDateTime(tStartDate);
		DateTime end = formatter.parseDateTime(tEndDate);
		dim=Hours.hoursBetween(start, end).getHours()+1;
		double []PT=new double[dim-t_i];
		double []ThetaT=new double[dim-t_i];
		startDate = formatter.parseDateTime(tStartDate);
		
		for (t=0;t<dim-t_i;t++){
			StartDate_ti=startDate.plusHours(t_i);	
			StartDate_t=StartDate_ti.plusHours(t);
			String startDate_ti=StartDate_ti.toString(formatter);
			String startDate_t=StartDate_t.toString(formatter);
			InputP dataInput=new InputP(pathToS,pathToDischarge,pathToET,startDate_t,startDate_ti,tEndDate,inTimestep,ID,dim,t,t_i);
			S=dataInput.storage;
			Q=dataInput.discharge;
			ET=dataInput.ET;
			if(ET>S){
				ET=0;
			}
			double pdfs= compute();	
			double theta= computeTheta();
			PT[t]=pdfs;
			ThetaT[t]=theta;
			endStore=dim-t_i;
		}
		
		
		t=0;
		storeResult(endStore,PT,ThetaT);
		t_i=t_i+1;
		inte=0;
		inte2=0;
		
		if (t_i==dim){
			double []fin=new double[dim];
			endStore=dim;
			storeResult(endStore,fin,fin);
		}

		
	}

	public double compute() throws IOException {
		dt=a/1000;
		
		if(S==0){
			p_ti=0;
		}else{
			double ratio_t=((Q+ET)/S);			
			double integrale=(ratio_t*dt+inte);
			
			p_ti=Math.exp(-integrale);
			inte=integrale;}

		System.out.println(p_ti);
		return p_ti;
	}

	public double computeTheta() throws IOException {
		dt=a/1000;

			if (mode == 1) {
				double ratio_theta=Q/S*p_ti;
				theta=(ratio_theta+inte2)*dt;
				inte2=ratio_theta+inte2;		
			}
			
			if (mode == 2) {
				theta=Q/(Q+ET);		
			}

		return theta;
	}
	private void storeResult(int endStore,double[] PT,double[]ThetaT) throws SchemaException {
		outHMQout = new HashMap<Integer, double[]>();
		outHMTout = new HashMap<Integer, double[]>();
		for (int k=0;k<endStore;k++){
			outHMQout.put(k, new double[]{PT[k]});
			outHMTout.put(k, new double[]{ThetaT[k]});
		}

	}
	
}

