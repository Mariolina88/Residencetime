/**
 * 
 */
package integrali;
import org.apache.commons.math3.analysis.integration.*;
import org.apache.commons.math3.analysis.polynomials.*;
/**
 * @author marialaura
 *
 */
public class IntegralePolinomio  {
  
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SimpsonIntegrator simpson = new SimpsonIntegrator();

	    double[] vector = new double[1];
	    vector[0] =1;

	    PolynomialFunction f = new PolynomialFunction(vector);
	    //System.out.println("y = " + f.toString());
	    //System.out.println("Degree: " + f.degree());
	    double i = simpson.integrate(10, f, 0, 10);
	    System.out.println("Simpson integral : " + i);        

	}

}
