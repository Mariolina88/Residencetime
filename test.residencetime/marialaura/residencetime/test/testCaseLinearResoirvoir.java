package marialaura.residencetime.test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import linear.reservoir.LinearReservoir_3_OmsConInput;

import org.geotools.data.DataUtilities;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import org.jgrasstools.gears.utils.math.NumericsUtilities;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.etp.OmsPresteyTaylorEtpModel;
import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class testCaseLinearResoirvoir extends HMTestCase {
	
	public void testLinear() throws Exception {
		LinearReservoir_3_OmsConInput l = new LinearReservoir_3_OmsConInput();
		//l.pPathtoMeas = "/Users/giuseppeformetta/Desktop/MarialauraWork/LWRB/data/Downwellingm_CLEAR";
		
		//l.pDoReadMeas = true;
		l.inPathToPrec = "/Users/marialaura/Desktop/Precipitazione.csv";
		l.inPathToDischarge = "/Users/marialaura/Desktop/Q.csv";
		l.inPathToET ="/Users/marialaura/Desktop/ET.csv";
		
		l.pathToQout= "/Users/marialaura/Desktop/ProvaOUT.csv";
		l.inTimestep = 60;

		l.tStartDate = "2010-01-01 00:00";
		l.tEndDate = "2010-01-01 13:00";
		l.id=8;

		l.process();
     
	}
}
