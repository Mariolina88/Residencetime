package marialaura.residencetime.test;


import java.net.URISyntaxException;
import java.util.HashMap;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class TestWaterBudget2 extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-01-11 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);

		String inPathToPrec = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/rainfall.csv";
		String inPathToDischarge = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/Q.csv";
		String inPathToET ="/Users/marialaura/Desktop/dottorato/Idrologia/dati/ET.csv";
		String pathToS= "/Users/marialaura/Desktop/S_prova.csv";
		String pathToQ= "/Users/marialaura/Desktop/Q_prova.csv";

		OmsTimeSeriesIteratorReader precipitationReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader dischargeReader = getTimeseriesReader(inPathToDischarge, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorWriter writerS = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerQ = new OmsTimeSeriesIteratorWriter();

		writerS.file = pathToS;
		writerS.tStart = startDate;
		writerS.tTimestep = timeStepMinutes;
		writerS.fileNovalue="-9999";
		
		writerQ.file = pathToQ;
		writerQ.tStart = startDate;
		writerQ.tTimestep = timeStepMinutes;
		writerQ.fileNovalue="-9999";
		
		WaterBudget2 waterBudget= new WaterBudget2();


		while( precipitationReader.doProcess ) {
		
			waterBudget.mode=3;
			waterBudget.A=115.4708483;
			waterBudget.a=0.68;
			waterBudget.b=1.5;
			waterBudget.nZ=0.8;
			waterBudget.ID=1;
			waterBudget.pathToPrec=inPathToPrec;
			waterBudget.pathToDischarge=inPathToDischarge;
			waterBudget.pathToET=inPathToET;
			waterBudget.tStartDate=startDate;
			waterBudget.tEndDate=endDate;
			//waterBudget.dim=11;

			
			
            waterBudget.process();
            
            HashMap<Integer, double[]> outHM = waterBudget.outHMSout;
            HashMap<Integer, double[]> outHMQ = waterBudget.outHMQout;
            
			writerS.inData = outHM;
			writerS.writeNextLine();
			
			if (pathToS != null) {
				writerS.close();
			}
			
			writerQ.inData = outHMQ;
			writerQ.writeNextLine();
			
			if (pathToS != null) {
				writerQ.close();
			}
            
            //double value = outHM.get(8)[0];
            //assertTrue(NumericsUtilities.dEq(value, 3.7612114870933824));
            //break;
		}
		
		precipitationReader.close();
        dischargeReader.close();
        ETReader.close();

	}


	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = "1994-01-01 00:00";
		reader.tTimestep = 60;
		reader.tEnd = "1994-01-01 00:00";
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}