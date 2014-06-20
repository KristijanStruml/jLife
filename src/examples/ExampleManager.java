package examples;

import java.util.HashMap;
import java.util.Map;

public class ExampleManager {
	
	public static String[] getExampleNames() {		
		String[] patterns = { 
				"Glider", 
				"Lightweight spaceship", 
				"Toad (period 2 oscillator)",
				"Pulsar (period 3 oscillator)", 
				"R-pentomino", 
				"10 cell row", 
				"Gosper glider gun" 
		};		
		return patterns;
	}
	
	public static Map<Integer, String> getExampleMap() {		
		Map<Integer, String> examples = new HashMap<Integer, String>();
		
		examples.put(0, "/examples/Glider.txt");
		examples.put(1, "/examples/Spaceship.txt");
		examples.put(2, "/examples/Toad.txt");
		examples.put(3, "/examples/Pulsar.txt");
		examples.put(4, "/examples/R.txt");
		examples.put(5, "/examples/10.txt");
		examples.put(6, "/examples/Cannon.txt");
		
		return examples;
	}
}
