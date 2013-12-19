package life.gui;

import javax.swing.SwingUtilities;

public class Program {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new LifeGUI().setVisible(true);
			}
		});
	}
}
