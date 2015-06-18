package linear.reservoir;

public class Context {
	int mode;
	double a;
	double b;
	StrategyTheta strategy;

	public Context(int mode, double a,double b, StrategyTheta strategy) {
		this.a=a;
		this.b=b;
		this.mode = mode;
		this.strategy = strategy;
	}

	public void setStrategy(StrategyTheta strategy) {
		this.strategy = strategy;
	}

	public int getMode() {
		return mode;
	}

	public double getResultTime() {
		return strategy.checkMode(mode,a,b,c);
	}
	

}
