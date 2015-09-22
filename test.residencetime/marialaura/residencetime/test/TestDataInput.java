package marialaura.residencetime.test;


import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class TestDataInput extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-01-02 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";


		String inPathToTime = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/time.csv";
		String inPathToPrec = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/rainfall.csv";
		String inPathToDischarge = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/Q.csv";
		String inPathToET ="/Users/marialaura/Desktop/dottorato/Idrologia/dati/ET.csv";
		String pathToJ= "/Users/marialaura/Desktop/J_matrix.csv";
		String pathToQ= "/Users/marialaura/Desktop/Q_matrix.csv";
		String pathToET= "/Users/marialaura/Desktop/ET_matrix.csv";

		OmsTimeSeriesIteratorReader timeReader = getTimeseriesReader(inPathToTime, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader JReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader QReader = getTimeseriesReader(inPathToDischarge, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);

		OmsTimeSeriesIteratorWriter writerJ = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerQ = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerET = new OmsTimeSeriesIteratorWriter();

		writerJ.file = pathToJ;
		writerJ.tStart = startDate;
		writerJ.tTimestep = timeStepMinutes;
		writerJ.fileNovalue="-9999";
		
		writerQ.file = pathToQ;
		writerQ.tStart = startDate;
		writerQ.tTimestep = timeStepMinutes;
		writerQ.fileNovalue="-9999";
		
		writerET.file = pathToET;
		writerET.tStart = startDate;
		writerET.tTimestep = timeStepMinutes;
		writerET.fileNovalue="-9999";
		
		DataInput dataInput= new DataInput();

		while( timeReader.doProcess ) {
			dataInput.ID=1;
			dataInput.tStartDate=startDate;
			dataInput.tEndDate=endDate;


			timeReader.nextRecord();
			
			HashMap<Integer, double[]> id2ValueMap = timeReader.outData;
			dataInput.inTimevalues = id2ValueMap;
			
			JReader.nextRecord();
            id2ValueMap = JReader.outData;
            dataInput.inPrecipvalues= id2ValueMap;
            
            QReader.nextRecord();
            id2ValueMap = QReader.outData;
            dataInput.inDischargeInputvalues = id2ValueMap;
            
            ETReader.nextRecord();
            id2ValueMap = ETReader.outData;
            dataInput.inETvalues = id2ValueMap;
		
            dataInput.process();
            
            HashMap<Integer, double[]> outHM = dataInput.outHMJout;
            HashMap<Integer, double[]> outHMQ = dataInput.outHMQout;
            HashMap<Integer, double[]> outHMET = dataInput.outHMETout;
            
			writerJ.inData = outHM;
			writerJ.writeNextLine();
			
			if (pathToJ != null) {
				writerJ.close();
			}
			
			writerQ.inData = outHMQ;
			writerQ.writeNextLine();
			
			if (pathToJ != null) {
				writerQ.close();
			}
			
			writerET.inData = outHMET;
			writerET.writeNextLine();
			
			if (pathToET != null) {
				writerET.close();
			}
            
            //double value = outHM.get(8)[0];
            //assertTrue(NumericsUtilities.dEq(value, 3.7612114870933824));
            //break;
		}
		
		timeReader.close();
		QReader.close();
		JReader.close();
		ETReader.close();


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