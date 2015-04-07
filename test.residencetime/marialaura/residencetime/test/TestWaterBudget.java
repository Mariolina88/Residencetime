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

public class TestWaterBudget extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "1995-01-01 00:00";
		String endDate = "1996-01-01 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);

		String inPathToPrec = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/rainfall.csv";
		String inPathToDischarge = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/Q.csv";
		String inPathToET ="/Users/marialaura/Desktop/dottorato/Idrologia/dati/ET.csv";
		String pathToQout= "/Users/marialaura/Desktop/OUTmode1_S.csv";

		OmsTimeSeriesIteratorReader precipitationReader = getTimeseriesReader(inPathToPrec, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader dischargeReader = getTimeseriesReader(inPathToDischarge, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader ETReader = getTimeseriesReader(inPathToET, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorWriter writer = new OmsTimeSeriesIteratorWriter();

		writer.file = pathToQout;
		writer.tStart = startDate;
		writer.tTimestep = timeStepMinutes;
		writer.fileNovalue="-9999";
		
		WaterBudget waterBudget= new WaterBudget();


		while( precipitationReader.doProcess ) {
		
			waterBudget.mode=2;
			precipitationReader.nextRecord();

			HashMap<Integer, double[]> id2ValueMap = precipitationReader.outData;
			waterBudget.inPrecipvalues = id2ValueMap;
			
			dischargeReader.nextRecord();
            id2ValueMap = dischargeReader.outData;
            waterBudget.inDischargevalues = id2ValueMap;
            
            ETReader.nextRecord();
            id2ValueMap = ETReader.outData;
            waterBudget.inETvalues = id2ValueMap;

            waterBudget.pm = pm;
            waterBudget.process();
            
            HashMap<Integer, double[]> outHM = waterBudget.outHMQout;
            
			writer.inData = outHM;
			writer.writeNextLine();
			
			if (pathToQout != null) {
				writer.close();
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
		reader.tStart = "1995-01-01 00:00";
		reader.tTimestep = 60;
		reader.tEnd = "1996-01-01 00:00";
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}
}
