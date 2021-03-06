package gameplay.attacks;

import java.awt.geom.Rectangle2D;

import clientside.gui.GamePanel;
import processing.core.PApplet;

/*
 * Represents a moving image.
 *
 * by: Shelby
 * on: 5/3/13
 */

/**
 * 
 * Shelby's MovingImage class for our convenience
 * 
 * @author shaylandias
 *
 */
public class MovingSprite extends Rectangle2D.Double {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3684240064305938973L;
	// FIELDS
	/**
	 * The Sprites
	 */
	protected String imageKey;

	/**
	 * 
	 * Creates a MovingImage with the given image and dimensions x, y, w, h
	 * 
	 * @param imageKey
	 *            The Resources key for the image for this MovingSprite
	 * @param x
	 *            The X-Coordinate of the upper left corner
	 * @param y
	 *            The Y-Coordinate of the upper left corner
	 * @param w
	 *            The width of the image
	 * @param h
	 *            The height of the image
	 */
	public MovingSprite(String imageKey, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.imageKey = imageKey;
	}

	// METHODS
	/**
	 * 
	 * Moves to a specific location
	 * 
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 */
	public void moveToLocation(double x, double y) {
		super.x = x;
		super.y = y;
	}

	/**
	 * 
	 * Moves by an amount
	 * 
	 * @param x
	 *            X to move by
	 * @param y
	 *            Y to move by
	 */
	public void moveByAmount(double x, double y) {
		super.x += x;
		super.y += y;
	}

	/**
	 * 
	 * Applies window limits
	 * 
	 * @param windowWidth
	 *            Window width
	 * @param windowHeight
	 *            Window height
	 */
	public void applyWindowLimits(int windowWidth, int windowHeight) {
		x = Math.min(x, windowWidth - width);
		y = Math.min(y, windowHeight - height);
		x = Math.max(0, x);
		y = Math.max(0, y);
	}

	/**
	 * 
	 * Draws this image
	 * 
	 * @param g
	 *            The surface to draw to
	 * @param time
	 *            The server time of drawing
	 */
	public void draw(PApplet g, long time) {
		g.pushStyle();
		g.image(GamePanel.resources.getImage(imageKey), (int) x, (int) y, (int) width, (int) height);
		g.popStyle();
	}

}
