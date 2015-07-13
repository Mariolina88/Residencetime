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

public class MassConservation extends JGTModel{


	@Description("Discharge")
	@In	
	public HashMap<Integer, double[]> inQoutvalues;
	double Qout;
	public String pathToQout;


	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inEToutvalues;
	double ETout;
	public String pathToETout;
	

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
	//injection time
	int t_i=0;
	DateTime StartDate_ti;
	//time
	int t=0;
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

		startDate = formatter.parseDateTime(tStartDate);

		for (t=0;t<dim;t++){
			StartDate_ti=startDate.plusHours(t_i);	
			StartDate_t=StartDate_ti.plusHours(t);
			String startDate_ti=StartDate_ti.toString(formatter);
			InputOut dataInput=new InputOut(pathToQout,pathToETout,
					tStartDate,startDate_ti,tEndDate,inTimestep,ID,dim,t,t_i);
			Qout=dataInput.Qout;
			ETout=dataInput.ETout;
			
			Q_t_i= computeQ();
			ET_t_i= computeET();
		}
		t=0;
		t_i=t_i+1;
		inte=0;
		storeResult(dim,Q_t_i,ET_t_i);
		

	}	


	public double computeQ() throws IOException {
		Q_t_i= Qout+inte;
		inte=Q_t_i;
		System.out.println(Q_t_i);
		return Q_t_i;
	}


	public double computeET() throws IOException {
		ET_t_i= ETout+inte2;
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
