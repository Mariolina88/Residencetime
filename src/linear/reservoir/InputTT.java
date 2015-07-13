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


public class InputTT {


	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;
	double precipitation;
	String pathToPrec;
	
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
	
	@Description("InputP")
	@In
	public HashMap<Integer, double[]> inPvalues;
	double P;
	String pathToP;
	
	@Description("InputTheta")
	@In
	public HashMap<Integer, double[]> inThetavalues;
	double theta;
	String pathToTheta;


	String tStartDate;
	String tEndDate;
	int inTimestep;
	int ID;

	public InputTT (String pathToPrec,String pathToS,String pathToDischarge,String pathToET, String pathToP,String pathToTheta,
			String tStartDate,String tStartDate_ti,String tEndDate,int inTimestep,int ID,int dim,int t,int t_i) throws IOException{

				OmsTimeSeriesIteratorReader reader_precip = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_storage = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_discharge = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_et = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_theta = new OmsTimeSeriesIteratorReader();
				OmsTimeSeriesIteratorReader reader_P = new OmsTimeSeriesIteratorReader();
				
				if (!((pathToPrec == null))) {
					reader_precip.file = pathToPrec ;
					reader_precip.idfield = "ID";
					reader_precip.tStart = tStartDate_ti;
					reader_precip.tEnd = tStartDate_ti;
					reader_precip.fileNovalue = "-9999";
					reader_precip.tTimestep = inTimestep;
					reader_precip.initProcess();
				}

				if (!((pathToS == null))) {

					reader_storage.file = pathToS ;
					reader_storage.idfield = "ID";
					reader_storage.tStart = tStartDate_ti;
					reader_storage.tEnd = tEndDate;
					reader_storage.fileNovalue = "-9999";
					reader_storage.tTimestep = inTimestep;
					reader_storage.initProcess();
					}



				if (!((pathToDischarge == null))) {
					reader_discharge.file = pathToDischarge ;
					reader_discharge.idfield = "ID";
					reader_discharge.tStart = tStartDate_ti;
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
				
				if (!((pathToP == null))) {
					reader_P.file = pathToP ;
					reader_P.idfield = "ID";
					reader_P.tStart = tStartDate_ti;
					reader_P.tEnd = tEndDate;
					reader_P.fileNovalue = "-9999";
					reader_P.tTimestep = inTimestep;
					reader_P.initProcess();
				}
				
				if (!((pathToTheta == null))) {
					reader_theta.file = pathToTheta ;
					reader_theta.idfield = "ID";
					reader_theta.tStart = tStartDate_ti;
					reader_theta.tEnd = tEndDate;
					reader_theta.fileNovalue = "-9999";
					reader_theta.tTimestep = inTimestep;
					reader_theta.initProcess();
				}

				if (!(pathToPrec == null)) {
					reader_precip.nextRecord();
					inPrecipvalues = reader_precip.outData;
				}

				if (inPrecipvalues!= null) {
					precipitation = inPrecipvalues.get(ID)[0];
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
				
				if (!(pathToP == null)) {
					reader_P.nextRecord();
					inPvalues = reader_P.outData;
				}

				if (inPvalues!= null) {
					P = inPvalues.get(t)[0];	
				}
				
				if (!(pathToTheta == null)) {
					reader_theta.nextRecord();
					inThetavalues = reader_theta.outData;
				}
 // nella versione ottimizzata era dim-ti-1
				if (inThetavalues!= null) {
					theta = inThetavalues.get(dim-3)[0];	
				}


		}
	}




