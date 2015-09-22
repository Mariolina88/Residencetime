package marialaura.residencetime.test;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class TestPdfs4 extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-01-01 10:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);


		String inPathToTime = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/time.csv";
		String inPathToDischarge = "/Users/marialaura/Desktop/output/Q_prova.csv";
		String inPathToET ="/Users/marialaura/Desktop/dottorato/Idrologia/Posina/output/ET_calibrato.csv";
		String inPathToS ="/Users/marialaura/Desktop/output/S_prova.csv";
		String pathToOut= "/Users/marialaura/Desktop/p_mode1.csv";
		String pathToTheta= "/Users/marialaura/Desktop/theta_mode1.csv";

		OmsTimeSeriesIteratorReader timeReader = getTimeseriesReader(inPathToTime, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader dischargeReader = getTimeseriesReader(inPathToDischarge, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader SReader = getTimeseriesReader(inPathToS, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorWriter writer_p = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writer_theta = new OmsTimeSeriesIteratorWriter();
		
		writer_p.file = pathToOut;
		writer_p.tStart = startDate;
		writer_p.tTimestep = timeStepMinutes;
		writer_p.fileNovalue="-9999";
		
		writer_theta.file = pathToTheta;
		writer_theta.tStart = startDate;
		writer_theta.tTimestep = timeStepMinutes;
		writer_theta.fileNovalue="-9999";

		
		Probabilities4 pdfs= new Probabilities4();


		while( timeReader.doProcess ) {
		
			timeReader.nextRecord();
			pdfs.ID=209;
			pdfs.mode=1;
			pdfs.tStartDate=startDate;
			pdfs.tEndDate=endDate;
	


			HashMap<Integer, double[]> id2ValueMap = timeReader.outData;
			pdfs.inTimevalues = id2ValueMap;
		
			dischargeReader.nextRecord();
            id2ValueMap = dischargeReader.outData;
            pdfs.inDischargevalues = id2ValueMap;
            
            ETReader.nextRecord();
            id2ValueMap = ETReader.outData;
            pdfs.inETvalues = id2ValueMap;
            
            SReader.nextRecord();
            id2ValueMap = SReader.outData;
            pdfs.inWaterStoragevalues = id2ValueMap;

            pdfs.pm = pm;
            pdfs.process();
            
            HashMap<Integer, double[]> outHM = pdfs.outHMQout;
            HashMap<Integer, double[]> outHMT = pdfs.outHMTout;
            
			writer_p.inData = outHM;
			writer_p.writeNextLine();
			
			if (pathToOut != null) {
				writer_p.close();
			}
            
			writer_theta.inData = outHMT;
			writer_theta.writeNextLine();
			
			if (pathToTheta != null) {
				writer_theta.close();
			}
			
            //double value = outHM.get(8)[0];
            //assertTrue(NumericsUtilities.dEq(value, 3.7612114870933824));
            //break;
		}
		
		timeReader.close();
        dischargeReader.close();
        ETReader.close();
        SReader.close();
	}


	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = "1994-01-01 00:00";
		reader.tTimestep = 60;
		reader.tEnd = "1994-01-01 10:00";
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}
