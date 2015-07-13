package linear.reservoir;
import static org.jgrasstools.gears.libs.modules.JGTConstants.doubleNovalue;
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


public class InputP {


	@Description("Input storageitation")
	@In
	public HashMap<Integer, double[]> inStoragevalues;
	double storage;

	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;
	double ET;
	String pathToET;

	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;
	double discharge;
	String pathToDischarge;


	String tStartDate;
	String tEndDate;
	int inTimestep;
	int ID;
	DateTime StartDate_t;
	DateTime StartDate_ti;
	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	public InputP (String pathToS,String pathToDischarge,String pathToET,String startDate_t,
			String startDate_ti,String tEndDate,int inTimestep,int ID,int dim,int t,int t_i) throws IOException{


				OmsTimeSeriesIteratorReader reader_storage = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_discharge = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_et = new OmsTimeSeriesIteratorReader();
				

				if (!((pathToS == null))) {

					reader_storage.file = pathToS ;
					reader_storage.idfield = "ID";
					reader_storage.tStart = startDate_ti;
					reader_storage.tEnd = tEndDate;
					reader_storage.fileNovalue = "-9999";
					reader_storage.tTimestep = inTimestep;
					reader_storage.initProcess();
					}



				if (!((pathToDischarge == null))) {
					reader_discharge.file = pathToDischarge ;
					reader_discharge.idfield = "ID";
					reader_discharge.tStart = startDate_ti;
					reader_discharge.tEnd = tEndDate;
					reader_discharge.fileNovalue = "-9999";
					reader_discharge.tTimestep = inTimestep;
					reader_discharge.initProcess();
				}

				if (!((pathToET == null))) {
					reader_et.file = pathToET ;
					reader_et.idfield = "ID";
					reader_et.tStart = startDate_t;
					reader_et.tEnd = tEndDate;
					reader_et.fileNovalue = "-9999";
					reader_et.tTimestep = inTimestep;
					reader_et.initProcess();
				}

				
				if (!(pathToS == null)) {
					reader_storage.nextRecord();
					inStoragevalues = reader_storage.outData;
				}
				
				
				if (inStoragevalues!= null) {
					storage = inStoragevalues.get(t)[0];	
				}




				if (!(pathToDischarge == null)) {
					
					reader_discharge.nextRecord();
					inDischargevalues = reader_discharge.outData;
				}
				

				if (inDischargevalues!= null) {
					discharge = inDischargevalues.get(t)[0];
				} 

				if (!(pathToET == null)) {
					reader_et.nextRecord();
					inETvalues = reader_et.outData;
				}

				if (inETvalues!= null) {
					ET = inETvalues.get(ID)[0];
				}

				}
		
	}
	




