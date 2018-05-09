package gameplay.maps;

import java.io.Serializable;

import clientside.gui.GamePanel;
import processing.core.PApplet;

/**
 * 
 * Stores information for the game map
 * 
 * @author shaylandias
 *
 */
public abstract class Map implements Serializable{
	
	private static String imageKey = "FNOLGO MAP";
	public Map() {
	}
	
	public void draw(PApplet surface) {

	}


}
