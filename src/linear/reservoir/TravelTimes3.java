package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.doubleNovalue;
import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;
import linear.reservoir.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.*;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.geotools.feature.SchemaException;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class TravelTimes3 extends JGTModel{

	public HashMap<Integer, double[]> inPrecipvalues;
	double J;
	@Description("Input Precipitation")
	@In
	public String pathToPrec;
	
	public HashMap<Integer, double[]> inETvalues;
	double ET;
	@Description("Input ET")
	@In
	public String pathToET;

	public HashMap<Integer, double[]> inDischargevalues;
	double Q;
	@Description("InputDischarge")
	@In
	public String pathToDischarge;

	public HashMap<Integer, double[]> inWaterStoragevalues;
	double S;
	@Description("InputWaterStorage")
	@In
	public String pathToS;

	public HashMap<Integer, double[]> inPvalues;
	double p;
	@Description("Input p")
	@In
	public String pathToP;
	
	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inTimevalues;
	
	public HashMap<Integer, double[]>  thetaHM;
	double theta;
	public double theta_i;
	@Description("Theta")
	@In
	public String pathToTheta;

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

	@Description("The matrix of pT")
	@Out
	public HashMap<Integer, double[]> outHMpTout;
	double pT;

	@Description("The matrix of pET")
	@Out
	public HashMap<Integer, double[]> outHMpETout;
	double pET;
	
	@Description("The matrix of Q")
	@Out
	public HashMap<Integer, double[]> outHMQout;
	double Qout;

	@Description("The matrix of ET")
	@Out
	public HashMap<Integer, double[]> outHMETout;
	double ETout;

	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	//dimension of the output matrix 
	int dim;
	//injection time
	public int mode;
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
		double[]resultPt = new double[dim];
		double[]resultPet = new double[dim];
		
		double[]Qout=new double[dim];
		double[]ETout=new double[dim];
		

		startDate = formatter.parseDateTime(tStartDate);
		Set<Entry<Integer, double[]>> entrySet = inTimevalues.entrySet();
		for( Entry<Integer, double[]> entry : entrySet ) {


			double ti = entry.getValue()[0];

			
			int t_i=(int) ti;

		for (t=0;t<dim;t++){
			StartDate_ti=startDate.plusHours(t_i);	
			StartDate_t=StartDate_ti.plusHours(t);
			String startDate_ti=StartDate_ti.toString(formatter);
			InputTT dataInput=new InputTT(pathToPrec,pathToS,pathToDischarge,pathToET, pathToP,pathToTheta,
					tStartDate,startDate_ti,tEndDate,inTimestep,ID,dim,t,t_i);
			J=dataInput.precipitation;
			S=dataInput.storage;
			Q=dataInput.discharge;
			ET=dataInput.ET;
			if(ET>S){
				ET=0;
			}
			p=dataInput.P;
			if (mode==1){
			theta=dataInput.theta;
			}
			if(mode==2){
				theta=theta_i;			
			}
			double pT= computePT();
			double pET= computePET();
			resultPt[t]=pT;
			resultPet[t]=pET;
			double Qo= computeQ();
			double ETo= computeET();
			Qout[t]=Qo;
			ETout[t]=ETo;
		}
		t=0;
		t_i=t_i+1;
		/*if(t_i>=dim-2){
			System.exit(0);
		}*/
		storeResult(dim,t,t_i,resultPt,resultPet,Qout,ETout);
		
		}

	}	


	public double computePT() throws IOException {
		if(p==0){
			pT=0;
		}else{
		pT=Q/(theta*S)*p;		
		System.out.println(pT);
		}
		return pT;
	}


	public double computePET() throws IOException {
		if(p==0){
			pET=0;
		}else{
		pET=ET/((1-theta)*S)*p;	
		}
		return pET;
	}
	
	public double computeQ() throws IOException {
		if(pT==0){
			Qout=0;
		}else{
		Qout= J*theta*pT;
		System.out.println(Qout);
		}
		return Qout;
	}


	public double computeET() throws IOException {
		if(pET==0){
			ETout=0;
		}else{
		ETout= J*(1-theta)*pET;
		}
		return ETout;
	}

	private void storeResult(int dim,int t,int t_i,double[] PtV,double[]PetV, double[]Qout, double[]ETout) throws SchemaException {
		outHMpTout = new HashMap<Integer, double[]>();
		outHMpETout = new HashMap<Integer, double[]>();
		outHMQout = new HashMap<Integer, double[]>();
		outHMETout = new HashMap<Integer, double[]>();
		for (int k=0;k<dim;k++){
			outHMpTout.put(k, new double[]{PtV[k]});
			outHMpETout.put(k, new double[]{PetV[k]});
			outHMQout.put(k, new double[]{Qout[k]});
			outHMETout.put(k, new double[]{ETout[k]});
		}

	}
}
