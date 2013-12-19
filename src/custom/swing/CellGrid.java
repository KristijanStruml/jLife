package custom.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class CellGrid extends JComponent {

	private static final long serialVersionUID = 1L;

	private int rows;
	private int columns;
	private int cellSize;
	private Color color;
	private boolean[][] gridElements;

	private int width;
	private int height;

	private List<IGridObserver> observers;
	private boolean mouseEnabled = true;
	private MouseListener mouseListener;

	public CellGrid(int rows, int columns, int cellSize, Color color) {
		if(rows < 0) {
			throw new IllegalArgumentException("Number of rows must be greater than zero!");
		}
		if(columns < 0) {
			throw new IllegalArgumentException("Number of columns must be greater than zero!");
		}
		if(cellSize < 0) {
			throw new IllegalArgumentException("Cell size must be greater than zero!");
		}

		this.rows = rows;
		this.columns = columns;
		this.cellSize = cellSize;
		this.color = color;

		gridElements = new boolean[rows][columns];
		
		observers = new ArrayList<>();

		height = rows * cellSize;
		width = columns * cellSize;
		setPreferredSize(new Dimension(width, height));

		initMouseListener();
		addMouseListener(mouseListener);
	}

	public CellGrid(int rows, int columns, int cellSize) {
		this(rows, columns, cellSize, Color.black);
	}

	public CellGrid(int rows, int columns) {
		this(rows, columns, 10, Color.black);
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public void registerObserver(IGridObserver obs) {
		observers.add(obs);
	}

	public void turnOn(int row, int column) {
		if(row >= 0 && column >= 0 && row < rows && column < columns) {
			gridElements[row][column] = true;
			repaint();
			
			for(IGridObserver obs : observers) {
				obs.cellTurnedOn(row, column);
			}
		}
	}

	public void turnOff(int row, int column) {
		if(row >= 0 && column >= 0 && row < rows && column < columns) {
			gridElements[row][column] = false;
			repaint();
			
			for(IGridObserver obs : observers) {
				obs.cellTurnedOff(row, column);
			}
		}
	}

	public void clear() {
		gridElements = new boolean[rows][columns];
		repaint();
	}

	public void enableMouse() {
		if(!mouseEnabled) {
			mouseEnabled = true;
			addMouseListener(mouseListener);
		}
	}

	public void disableMouse() {
		if(mouseEnabled) {
			mouseEnabled = false;
			removeMouseListener(mouseListener);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.gray);
		for(int i = 0; i <= rows; i++) {
			g.drawLine(0, i * cellSize, width, i * cellSize);
		}
		for(int i = 0; i <= columns; i++) {
			g.drawLine(i * cellSize, 0, i * cellSize, height);
		}

		g.setColor(color);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				if(gridElements[i][j]) {
					g.fillRect(1 + j * cellSize, 1 + i * cellSize, cellSize - 1, cellSize - 1);
				}
			}
		}
	}

	private void initMouseListener() {
		mouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int row = (e.getY()) / cellSize;
				int col = (e.getX()) / cellSize;

				if(row < 0 || col < 0 || row >= rows || col >= columns) {
					return;
				}

				if(gridElements[row][col] == false) {
					turnOn(row, col);
				} else {
					turnOff(row, col);
				}
				repaint();
			}
		};
	}
}
