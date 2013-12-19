package life.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import custom.swing.CellGrid;
import custom.swing.IGridObserver;
import examples.ExampleManager;

import life.model.Population;

public class LifeGUI extends JFrame implements IGridObserver {

	private static final long serialVersionUID = 1L;

	private static final int ANIMATION_DELAY = 40;

	private Population population;
	private CellGrid biotope;

	private Thread life;
	private Object lock;
	private boolean paused;
	private volatile boolean running;

	private Map<String, Action> actions = new HashMap<String, Action>();

	private JButton btnStart;
	private JButton btnNext;
	private JButton btnPause;
	private JButton btnClear;
	private JComboBox<String> jcbPatternChooser;

	public LifeGUI() {
		population = Population.fromFile("/examples/Glider.txt");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("jLife");
		setResizable(false);
		initActions();
		initGUI();
		displayPopulation();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				paused = false;
				synchronized(lock) {
					lock.notifyAll();
				}
				running = false;
			}			
		});
	}

	private void initGUI() {
		biotope = new CellGrid(population.getRows(), population.getColumns());
		biotope.registerObserver(this);
		
		life = new Thread(new Life());		
		lock = new Object();

		btnStart = new JButton(actions.get("start"));
		btnNext = new JButton(actions.get("step"));
		btnPause = new JButton(actions.get("pause"));
		btnClear = new JButton(actions.get("clear"));

		jcbPatternChooser = createPatternMenu();
		JToolBar toolbar = createToolbar();

		getContentPane().add(toolbar, BorderLayout.PAGE_START);
		getContentPane().add(biotope, BorderLayout.CENTER);

		pack();
	}

	private class Life implements Runnable {
		@Override
		public void run() {
			while(running) {
				suspendWhilePaused();
				population.nextGeneration();
				displayPopulationEDT();
				sleep(ANIMATION_DELAY);
			}
		}
	}

	private void suspendWhilePaused() {
		synchronized(lock) {
			while(paused) {
				try {
					lock.wait();
				} catch(InterruptedException e) {}
			}
		}
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch(InterruptedException e) {}
	}

	private void initActions() {

		Action action = new AbstractAction("START") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				running = true;
				paused = false;
				biotope.disableMouse();

				if(!life.isAlive()) {
					life.start();
				} else {
					synchronized(lock) {
						lock.notifyAll();
					}
				}
				actions.get("start").setEnabled(false);
				actions.get("step").setEnabled(false);
				actions.get("pause").setEnabled(true);
				actions.get("clear").setEnabled(false);
			}
		};

		actions.put("start", action);

		action = new AbstractAction("STEP") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				population.nextGeneration();
				displayPopulation();
			}
		};

		actions.put("step", action);

		action = new AbstractAction("PAUSE") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				paused = true;
				biotope.enableMouse();
				actions.get("start").setEnabled(true);
				actions.get("step").setEnabled(true);
				actions.get("pause").setEnabled(false);
				actions.get("clear").setEnabled(true);
			}
		};

		action.setEnabled(false);
		actions.put("pause", action);

		action = new AbstractAction("CLEAR") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				population = new Population(population.getRows(), population.getColumns());
				biotope.clear();
				displayPopulation();
			}
		};
		
		actions.put("clear", action);
	}

	private JComboBox<String> createPatternMenu() {

		jcbPatternChooser = new JComboBox<String>(ExampleManager.getNames());
		final Map<Integer, String> examples = ExampleManager.getMap();

		jcbPatternChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				paused = false;
				running = false;
				biotope.enableMouse();

				synchronized(lock) {
					lock.notify();
				}

				population = Population.fromFile(examples.get(jcbPatternChooser.getSelectedIndex()));
				displayPopulation();

				life = new Thread(new Life());

				actions.get("start").setEnabled(true);
				actions.get("step").setEnabled(true);
				actions.get("pause").setEnabled(false);
				actions.get("clear").setEnabled(true);
			}
		});

		return jcbPatternChooser;
	}

	private JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);

		toolbar.add(btnStart);
		toolbar.add(btnNext);
		toolbar.add(btnPause);
		toolbar.addSeparator();
		toolbar.add(jcbPatternChooser);
		toolbar.addSeparator();
		toolbar.add(btnClear);

		return toolbar;
	}
	
	private void displayPopulationEDT() {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				displayPopulation();				
			}
		});
	}

	private void displayPopulation() {
		biotope.clear();
		for(int i = 0; i < biotope.getRows(); i++) {
			for(int j = 0; j < biotope.getColumns(); j++) {
				if(population.isAlive(i, j)) {
					biotope.turnOn(i, j);
				}
			}
		}
	}

	@Override
	public void cellTurnedOn(int row, int column) {
		population.animateCell(row, column);

	}

	@Override
	public void cellTurnedOff(int row, int column) {
		population.killCell(row, column);
	}
}
