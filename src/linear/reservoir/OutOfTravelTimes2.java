package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;
import linear.reservoir.*;

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

public class OutOfTravelTimes2 extends JGTModel{

	
	double Qout;
	@Description("Discharge")
	@In	
	public HashMap<Integer, double[]> inQoutvalues;

	double ETout;
	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inEToutvalues;

	@Description("Input Time values")
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

	@Description("The matrix of Q")
	@Out
	public HashMap<Integer, double[]> outHMQ;


	@Description("The matrix of ET")
	@Out
	public HashMap<Integer, double[]> outHMET;


	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	double dt;
	//dimension of the output matrix 
	int dim;
	int t_i;
	Double[] vectorQ;
	Double[] vectorET;

	@Execute
	public void process() throws Exception {
		//integration time
		dt=1E-4;
		
		DateTime start = formatter.parseDateTime(tStartDate);
		DateTime end = formatter.parseDateTime(tEndDate);
		dim=Hours.hoursBetween(start, end).getHours()+1;
		ArrayList<Double> valueQ = new ArrayList<Double>();
		ArrayList<Double> valueET = new ArrayList<Double>();
		

		Set<Entry<Integer, double[]>> entrySet = inTimevalues.entrySet();
		for( Entry<Integer, double[]> entry : entrySet ) {

			double ti = entry.getValue()[0];
			t_i=(int) ti;
			
			for (int t=0;t<dim;t++){

				Integer basinId = t;

				Qout =inQoutvalues.get(basinId)[0];
				if (isNovalue(Qout)) {
					Qout= 0;
				} else {
					Qout = inQoutvalues.get(basinId)[0]*dt;
				}

				ETout =inEToutvalues.get(basinId)[0];
				if (isNovalue(ETout)) {
					ETout= 0;
				} else {
					ETout = inEToutvalues.get(basinId)[0]*dt;
				}

				valueQ.add(Qout);
				valueET.add(ETout);

				//System.out.println(Q_t_i);

			}
			
			vectorQ = (entry.getValue()[0] == 0) ? toArray(valueQ, dim) : computeSum(valueQ, vectorQ);
			vectorET = (entry.getValue()[0] == 0) ? toArray(valueET, dim) : computeSum(valueET, vectorET);

		}

		storeResult(t_i,vectorQ,vectorET);



	}

	public Double[] toArray(ArrayList<Double> value, int dim) {
		Double[] vector = new Double[dim];
		value.toArray(vector);
		
		return vector;
	}

	public Double[] computeSum(ArrayList<Double> value, Double[] vector) {

		Double[] tmpVector = new Double[dim];
		value.toArray(tmpVector);

		for (int i=0; i<vector.length; i++) vector[i] += tmpVector[i];
		
		return vector;

	}


	private void storeResult(int t_i, Double[] vectorQ,Double[] vectorET) throws SchemaException {
		outHMQ = new HashMap<Integer, double[]>();
		outHMET = new HashMap<Integer, double[]>();

		outHMQ.put(ID, new double[]{vectorQ[t_i]});
		outHMET.put(ID, new double[]{vectorET[t_i]});
		

	}	

}
