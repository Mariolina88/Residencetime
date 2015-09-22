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

public class TravelTimes4 extends JGTModel{


	double J;
	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;


	double ET;
	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;


	double Q;
	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;


	double S;
	@Description("InputWaterStorage")
	@In
	public HashMap<Integer, double[]> inWaterStoragevalues;


	double p;
	@Description("Input p")
	@In
	public HashMap<Integer, double[]> inPvalues;

	@Description("Input injection times")
	@In
	public HashMap<Integer, double[]> inTimevalues;

	@Description("Input theta")
	@In	
	public HashMap<Integer, double[]>  inthetavalues;
    
	
	@Description("Theta: mode 1 --> values from the matrix,"
			   + " mode 2 --> fixed values --> theta_i")
	@In
	public int mode;
	
	double theta;
	@Description("Input theta")
	@In	
	public double theta_i;
	

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


	//time
	int t=0;



	@Execute
	public void process() throws Exception {
		
		DateTime start = formatter.parseDateTime(tStartDate);
		DateTime end = formatter.parseDateTime(tEndDate);
		dim=Hours.hoursBetween(start, end).getHours()+1;
		
		double[]resultPt = new double[dim];
		double[]resultPet = new double[dim];

		double[]Qout=new double[dim];
		double[]ETout=new double[dim];


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

				J = inPrecipvalues.get(ID)[0];
				if (isNovalue(J)) {
					J= 0;
				} else {
					J = inPrecipvalues.get(ID)[0];
				}


				p = inPvalues.get(basinId)[0];
				if (isNovalue(p)) {
					p= 0;
				} else {
					p = inPvalues.get(basinId)[0];
				}

				if (mode==1){
					theta = inthetavalues.get(dim)[0];
					if (isNovalue(theta)) {
						theta= 0;
					} else {
						theta = inthetavalues.get(dim)[0];
					}
				}
				if(mode==2) theta=theta_i;			
				

				double pT= computePT();
				double pET= computePET();
				resultPt[t]=pT;
				resultPet[t]=pET;
				double Qo= computeQ();
				double ETo= computeET();
				Qout[t]=Qo;
				ETout[t]=ETo;
			}
			//t=0;

			storeResult(dim,resultPt,resultPet,Qout,ETout);

		}

		
	public double computePT() throws IOException {
		pT= (p==0)? pT=0 : Q/(theta*S)*p;
		return pT;
	}


	public double computePET() throws IOException {
		pET= (p==0)? pET=0 : ET/((1-theta)*S)*p;
		return pET;
	}

	public double computeQ() throws IOException {
		Qout = (pT==0)? 0 :J*theta*pT;
		return Qout;
	}


	public double computeET() throws IOException {
		ETout = (pET==0)? 0 : J*(1-theta)*pET;

		return ETout;
	}

	private void storeResult(int dim,double[] PtV,double[]PetV, double[]Qout, double[]ETout) throws SchemaException {
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
