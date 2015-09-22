package marialaura.residencetime.test;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class TestTravelTimes4 extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-01-02 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);

		String inPathToTime = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/time.csv";
		String inPathToPrec = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/rainfall.csv";
		String inPathToDischarge = "/Users/marialaura/Desktop/output/Q_prova.csv";
		String inPathToET ="/Users/marialaura/Desktop/dottorato/Idrologia/dati/ET.csv";
		String inPathToS ="/Users/marialaura/Desktop/output/S_prova.csv";
		String inPathToP= "/Users/marialaura/Desktop/output/p_prova.csv";
		String inPathToTheta= "/Users/marialaura/Desktop/output/theta_prova.csv";
		String pathToOut= "/Users/marialaura/Desktop/pT_prova.csv";
		String pathToOutET= "/Users/marialaura/Desktop/pET_prova.csv";
		String pathToQout= "/Users/marialaura/Desktop/QOUT_prova.csv";
		String pathToETout= "/Users/marialaura/Desktop/ETOUT_prova.csv";

		OmsTimeSeriesIteratorReader timeReader = getTimeseriesReader(inPathToTime, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader precipitationReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader dischargeReader = getTimeseriesReader(inPathToDischarge, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader SReader = getTimeseriesReader(inPathToS, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader pReader = getTimeseriesReader(inPathToP, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader thetaReader = getTimeseriesReader(inPathToTheta, fId, startDate, endDate, timeStepMinutes);

		OmsTimeSeriesIteratorWriter writer_pT = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writer_pET = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writer_Qout = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writer_ETout = new OmsTimeSeriesIteratorWriter();
		
		writer_pT.file = pathToOut;
		writer_pT.tStart = startDate;
		writer_pT.tTimestep = timeStepMinutes;
		writer_pT.fileNovalue="-9999";

		writer_pET.file = pathToOutET;
		writer_pET.tStart = startDate;
		writer_pET.tTimestep = timeStepMinutes;
		writer_pET.fileNovalue="-9999";
		
		writer_Qout.file = pathToQout;
		writer_Qout.tStart = startDate;
		writer_Qout.tTimestep = timeStepMinutes;
		writer_Qout.fileNovalue="-9999";
		
		writer_ETout.file = pathToETout;
		writer_ETout.tStart = startDate;
		writer_ETout.tTimestep = timeStepMinutes;
		writer_ETout.fileNovalue="-9999";

		
		TravelTimes4 pdfs= new TravelTimes4();


		while( timeReader.doProcess ) {
		
			timeReader.nextRecord();
			pdfs.ID=1;
			pdfs.mode=2;
			pdfs.theta_i=0.5896090;


			pdfs.tStartDate=startDate;
			pdfs.tEndDate=endDate;
	


			HashMap<Integer, double[]> id2ValueMap = timeReader.outData;
			pdfs.inTimevalues = id2ValueMap;
			
			precipitationReader.nextRecord();
            id2ValueMap = precipitationReader.outData;
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
            
            pReader.nextRecord();
            id2ValueMap = pReader.outData;
            pdfs.inPvalues= id2ValueMap;
            
            thetaReader.nextRecord();
            id2ValueMap = thetaReader.outData;
            pdfs.inthetavalues = id2ValueMap;
            
		

            pdfs.pm = pm;
            pdfs.process();
            
            HashMap<Integer, double[]> outHM = pdfs.outHMpTout;
            HashMap<Integer, double[]> outHMT = pdfs.outHMpETout;
            
			writer_pT.inData = outHM;
			writer_pT.writeNextLine();
			
			if (pathToOut != null) {
				writer_pT.close();
			}
            
			writer_pET.inData = outHMT;
			writer_pET.writeNextLine();
			
			if (pathToOutET != null) {
				writer_pET.close();
			}
			
			HashMap<Integer, double[]> outHMout = pdfs.outHMQout;
            HashMap<Integer, double[]> outHMTout = pdfs.outHMETout;
            
			writer_Qout.inData = outHMout;
			writer_Qout.writeNextLine();
			
			if (pathToQout != null) {
				writer_Qout.close();
			}
            
			writer_ETout.inData = outHMTout;
			writer_ETout.writeNextLine();
			
			if (pathToETout != null) {
				writer_ETout.close();
			}
			
            //double value = outHM.get(8)[0];
            //assertTrue(NumericsUtilities.dEq(value, 3.7612114870933824));
            //break;
		}
		
		timeReader.close();
		precipitationReader.close();
        dischargeReader.close();
        ETReader.close();
        SReader.close();
        pReader.close();
        thetaReader.close();

	}


	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = "1994-01-01 00:00";
		reader.tTimestep = 60;
		reader.tEnd = "1994-01-02 00:00";
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}
