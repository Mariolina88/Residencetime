package marialaura.residencetime.test;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class TestOUT extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-01-01 06:00";
		int timeStepMinutes = 60;
		String fId = "ID";


		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);
		
		String inPathToPrec = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/rainfall.csv";
		String inPathToDischarge = "/Users/marialaura/Desktop/Q_mode3.csv";
		String inPathToET ="/Users/marialaura/Desktop/dottorato/Idrologia/dati/ET.csv";
		String inPathToS ="/Users/marialaura/Desktop/S_mode3.csv";
		String inPathToP ="/Users/marialaura/Desktop/p_mode1.csv";
		String inpathTopT= "/Users/marialaura/Desktop/pT_mode1.csv";
		String pathToOut= "/Users/marialaura/Desktop/pET_mode1.csv";

		OmsTimeSeriesIteratorReader precipitationReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader dischargeReader = getTimeseriesReader(inPathToDischarge, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader SReader = getTimeseriesReader(inPathToS, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader PTReader = getTimeseriesReader(inpathTopT, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorWriter writer_Q = new OmsTimeSeriesIteratorWriter();



		writer_Q.file = pathToOut;
		writer_Q.tStart = startDate;
		writer_Q.tTimestep = timeStepMinutes;
		writer_Q.fileNovalue="-9999";





		OutOFTravelTimes pdfs= new OutOFTravelTimes();


		while( precipitationReader.doProcess ) {

			precipitationReader.nextRecord();
			pdfs.ID=1;
			pdfs.pathToPrec=inPathToPrec;
			pdfs.pathToDischarge=inPathToDischarge;
			pdfs.pathToET=inPathToET;
			pdfs.pathToS=inPathToS;
			pdfs.pathToPT=inpathTopT;
			pdfs.pathToPet=inpathTopT;
			pdfs.tStartDate=startDate;
			pdfs.tEndDate=endDate;
			pdfs.pathToTheta="/Users/marialaura/Desktop/theta_mode1.csv";
			pdfs.tStartDateT="1995-12-31 23:00";
			pdfs.tEndDateT="1996-01-01 00:00";
			pdfs.inTimestepT=60;
			pdfs.a=0.681224884;



			HashMap<Integer, double[]> id2ValueMap = precipitationReader.outData;
			pdfs.inPrecipvalues = id2ValueMap;

			dischargeReader.nextRecord();
			id2ValueMap = dischargeReader.outData;
			pdfs.inDischargevalues = id2ValueMap;

			ETReader.nextRecord();
			id2ValueMap = ETReader.outData;
			pdfs.inETvalues = id2ValueMap;

			SReader.nextRecord();
			id2ValueMap = SReader.outData;
			pdfs.inWaterStoragevalues = id2ValueMap;

			PTReader.nextRecord();
			id2ValueMap = PTReader.outData;
			pdfs.inPvalues= id2ValueMap;



			pdfs.pm = pm;
			pdfs.process();

			HashMap<Integer, double[]> outHM = pdfs.outHMout;

			writer_Q.inData = outHM;
			writer_Q.writeNextLine();

			if (pathToOut != null) {
				writer_Q.close();
			}

			HashMap<Integer, double[]> outHMet = pdfs.outHMout;

			}


			//double value = outHM.get(8)[0];
			//assertTrue(NumericsUtilities.dEq(value, 3.7612114870933824));
			//break;
		}


	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = "1994-01-01 00:00";
		reader.tTimestep = 60;
		reader.tEnd = "1994-01-01 03:00";
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}
