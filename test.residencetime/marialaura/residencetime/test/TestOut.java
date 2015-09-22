package marialaura.residencetime.test;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import linear.reservoir.Reader;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class TestOut extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "1994-01-01 00:00";
		String endDate = "1994-01-02 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);

		String inPathToTime = "/Users/marialaura/Desktop/dottorato/Idrologia/dati/time.csv";
		String pathToQout= "/Users/marialaura/Desktop/QOUT_prova.csv";
		String pathToETout= "/Users/marialaura/Desktop/ETOUT_prova.csv";
		String pathToQ= "/Users/marialaura/Desktop/Qend_prova.csv";
		String pathToET= "/Users/marialaura/Desktop/ETend_prova.csv";

		Reader timeReader = getTimeseriesReader(inPathToTime, fId, startDate, endDate, timeStepMinutes);
		Reader QoutReader = getTimeseriesReader(pathToQout, fId, startDate, endDate, timeStepMinutes);
		Reader EtoutReader = getTimeseriesReader(pathToETout, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorWriter writer_Qout = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writer_ETout = new OmsTimeSeriesIteratorWriter();
		
		writer_Qout.file = pathToQ;
		writer_Qout.tStart = startDate;
		writer_Qout.tTimestep = timeStepMinutes;
		writer_Qout.fileNovalue="-9999";
		
		writer_ETout.file = pathToET;
		writer_ETout.tStart = startDate;
		writer_ETout.tTimestep = timeStepMinutes;
		writer_ETout.fileNovalue="-9999";

		
		OutOfTravelTimes2 pdfs= new OutOfTravelTimes2();


		while( timeReader.doProcess ) {
		
			timeReader.nextRecord();
			pdfs.ID=1;



			pdfs.tStartDate=startDate;
			pdfs.tEndDate=endDate;
	

			HashMap<Integer, double[]> id2ValueMap = timeReader.outData;
			pdfs.inTimevalues = id2ValueMap;

			QoutReader.nextRecord();
            id2ValueMap = QoutReader.outData;
            pdfs.inQoutvalues= id2ValueMap;
            
            EtoutReader.nextRecord();
            id2ValueMap = EtoutReader.outData;
            pdfs.inEToutvalues= id2ValueMap;

            pdfs.pm = pm;
            pdfs.process();
            
            HashMap<Integer, double[]> outHM = pdfs.outHMQ;
            HashMap<Integer, double[]> outHMT = pdfs.outHMET;
            
			writer_Qout.inData = outHM;
			writer_Qout.writeNextLine();
			
			if (pathToQout != null) {
				writer_Qout.close();
			}
            
			writer_ETout.inData = outHMT;
			writer_ETout.writeNextLine();
			
			if (pathToETout != null) {
				writer_ETout.close();
			}
			
            //double value = outHM.get(8)[0];
            //assertTrue(NumericsUtilities.dEq(value, 3.7612114870933824));
            //break;
		}
		
		timeReader.close();
		QoutReader.close();
		EtoutReader.close();
		
		

	}


	private Reader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		Reader reader = new Reader();
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
