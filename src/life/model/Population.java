package life.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Population {

	private Cell[][] population;
	private int rows;
	private int columns;

	public Population(int rows, int columns) {
		if(rows < 1 || columns < 1) {
			throw new IllegalArgumentException("Both dimensions must be greater than zero.");
		}

		this.rows = rows;
		this.columns = columns;
		population = Cell.createPopulation(rows, columns);
	}

	public int getRowCount() {
		return rows;
	}

	public int getColumnCount() {
		return columns;
	}

	public Cell getCell(int row, int col) {
		if(row < 0 || col < 0 || row >= rows || col >= columns) {
			throw new IndexOutOfBoundsException("Index must be in " + rows + "×" + columns + ".");
		}
		return population[row][col];
	}

	public boolean isAlive(int row, int col) {
		if(row < 0 || col < 0 || row >= rows || col >= columns) {
			throw new IndexOutOfBoundsException("Index must be in " + rows + "×" + columns + ".");
		}
		return population[row][col].isAlive();
	}

	public void animateCell(int row, int col) {
		if(row < 0 || col < 0 || row >= rows || col >= columns) {
			return;
		}
		if(population[row][col].isAlive()) {
			return;
		}

		population[row][col].animate();

		for(int i = row - 1; i <= row + 1; i++) {
			for(int j = col - 1; j <= col + 1; j++) {
				if(i == row && j == col) {
					continue;
				}
				try {
					population[i][j].incrementNeighbourCount();
				} catch(IndexOutOfBoundsException e) { }
			}
		}
	}

	public void killCell(int row, int col) {
		if(row < 0 || col < 0 || row >= rows || col >= columns) {
			return;
		}
		if(!population[row][col].isAlive()) {
			return;
		}

		population[row][col].kill();

		for(int i = row - 1; i <= row + 1; i++) {
			for(int j = col - 1; j <= col + 1; j++) {
				if(i == row && j == col) {
					continue;
				}
				try {
					population[i][j].decrementNeighbourCount();
				} catch(IndexOutOfBoundsException e) { }
			}
		}
	}

	public void nextGeneration() {
		Population next = new Population(rows, columns);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				if(population[i][j].isAlive()
						&& (population[i][j].getNeighbourCount() == 2 || population[i][j].getNeighbourCount() == 3)) {
					next.animateCell(i, j);
				}
				if(!population[i][j].isAlive() && population[i][j].getNeighbourCount() == 3) {
					next.animateCell(i, j);
				}
			}
		}
		population = next.population;
	}

	public static Population fromFile(String path) {
		try {
			InputStream is = Population.class.getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(is),
					StandardCharsets.UTF_8));

			List<String> input = new ArrayList<String>();
			String line = null;

			while((line = br.readLine()) != null) {
				input.add(line);
			}

			int rows = input.size();
			int cols = input.get(0).length();
			Population p = new Population(rows, cols);

			for(int i = 0; i < input.size(); i++) {
				char[] array = input.get(i).toCharArray();
				for(int j = 0; j < input.get(0).length(); j++) {					
					if(array[j] == '#') {
						p.animateCell(i, j);
					}
				}
			}
			return p;
		} catch(Exception e) {
			return new Population(40, 60);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				sb.append(population[i][j].isAlive() ? "#" : "-");
			}
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}
}
