package linear.reservoir;
import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.jgrasstools.gears.libs.modules.JGTModel;

import java.io.IOException;


public class InputDataP {

	
	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;
	double precipitation;

	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;
	double ET;

	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;
	
	double discharge;

	@Description("InputWaterStorage")
	@In
	public HashMap<Integer, double[]> inWaterStoragevalues;
	double S;
	
	
	@Description("Input p")
	@In
	public HashMap<Integer, double[]> inPvalues;
	double p;
	
	



	public InputDataP (HashMap<Integer, double[]> inPrecipvalues, HashMap<Integer, double[]> inETvalues, 
			HashMap<Integer, double[]> inDischargevalues,HashMap<Integer, double[]> inWaterStoragevalues,
			HashMap<Integer, double[]> inPvalues){
		
		Set<Entry<Integer, double[]>> entrySet = inPrecipvalues.entrySet();

		for( Entry<Integer, double[]> entry : entrySet ) {
			
			Integer basinId = entry.getKey();

			
			precipitation = entry.getValue()[0];	
			if (isNovalue(precipitation)) {
				precipitation = 0;
			} 	
			
			ET = inETvalues.get(basinId)[0];
			if (isNovalue(ET)) {
				ET= 0;
			} else {
				ET = inETvalues.get(basinId)[0];
			}
			
			discharge =inDischargevalues.get(basinId)[0];
			if (isNovalue(discharge)) {
				discharge= 0;
			} else {
				discharge = inDischargevalues.get(basinId)[0];
			}
			
			S= inWaterStoragevalues.get(basinId)[0];
			if (isNovalue(S)) {
				S= 0;
			} else {
				S = inWaterStoragevalues.get(basinId)[0];
			}
			
			p= inPvalues.get(basinId)[0];
			if (isNovalue(p)) {
				p= 0;
			} else {
				p = inPvalues.get(basinId)[0];
			}
		}
	}
		
	 
	 public double getJ(){
	       return this.precipitation; 
	    }
	 
	 public double getET(){
	       return this.ET; 
	    }
	
	 public double getQ(){
	       return this.discharge; 
	    }
	 
	 public double getWS(){
	       return this.S; 
	    }
	 
	 public double getp(){
	       return this.p; 
	    }
	}
	


