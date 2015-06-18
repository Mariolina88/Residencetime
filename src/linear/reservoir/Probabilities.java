package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;
import linear.reservoir.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.*;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.jgrasstools.gears.libs.modules.JGTModel;

import java.io.IOException;


public class Probabilities extends JGTModel{

	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;
	double J;

	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;
	double et;

	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;
	double Q;

	@Description("InputWaterStorage")
	@In
	public HashMap<Integer, double[]> inWaterStoragevalues;
	double S;
	
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

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;
	double p_ti;

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMTout;

	double dt;
	double inte=0;
	double inte2=0;
	double theta;

	ArrayList<InputData> datalist = new ArrayList<InputData>();

	@Execute
	public void process() throws Exception {
		outHMQout = new HashMap<Integer, double[]>();
		outHMTout = new HashMap<Integer, double[]>();
		datalist.add(new InputData(inPrecipvalues,inETvalues,inDischargevalues,inWaterStoragevalues));
		double pdfs= compute();	
		double theta= computeTheta();	
		outHMQout.put(ID, new double[]{pdfs});
		outHMTout.put(ID, new double[]{theta});
	}

	public double compute() throws IOException {
		dt=a/1000;
		for (InputData t : datalist){
			et=t.getET();
			Q=t.getQ();
			S=t.getWS();
			double ratio_t=((Q+et)/S);			
			double integrale=(ratio_t+inte)*dt;
			inte=ratio_t+inte;
			p_ti=Math.exp(-integrale);

		}

		System.out.println(p_ti);
		inte=0;

		return p_ti;
	}

	public double computeTheta() throws IOException {
		dt=a/1000;
		for (InputData t : datalist){
			et=t.getET();
			Q=t.getQ();
			S=t.getWS();
			if (mode == 1) {
				double ratio_t=((Q+et)/S);			
				double integrale=(ratio_t+inte)*dt;
				inte=ratio_t+inte;
				p_ti=Math.exp(-integrale);
				double ratio_theta=Q/S*p_ti;
				theta=(ratio_theta+inte2)*dt;
				inte2=ratio_theta+inte2;		
			}
			
			if (mode == 2) {
				theta=Q/(Q+et);		
			}
				
		}
	
		inte=0;
		inte2=0;
		return theta;
	}
 //ATTENTA AL DT CHE DEVE ESSERE SEMPRE UGUALE
	
}

