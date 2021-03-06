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

import org.geotools.feature.SchemaException;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class OutOfTravelTimes extends JGTModel{

	public HashMap<Integer, double[]> inQoutvalues;
	double Qout;
	@Description("Discharge")
	@In	
	public String pathToQout;

	public HashMap<Integer, double[]> inEToutvalues;
	double ETout;
	@Description("Input ET")
	@In
	public String pathToETout;

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

	@Description("a")
	@In
	public double a;

	@Description("The matrix of Q")
	@Out
	public HashMap<Integer, double[]> outHMQ;
	double Q_t_i;

	@Description("The matrix of ET")
	@Out
	public HashMap<Integer, double[]> outHMET;
	double ET_t_i;

	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	double dt;
	//dimension of the output matrix 
	int dim;

	DateTime StartDate_ti;
	//time
	int t=0;
	int t_i;

	double inte;
	double inte2;
	DateTime StartDate_t;
	DateTime startDate;
	int endStore;


	@Execute
	public void process() throws Exception {

		DateTime start = formatter.parseDateTime(tStartDate);
		DateTime end = formatter.parseDateTime(tEndDate);
		dim=Hours.hoursBetween(start, end).getHours()+1;


			
		Set<Entry<Integer, double[]>> entrySet = inTimevalues.entrySet();
		for( Entry<Integer, double[]> entry : entrySet ) {


			double ti = entry.getValue()[0];
			
		
			t_i=(int) ti;

			

				Integer basinId = t;

				Qout =inQoutvalues.get(basinId)[0];
				if (isNovalue(Qout)) {
					Qout= 0;
				} else {
					Qout = inQoutvalues.get(basinId)[0];
				}

				ETout =inEToutvalues.get(basinId)[0];
				if (isNovalue(ETout)) {
					ETout= 0;
				} else {
					ETout = inEToutvalues.get(basinId)[0];
				}

				Q_t_i= computeQ();
				ET_t_i= computeET();
				System.out.println(Q_t_i);

			}

		
		storeResult(dim,Q_t_i,ET_t_i);
		//t=t+1;
		
		}


	public double computeQ() throws IOException {
		dt=a/1000;
		Q_t_i= Qout*dt+inte;
		inte=Q_t_i;		
		return Q_t_i;
	}


	public double computeET() throws IOException {
		dt=a/1000;
		ET_t_i= ETout*dt+inte2;
		inte2=ET_t_i;
		return ET_t_i;
	}


	private void storeResult(int dim,double Qout,double ETout) throws SchemaException {
		outHMQ = new HashMap<Integer, double[]>();
		outHMET = new HashMap<Integer, double[]>();

		outHMQ.put(ID, new double[]{Qout});
		outHMET.put(ID, new double[]{ETout});


	}	

}
