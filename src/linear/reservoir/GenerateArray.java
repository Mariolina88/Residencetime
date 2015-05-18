package linear.reservoir;

import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import org.jgrasstools.gears.libs.modules.JGTModel;

import java.io.IOException;


public class GenerateArray extends JGTModel{


	@Description("Input Precipitation")
	@In
	public HashMap<Integer, double[]> inPrecipvalues;

	@Description("Input ET")
	@In
	public HashMap<Integer, double[]> inETvalues;

	@Description("InputDischarge")
	@In
	public HashMap<Integer, double[]> inDischargevalues;

	@Description("InputWaterStorage")
	@In
	public HashMap<Integer, double[]> inWaterStoragevalues;

	public double A;

	@Description("The matrix of output discharge")
	@Out
	public HashMap<Integer, double[]> outHMQout;

	int i=-1;

	@Execute
	public void process() throws Exception {
		checkNull(inPrecipvalues,inDischargevalues,inETvalues,inWaterStoragevalues);

		ArrayList v=new ArrayList();

		Set<Entry<Integer, double[]>> entrySet = inPrecipvalues.entrySet();
		for( Entry<Integer, double[]> entry : entrySet ) {
			
			i=i+1;
			v.add(i);
			
			Integer basinId = entry.getKey();
			
			double precipitation = entry.getValue()[0];	
			if (isNovalue(precipitation)) {
				precipitation = 0;
			} 

			v.add(precipitation);
			
			double discharge =inDischargevalues.get(basinId)[0];
			if (isNovalue(discharge)) {
				discharge= 0;
			} else {
				discharge = inDischargevalues.get(basinId)[0];
			}
			v.add(discharge/A*3.6);
				
			
			double ET = inETvalues.get(basinId)[0];
			if (isNovalue(ET)) {
				ET= 0;
			} else {
				ET = inETvalues.get(basinId)[0];
			}
			v.add(ET);
			
			double S = inWaterStoragevalues.get(basinId)[0];
			if (isNovalue(S)) {
				S= 0;
			} else {
				S = inWaterStoragevalues.get(basinId)[0];
			}
			v.add(S);
			
			System.out.println(v);	
		}
		
	}



}

