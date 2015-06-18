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

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.libs.modules.JGTModel;

import java.io.IOException;

public class TravelTimes extends JGTModel{

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

	@Description("Input p")
	@In
	public HashMap<Integer, double[]> inPvalues;
	double p;

	@Description("Theta")
	@In
	public HashMap<Integer, double[]>  thetaHM;
	double theta;
	public String pathToTheta;
	public String tStartDateT;
	public String tEndDateT;
	public int inTimestepT;

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

	ArrayList<InputDataP> datalist = new ArrayList<InputDataP>();


	@Execute
	public void process() throws Exception {
		outHMpTout = new HashMap<Integer, double[]>();
		outHMpETout = new HashMap<Integer, double[]>();
		datalist.add(new InputDataP(inPrecipvalues,inETvalues,inDischargevalues,inWaterStoragevalues,inPvalues));
		int dim=datalist.size();
		OmsTimeSeriesIteratorReader reader_theta = new OmsTimeSeriesIteratorReader();
		if (!((pathToTheta == null))) {
			reader_theta.file = pathToTheta ;
			reader_theta.idfield = "ID";
			reader_theta.tStart = tStartDateT;
			reader_theta.tEnd = tEndDateT;
			reader_theta.fileNovalue = "-9999";
			reader_theta.tTimestep = inTimestepT;
			reader_theta.initProcess();
		}
		for (int i = 0; i <dim; i++) {
			if (!(pathToTheta == null)) {
				reader_theta.nextRecord();
				thetaHM = reader_theta.outData;
			}

			if (thetaHM != null) {
				theta = thetaHM.get(ID)[0];
			}
		}	


		double pT= computePT();		
		outHMpTout.put(ID, new double[]{pT});
		double pET= computePET();		
		outHMpETout.put(ID, new double[]{pET});
	}

	public double computePT() throws IOException {

		for (InputDataP t : datalist){
			S=t.getWS();
			Q=t.getQ();
			p=t.getp();
			pT=Q/(theta*S)*p;		
		}

		System.out.println(pT);
		return pT;
	}


	public double computePET() throws IOException {

		for (InputDataP t : datalist){
			S=t.getWS();
			et=t.getET();
			p=t.getp();
			pET=et/((1-theta)*S)*p;			
		}

		return pET;
	}


}
