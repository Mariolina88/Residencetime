package linear.reservoir;

public class Context {
	int mode;
	double a;
	Strategy strategy;

	public Context(int mode, double a, Strategy strategy) {
		this.a=a;
		this.mode = mode;
		this.strategy = strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public int getMode() {
		return mode;
	}

	public double getResultTime() {
		return strategy.checkMode(mode,a);
	}
	

}
