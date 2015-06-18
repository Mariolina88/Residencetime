package linear.reservoir;

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
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.joda.time.DateTime;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class OutOFTravelTimes extends JGTModel{


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

	@Description("Input pT")
	@In
	public HashMap<Integer, double[]> inPvalues;
	double pT;

	@Description("Theta")
	@In
	public HashMap<Integer, double[]>  thetaHM;
	double theta;
	public String pathToTheta;

	public String pathToPrec;
	public String pathToDischarge;
	public String pathToET;
	public String pathToS;
	public String pathToPT;
	public String pathToPet;

	public String tStartDateT;
	public String tEndDateT;
	public int inTimestepT;
	public String tStartDate;
	public String tEndDate;
	public int inTimestep;

	@Description("Station ID")
	@In
	public int ID;

	@Description("Parameter of linear Reservoir")
	@In
	public static double a ;

	@Description("The matrix of Q calcultaed with travel times")
	@Out
	public HashMap<Integer, double[]> outHMout;
	double integrale_Q;
	public String tStart;


	double dt;
	public int dim=7;

	double inte;
	ArrayList<provaInput> datalist = new ArrayList<provaInput>();
	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	@Execute
	public void process() throws Exception {
		outHMout = new HashMap<Integer, double[]>();
		for (int i=0;i<dim-1;i++)	{
			DateTime startDate = formatter.parseDateTime(tStartDate);
			DateTime EndDate = formatter.parseDateTime(tEndDate);
			startDate=startDate.plusHours(i);
			for (DateTime date = startDate; date.isBefore(EndDate); date = date.plusHours(1))	{
				String StartDate=date.toString(formatter);
				provaInput dataInput=new provaInput(pathToPrec,pathToDischarge,pathToET,pathToS, 
						pathToTheta,pathToPT,pathToPet,tStartDateT,
						tEndDateT,StartDate,tEndDate,inTimestep,ID);
				//datalist.add(dataInput);
				//dim=datalist.size();
				J=dataInput.precipitation;

				System.out.println(J);


				double pdfs= computeOUT();		
				outHMout.put(ID, new double[]{pdfs});
			}
		}

	}

	public double computeOUT() throws IOException {
		dt=a/1000;
		System.out.println("ciao");
		return dt;

	}	

}
