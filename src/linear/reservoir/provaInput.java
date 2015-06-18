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


public class provaInput {


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

	@Description("InputWaterStorage")
	@In
	public HashMap<Integer, double[]> inWaterStoragevalues;
	double S;


	@Description("Input pT")
	@In
	public HashMap<Integer, double[]> inPTvalues;
	double pT;

	@Description("Input p")
	@In
	public HashMap<Integer, double[]> inPETvalues;
	double pET;

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
	public String tStartDate;
	public String tEndDate;
	public int inTimestep;
	public int ID;
	public int t_i=-1;

	private DateTimeFormatter formatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

	public provaInput ( String pathToPrec,String pathToDischarge,String pathToET,String pathToS, 
			String pathToTheta,String pathToPT,String pathToPet,String tStartDateT,
			String tEndDateT,String tStartDate,String tEndDate,int inTimestep,int ID) throws IOException{

		//DateTime startDate = formatter.parseDateTime(tStartDate);
		//DateTime EndDate = formatter.parseDateTime(tEndDate);
		
		
		//for (DateTime date = startDate; date.isBefore(EndDate); date = date.plusHours(1))	{
			//tStartDate=date.toString(formatter);

			OmsTimeSeriesIteratorReader reader_precip = new OmsTimeSeriesIteratorReader();
			OmsTimeSeriesIteratorReader reader_discharge = new OmsTimeSeriesIteratorReader();
			OmsTimeSeriesIteratorReader reader_et = new OmsTimeSeriesIteratorReader();
			OmsTimeSeriesIteratorReader reader_S = new OmsTimeSeriesIteratorReader();
			OmsTimeSeriesIteratorReader reader_pT = new OmsTimeSeriesIteratorReader();
			OmsTimeSeriesIteratorReader reader_pET = new OmsTimeSeriesIteratorReader();


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

			if (!((pathToS == null))) {
				reader_S.file = pathToS ;
				reader_S.idfield = "ID";
				reader_S.tStart = tStartDate;
				reader_S.tEnd = tEndDate;
				reader_S.fileNovalue = "-9999";
				reader_S.tTimestep = inTimestep;
				reader_S.initProcess();
			}

			if (!((pathToPT == null))) {
				reader_pT.file = pathToPT ;
				reader_pT.idfield = "ID";
				reader_pT.tStart = tStartDate;
				reader_pT.tEnd = tEndDate;
				reader_pT.fileNovalue = "-9999";
				reader_pT.tTimestep = inTimestep;
				reader_pT.initProcess();
			}

			if (!((pathToPet == null))) {
				reader_pET.file = pathToPet ;
				reader_pET.idfield = "ID";
				reader_pET.tStart = tStartDate;
				reader_pET.tEnd = tEndDate;
				reader_pET.fileNovalue = "-9999";
				reader_pET.tTimestep = inTimestep;
				reader_pET.initProcess();
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

			if (!(pathToS == null)) {
				reader_S.nextRecord();
				inWaterStoragevalues = reader_S.outData;
			}

			if (inWaterStoragevalues!= null) {
				S = inWaterStoragevalues.get(ID)[0];
			}

			if (!(pathToPT == null)) {
				reader_pT.nextRecord();
				inPTvalues = reader_pT.outData;
			}

			if (inPTvalues!= null) {
				pT = inPTvalues.get(ID)[0];
			}

			if (!(pathToPet == null)) {
				reader_pET.nextRecord();
				inPETvalues = reader_pET.outData;
			}

			if (inPETvalues!= null) {
				pET = inPETvalues.get(ID)[0];
			}

		}
	//}		


}



