package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.jgrasstools.gears.libs.modules.JGTModel;

import java.io.IOException;


public class TravelProbabilities extends JGTModel{

	@Description("Integration time")
	@In
	public double time ;
	public int mode;
	public double A;
	public double ti;
	
	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;

	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;

	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;

	@Description("InputWaterStorage")
	@In
	public HashMap<Integer, double[]> inWaterStoragevalues;

	double J_ti;
	double Q_ti;
	double et_ti;
	double S_ti;
	int i=-1;
	int id;
	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;
	
	@Execute
	public void process() throws Exception {
		Array data=new Array(inPrecipvalues, inETvalues,inDischargevalues,inWaterStoragevalues);
		System.out.println(data);	
		//double pdfs= compute(data);
		//outHMQout.put(id, new double[]{pdfs});

	}

	public double compute(GenerateArray data) throws IOException {
		
	
		
		
		/*
		
		
		if (S>0){
		integralp=(Q+ET)/S;	
		integralptot=integralp1+integralp;
		integralp1=integralptot;
		p = Math.exp(-integralp);
		
		
		//theta=theta1+Q/S*p;
		theta=Q/S*p;
		theta1=theta;
		pT = Q / (theta * S) * p;
		pET = ET / ((1-theta) * S) * p;
		}
		
		
		Sout=J*p;
		Qout=a * (Math.pow(Sout, b));
		
		System.out.println("Qout"+p+ "\t");
		return Qout;
*/
		return A;
	}


}

