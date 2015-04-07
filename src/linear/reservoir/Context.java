package linear.reservoir;

public class Context {
	int mode;
	Strategy strategy;

	public Context(int mode, Strategy strategy) {
		this.mode = mode;
		this.strategy = strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public int getMode() {
		return mode;
	}

	public int getResult() {
		return strategy.checkMode(mode);
	}

}
