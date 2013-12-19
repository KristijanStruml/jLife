package life.lib;

public class Cell {

	private boolean alive;
	private int neighbourCount;

	public Cell() {
		alive = false;
		neighbourCount = 0;
	}

	public void animate() {
		alive = true;
	}

	public void kill() {
		alive = false;
	}

	public boolean isAlive() {
		return alive;
	}

	public int getNeighbourCount() {
		return neighbourCount;
	}

	public void incrementNeighbourCount() {
		neighbourCount++;
	}

	public void decrementNeighbourCount() {
		neighbourCount--;
	}

	public static Cell[][] createPopulation(int rows, int columns) {
		if(rows < 1 || columns < 1) {
			throw new IllegalArgumentException("Both dimensions must be greater than zero.");
		}

		Cell[][] population = new Cell[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				population[i][j] = new Cell();
			}
		}
		return population;
	}
}
