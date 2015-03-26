package marialaura.residencetime.test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.geotools.data.DataUtilities;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import org.jgrasstools.gears.utils.math.NumericsUtilities;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class TestOmsLinearReservoir extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "2010-01-01 00:00";
		String endDate = "2010-01-01 13:00";
		int length=12;
		int timeStepMinutes = 60;
		String fId = "ID";

		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);

		String inPathToPrec = "/Users/marialaura/Desktop/Precipitazione.csv";
		String inPathToDischarge = "/Users/marialaura/Desktop/Q.csv";
		String inPathToET ="/Users/marialaura/Desktop/ET.csv";
		String pathToQout= "/Users/marialaura/Desktop/ProvaOUT.csv";

		OmsTimeSeriesIteratorReader precipitationReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader dischargeReader = getTimeseriesReader(inPathToDischarge, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);

		OmsLinearReservoir LR= new OmsLinearReservoir();


		while( precipitationReader.doProcess ) {
		
			
			precipitationReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = precipitationReader.outData;
			LR.inPrecipvalues = id2ValueMap;
			
			dischargeReader.nextRecord();
            id2ValueMap = dischargeReader.outData;
            LR.inDischargevalues = id2ValueMap;
            
            ETReader.nextRecord();
            id2ValueMap = ETReader.outData;
            LR.inETvalues = id2ValueMap;

            LR.pm = pm;
            LR.process();
            
            HashMap<Integer, double[]> outHM = LR.outHMQout;
            
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
		reader.tStart = "2010-01-01 00:00";
		reader.tTimestep = 60;
		reader.tEnd = "2010-01-01 13:00";
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}
