package linear.reservoir;
import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;


public class InputOut {


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

	String tStartDate;
	String tEndDate;
	int inTimestep;
	int ID;


	public InputOut (String pathToQout,String pathToETout, String tStartDate,String startDate_ti,
			String tEndDate,int inTimestep,int ID,int dim,int t,int t_i) throws IOException{


				OmsTimeSeriesIteratorReader reader_Qout = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_pETout = new OmsTimeSeriesIteratorReader();


				if (!((pathToQout == null))) {
					reader_Qout.file = pathToQout ;
					reader_Qout.idfield = "ID";
					reader_Qout.tStart = startDate_ti;
					reader_Qout.tEnd = tEndDate;
					reader_Qout.fileNovalue = "-9999";
					reader_Qout.tTimestep = inTimestep;
					reader_Qout.initProcess();
				}


				if (!((pathToETout == null))) {
					reader_pETout.file = pathToETout ;
					reader_pETout.idfield = "ID";
					reader_pETout.tStart = startDate_ti;
					reader_pETout.tEnd = tEndDate;
					reader_pETout.fileNovalue = "-9999";
					reader_pETout.tTimestep = inTimestep;
					reader_pETout.initProcess();
				}

				

				
				if (!(pathToQout == null)) {
					reader_Qout.nextRecord();
					inQoutvalues = reader_Qout.outData;
				}

				if (inQoutvalues!= null) {
					Qout = inQoutvalues.get(t)[0];
				}

				if (!(pathToETout == null)) {
					reader_pETout.nextRecord();
					inEToutvalues = reader_pETout.outData;
				}

				if (inEToutvalues!= null) {
					ETout = inEToutvalues.get(t)[0];
				}

				
		}
	}




