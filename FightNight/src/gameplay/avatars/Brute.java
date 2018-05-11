package gameplay.avatars;

import java.awt.Rectangle;

import clientside.gui.GamePanel;
import gameplay.attacks.Attack;
import gameplay.attacks.Fireball;
import gameplay.attacks.Howl;
import gameplay.attacks.Lunge;
import gameplay.attacks.MeleeAttack;
import gameplay.attacks.StatusEffect;
import gameplay.attacks.StatusEffect.Effect;
import gameplay.attacks.TrailingAttack;
import processing.core.PApplet;

/**
 * Creates a specific brute type of character.
 * 
 * @author hzhu684
 *
 */
public class Brute extends Avatar {

	private String[] deathImageKeys;
	private String[] upperCutKeys;
	private String[] howlKeys;
	private AttackType currentAttack;
	private final double upperCutTime = 1.1;
	private double upperCutAngle;

	/**
	 * Instantiates a Brute
	 */
	public Brute() {
		super();
		spriteSheetKey = "WWDefault";
		sprites = new Rectangle[] { new Rectangle(62, 94, 85, 65) };
		hitbox.height = sprites[0].height;
		hitbox.width = sprites[0].width;
		dashCD = 1.0;
		dashDistance = 120;
		dashSpeed = 40;
		a1CD = 7;
		basicCD = 0.3;
		a2CD = 10;
		super.basicCD = 0.2;
		rangedCDStart = 0;
		rangedCD = 3;
		currentAttack = AttackType.NONE;
		a3CD = 15;
		deathImageKeys = new String[] {"WWDying", "WWDead"};
		upperCutKeys = new String[] {"UpperCut1", "UpperCut2", "UpperCut3", "UpperCut4", "UpperCut5", "UpperCut6", "UpperCut7", "UpperCut8"};
		howlKeys = new String[] {"Howl1", "Howl2", "Howl3", "Howl4"};
		getSpriteListWalk().add("WWWalk0");
		getSpriteListWalk().add("WWWalk1");
		getSpriteListWalk().add("WWWalk2");
		getSpriteListWalk().add("WWWalk3");
		numOfSpriteWalk = 4;
	}

	/**
	 * 
	 * Creates a Brute at this x,y
	 * 
	 * @param x
	 * @param y
	 */
	public Brute(double x, double y) {
		this();
		this.hitbox.x = x;
	}

	public void act() {
		if(currentAttack == AttackType.A1)
			actUpperCut();
		else if(currentAttack == AttackType.RANGED)
			rangedAct();
		else if(currentAttack == AttackType.BASIC)
			basicAct();
		else if(currentAttack == AttackType.A2)
			actHowl();
		else if(currentAttack == AttackType.A3)
			actFury();
		else {
			super.act();
			if (!super.isLeft() && !super.isRight() && !super.isUp() && !super.isDown()) {
				spriteSheetKey = "WWDefault";
			}
		}
	
	}

	public void draw(PApplet surface) {
		surface.pushMatrix();
		surface.pushStyle();
	
		drawHealthBar(surface);
	
		if(deathTime != 0) {
			drawDeath(surface);
			surface.popMatrix();
			surface.popStyle();
			return;
		}
	
		if (blocking) {
			// Draw block
		}
	
		int sw, sh;
		//		sx = (int) sprites[spriteInd].getX();
		//		sy = (int) sprites[spriteInd].getY();
		sw = (int) sprites[spriteInd].getWidth();
		sh = (int) sprites[spriteInd].getHeight();
	
		surface.imageMode(PApplet.CENTER);
	
		if (super.getStatus().getEffect().equals(Effect.STUNNED)) {
			surface.image(GamePanel.resources.getImage("Stun"), (float)hitbox.x, (float)(hitbox.y - hitbox.height * 1.1), 30, 30);
		}
		
		surface.pushMatrix();
	
		float widthMod = 1f;
		if(currentlyAttacking)
			widthMod = 1.3f;
		
		if (!lastDir) {
			surface.scale(-1, 1);
			surface.image(GamePanel.resources.getImage(spriteSheetKey), (float) -hitbox.x, (float) hitbox.y, -sw * widthMod, sh);
		} else {
			surface.image(GamePanel.resources.getImage(spriteSheetKey), (float) hitbox.x, (float) hitbox.y, sw * widthMod, sh);
		}
		surface.popMatrix();
		if (blocking) {
			if (System.currentTimeMillis() / 250 % 5 == 0) {
				surface.tint(140);
			} else if (System.currentTimeMillis() / 250 % 5 == 1) {
				surface.tint(170);
			} else if (System.currentTimeMillis() / 250 % 5 == 2) {
				surface.tint(200);
			} else if (System.currentTimeMillis() / 250 % 5 == 3) {
				surface.tint(240);
			}
			surface.image(GamePanel.resources.getImage(blockImageKey), (float) hitbox.x, (float) hitbox.y, 1.5f * sw,
					1.5f * sh);
		}
	
		// surface.rect((float)hitbox.x, (float)hitbox.y, (float)sw, (float)sh);
		// surface.fill(Color.RED.getRGB());
		// surface.ellipseMode(PApplet.CENTER);
		// surface.ellipse((float)(hitbox.x), (float)(hitbox.y), 5f, 5f);
	
		surface.popMatrix();
		surface.popStyle();
	}

	protected void drawDeath(PApplet surface){
		if(System.currentTimeMillis() <= deathTime + 1000)
			surface.image(GamePanel.resources.getImage(deathImageKeys[0]), (float)hitbox.x, (float)hitbox.y, (float)hitbox.width, (float)hitbox.height);
		else {
			surface.image(GamePanel.resources.getImage(deathImageKeys[1]), (float)hitbox.x, (float)hitbox.y, (float)hitbox.width * 1.2f, (float)hitbox.height * 0.7f);
		}
	}

	@Override
	public void dash(Double mouseAngle) {
		if (System.currentTimeMillis() > super.dashCDStart + super.dashCD * 1000) {
			super.dashCDStart = System.currentTimeMillis();
			super.dash(mouseAngle);
		}
	}

	// Punch, slow but does a lot of dmg
	@Override
	public Attack[] basicAttack(String player, double angle) {
		if (System.currentTimeMillis() > super.basicCDStart + super.basicCD * 1000 && !dashing && !blocking) {
			currentlyAttacking = true;
			basicCDStart = System.currentTimeMillis();
			currentAttack = AttackType.BASIC;
			timeActionStarted = System.currentTimeMillis();
			if(angle > 90 && angle < 270) 
				lastDir = true;
			else
				lastDir = false;
			return new Attack[] {new MeleeAttack("WWBasic", (int)( hitbox.x + 75 * Math.cos(Math.toRadians(angle))), (int)( hitbox.y - 75 * Math.sin(Math.toRadians(angle))), 40, 40, player, 20,
					false, new StatusEffect(Effect.NONE,0,0), angle, 0.15)};
		} else {
			return null;
		}
	}

	// Throws a slow moving projectile (Rock)
	@Override
	public Attack[] rangedAttack(String player, double angle) {
		if (System.currentTimeMillis() > super.rangedCDStart + super.rangedCD * 1000 && !dashing && !blocking) {
			super.rangedCDStart = System.currentTimeMillis();
			currentlyAttacking = true;
			currentAttack = AttackType.RANGED;
			timeActionStarted = System.currentTimeMillis();
			if(angle > 90 && angle < 270)
				lastDir = true;
			else
				lastDir = false;
			return new Attack[] {new Fireball((int) hitbox.x, (int) hitbox.y, player, angle, "Rock", 400, 22)};
		} else {
			return null;
		}
	
	}

	// UpperCut - dashes forwards and stuns someone
	@Override
	public Attack[] abilityOne(String player, double angle) {
		currentlyAttacking = true;
		movementControlled = false;
		currentAttack = AttackType.A1;
		upperCutAngle = 360 - angle;
		if(angle > 90 && angle < 270)
			lastDir = true;
		else
			lastDir = false;
		a1CDStart = System.currentTimeMillis();
		timeActionStarted = a1CDStart;
		return new Attack[] {new Lunge(this.getPlayer(), angle, this, (int)super.getX(), (int)super.getY(), new StatusEffect(Effect.STUNNED, 1.5d, 1), 1.1, 0.25)};
	}

	// GroundSmash, and aoe stun that does dmg
	@Override
	public Attack[] abilityTwo(String player, double angle) {
		currentlyAttacking = true;
		movementControlled = false;
		currentAttack = AttackType.A2;
		if(angle > 90 && angle < 270)
			lastDir = true;
		else
			lastDir = false;
		a2CDStart = System.currentTimeMillis();
		timeActionStarted = a2CDStart;
		return new Attack[] {new Howl((int)super.getX(), (int)super.getY(), player)};
	}

	// Fury- Swipes three times at a location
	@Override
	public Attack[] abilityThree(String player, double angle) {
		currentlyAttacking = true;
		movementControlled = false;
		currentAttack = AttackType.A3;
		upperCutAngle = 360 - angle;
		if(angle > 90 && angle < 270)
			lastDir = true;
		else
			lastDir = false;
		a3CDStart = System.currentTimeMillis();
		timeActionStarted = a3CDStart;
		return new Attack[]{new Lunge(this.getPlayer(), angle, this, (int)super.getX(), (int)super.getY(), new StatusEffect(Effect.NONE, 0, 0), 0.9, 0),
				new TrailingAttack("WWBasic", 50, 50, 60, 50, player, 4, new StatusEffect(Effect.NONE,0,0), angle + 90, 0.9, this),
				new TrailingAttack("WWBasic", -50, -50, 60, 50, player, 4, new StatusEffect(Effect.NONE,0,0), angle + 90, 0.9, this)};
	}

	private void basicAct() {
		if(System.currentTimeMillis() < timeActionStarted + 0.06 * 1000) {
			spriteSheetKey = upperCutKeys[7];
		} else if(System.currentTimeMillis() < timeActionStarted +  0.15 * 1000 ) {
			spriteSheetKey = upperCutKeys[5];
		} else {
			currentlyAttacking = false;
			currentAttack = AttackType.NONE;
		}
	}

	private void rangedAct() {
		if(System.currentTimeMillis() < timeActionStarted + 0.2 * 1000) {
			spriteSheetKey = upperCutKeys[7];
		} else if(System.currentTimeMillis() < timeActionStarted +  0.4 * 1000 ) {
			spriteSheetKey = upperCutKeys[6];
		} else {
			currentlyAttacking = false;
			currentAttack = AttackType.NONE;
		}

	}

	private void actUpperCut() {
		if(System.currentTimeMillis() < timeActionStarted + 0.1 * 1000) {
			spriteSheetKey = upperCutKeys[0];
		} else if(System.currentTimeMillis() < timeActionStarted +  0.15 * 1000 ) {
			spriteSheetKey = upperCutKeys[1];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.2 * 1000) {
			spriteSheetKey = upperCutKeys[2];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.3 * 1000) {
			spriteSheetKey = upperCutKeys[3];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.85 * 1000) {
			spriteSheetKey = upperCutKeys[4];
			super.moveDistance(30, upperCutAngle);
		} else if(System.currentTimeMillis() < timeActionStarted + 0.88 * 1000) {
			spriteSheetKey = upperCutKeys[5];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.94 * 1000) {
			spriteSheetKey = upperCutKeys[6];
		} else if(System.currentTimeMillis() < timeActionStarted + 1000) {
			spriteSheetKey = upperCutKeys[7];
		} else if (System.currentTimeMillis() > timeActionStarted + 1000) {
			currentAttack = AttackType.NONE;
			movementControlled = true;
			currentlyAttacking = false;
		}
	}

	private void actHowl() {
		if(System.currentTimeMillis() < timeActionStarted + 0.25 * 1000) {
			spriteSheetKey = howlKeys[0];
		} else if(System.currentTimeMillis() < timeActionStarted +  .5 * 1000 ) {
			spriteSheetKey = howlKeys[1];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.75 * 1000) {
			spriteSheetKey = howlKeys[2];
		} else if(System.currentTimeMillis() < timeActionStarted + 1.25 * 1000) {
			spriteSheetKey = howlKeys[3];
		} else if(System.currentTimeMillis() < timeActionStarted +  1.5 * 1000 ) {
			spriteSheetKey = howlKeys[2];
		} else if(System.currentTimeMillis() < timeActionStarted + 1.75 * 1000) {
			spriteSheetKey = howlKeys[1];
		} else if(System.currentTimeMillis() < timeActionStarted + 2 * 1000) {
			spriteSheetKey = howlKeys[0];
		}else if (System.currentTimeMillis() >= timeActionStarted + 2000) {
			currentAttack = AttackType.NONE;
			movementControlled = true;
			currentlyAttacking = false;
		}
	}

	private void actFury() {
		if(System.currentTimeMillis() < timeActionStarted + 0.1 * 1000) {
			spriteSheetKey = upperCutKeys[0];
		} else if(System.currentTimeMillis() < timeActionStarted +  0.15 * 1000 ) {
			spriteSheetKey = upperCutKeys[1];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.2 * 1000) {
			spriteSheetKey = upperCutKeys[2];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.3 * 1000) {
			spriteSheetKey = upperCutKeys[3];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.9 * 1000) {
			spriteSheetKey = upperCutKeys[4];
			super.moveDistance(30, upperCutAngle);
		} else if(System.currentTimeMillis() < timeActionStarted + 0.88 * 1000) {
			spriteSheetKey = upperCutKeys[5];
		} else if(System.currentTimeMillis() < timeActionStarted + 0.94 * 1000) {
			spriteSheetKey = upperCutKeys[6];
		} else if(System.currentTimeMillis() < timeActionStarted + 1000) {
			spriteSheetKey = upperCutKeys[7];
		} else if (System.currentTimeMillis() > timeActionStarted + 1000) {
			currentAttack = AttackType.NONE;
			movementControlled = true;
			currentlyAttacking = false;
		}
	}
	
	public String getSpriteSheetKey() {
		return spriteSheetKey;
	}

	public void setSpriteSheetKey(String spriteSheetKey) {
		this.spriteSheetKey = spriteSheetKey;
	}


}
