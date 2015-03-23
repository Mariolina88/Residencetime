package marialaura.residencetime.test;

import linear.reservoir.*;

import org.jgrasstools.hortonmachine.utils.HMTestCase;

public class testNew extends HMTestCase {
	public String inPathToPrec = "/Users/marialaura/Desktop/Taria.csv";

	public String inPathToDischarge = "/Users/marialaura/Desktop/Taria.csv";

	public String inPathToNetRadiation = "/Users/marialaura/Desktop/Taria.csv";

	public String inPathToAirTemperature = "/Users/marialaura/Desktop/Taria.csv";

	public String inPathToPressure = "/Users/marialaura/Desktop/Taria.csv";
	public String PathToOutput="/Users/marialaura/Desktop/ProvaOUT.csv";

	public void testLinearNew() throws Exception {
	LinearReservoir_2_csvReader linearNew = new LinearReservoir_2_csvReader();
	linearNew.inPathToPrec=inPathToPrec;
	linearNew.inPathToDischarge=inPathToDischarge;
	linearNew.inPathToNetRadiation=inPathToNetRadiation;
	linearNew.inPathToAirTemperature=inPathToAirTemperature;
	linearNew.inPathToPressure=inPathToPressure;
	linearNew.pathToOutput=PathToOutput;

	linearNew.process();

	}

}
