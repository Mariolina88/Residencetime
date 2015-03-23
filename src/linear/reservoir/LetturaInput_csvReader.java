/**
 * 
 */
package linear.reservoir;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import oms3.annotations.Description;
import oms3.annotations.In;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.opencsv.CSVReader;

/**
 * @author marialaura
 *
 */
public class LetturaInput_csvReader {

	String tStartDate = null;
	String tEndDate = null;
	String inPathTo=null;
	String [][] matrix=null;

	public LetturaInput_csvReader (String inPathTo) throws IOException{
		CSVReader reader = new CSVReader(new FileReader(inPathTo), ',', '\'', 8);
		String[] dataRow = reader.readNext();
		int rows=0;
		int cols=dataRow.length;
		while (dataRow != null){
			rows++;
			dataRow = reader.readNext();
		}

		reader.close();

		CSVReader reader_p = new CSVReader(new FileReader(inPathTo), ',', '\'', 8);

		this.matrix=new String [rows][cols];
		String [] nextLineP;
		for(int i=0;i<rows;i++){
			nextLineP = reader_p.readNext();
			for(int k=0;k<cols;k++){
				matrix[i][k]=nextLineP[k];

			}

		} 

		reader_p.close();
	
		


	}
}
