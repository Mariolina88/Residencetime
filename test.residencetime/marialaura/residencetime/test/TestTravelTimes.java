package marialaura.residencetime.test;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class TestTravelTimes extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1999-01-01 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";


		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);

		String inPathToPrec = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/rainfall.csv";
		String inPathToDischarge = "/Users/marialaura/Desktop/Q_mode3.csv";
		String inPathToET ="/Users/marialaura/Desktop/dottorato/Idrologia/dati/ET.csv";
		String inPathToS ="/Users/marialaura/Desktop/S_mode3.csv";
		String inPathToP ="/Users/marialaura/Desktop/p_mode1.csv";
		String pathToOut= "/Users/marialaura/Desktop/pT_mode1.csv";
		String pathToOutET= "/Users/marialaura/Desktop/pET_mode1.csv";

		OmsTimeSeriesIteratorReader precipitationReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader dischargeReader = getTimeseriesReader(inPathToDischarge, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader SReader = getTimeseriesReader(inPathToS, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader PReader = getTimeseriesReader(inPathToP, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorWriter writer_pT = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writer_pET = new OmsTimeSeriesIteratorWriter();


		writer_pT.file = pathToOut;
		writer_pT.tStart = startDate;
		writer_pT.tTimestep = timeStepMinutes;
		writer_pT.fileNovalue="-9999";

		writer_pET.file = pathToOutET;
		writer_pET.tStart = startDate;
		writer_pET.tTimestep = timeStepMinutes;
		writer_pET.fileNovalue="-9999";



		TravelTimes pdfs= new TravelTimes();


		while( precipitationReader.doProcess ) {

			precipitationReader.nextRecord();
			pdfs.ID=1;
			pdfs.pathToTheta="/Users/marialaura/Desktop/theta_mode1.csv";
			pdfs.tStartDateT="1996-01-01 00:00";
			pdfs.tEndDateT="1996-01-01 00:00";
			pdfs.inTimestepT=60;



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

			PReader.nextRecord();
			id2ValueMap = PReader.outData;
			pdfs.inPvalues= id2ValueMap;



			pdfs.pm = pm;
			pdfs.process();

			HashMap<Integer, double[]> outHM = pdfs.outHMpTout;

			writer_pT.inData = outHM;
			writer_pT.writeNextLine();

			if (pathToOut != null) {
				writer_pT.close();
			}

			HashMap<Integer, double[]> outHMet = pdfs.outHMpETout;

			writer_pET.inData = outHMet;
			writer_pET.writeNextLine();

			if (pathToOutET != null) {
				writer_pET.close();
			}


			//double value = outHM.get(8)[0];
			//assertTrue(NumericsUtilities.dEq(value, 3.7612114870933824));
			//break;
		}

		precipitationReader.close();
		dischargeReader.close();
		ETReader.close();
		SReader.close();
		PReader.close();
	}


	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = "1994-01-01 00:00";
		reader.tTimestep = 60;
		reader.tEnd = "1999-01-01 00:00";
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}
