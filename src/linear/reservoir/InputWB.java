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


public class InputWB {


	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;
	double precipitation;

	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;
	double ET;

	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;

	double discharge;
	double S_i;


	String pathToPrec;
	String pathToDischarge;
	String pathToET;

	String tStartDate;
	String tEndDate;
	int inTimestep;
	int ID;
	int t_i;
	public DateTime StartDate_ti;
	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;
	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	public InputWB (String pathToPrec,String pathToDischarge,String pathToET,
			String tStartDate,String tEndDate,int inTimestep,int ID) throws IOException{


				OmsTimeSeriesIteratorReader reader_precip = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_discharge = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_et = new OmsTimeSeriesIteratorReader();

				if (!((pathToPrec == null))) {
					reader_precip.file = pathToPrec ;
					reader_precip.idfield = "ID";
					reader_precip.tStart = tStartDate;
					reader_precip.tEnd = tEndDate;
					reader_precip.fileNovalue = "-9999";
					reader_precip.tTimestep = inTimestep;
					reader_precip.initProcess();
				}


				if (!((pathToDischarge == null))) {
					reader_discharge.file = pathToDischarge ;
					reader_discharge.idfield = "ID";
					reader_discharge.tStart = tStartDate;
					reader_discharge.tEnd = tEndDate;
					reader_discharge.fileNovalue = "-9999";
					reader_discharge.tTimestep = inTimestep;
					reader_discharge.initProcess();
				}

				if (!((pathToET == null))) {
					reader_et.file = pathToET ;
					reader_et.idfield = "ID";
					reader_et.tStart = tStartDate;
					reader_et.tEnd = tEndDate;
					reader_et.fileNovalue = "-9999";
					reader_et.tTimestep = inTimestep;
					reader_et.initProcess();
				}

				
				if (!(pathToPrec == null)) {
					reader_precip.nextRecord();
					inPrecipvalues = reader_precip.outData;
				}

				if (inPrecipvalues!= null) {
					precipitation = inPrecipvalues.get(ID)[0];
				}

				if (!(pathToDischarge == null)) {
					reader_discharge.nextRecord();
					inDischargevalues = reader_discharge.outData;
				}

				if (inDischargevalues!= null) {
					discharge = inDischargevalues.get(ID)[0];
				}

				if (!(pathToET == null)) {
					reader_et.nextRecord();
					inETvalues = reader_et.outData;
				}

				if (inETvalues!= null) {
					ET = inETvalues.get(ID)[0];
				}

			
			S_i=0;
		}
	}




