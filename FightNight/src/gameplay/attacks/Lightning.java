package gameplay.attacks;

import java.util.ArrayList;

import gameplay.attacks.StatusEffect.Effect;
import gameplay.avatars.Avatar;
import processing.core.PApplet;

public class Lightning extends Attack{
	
	private double delay;
	private Lightning attached;
	public static final int w = 80, h = 80, damage = 40;
	private boolean dead;
	
	public Lightning(String imageKey, int x, int y, String playerAddress, double dir, double delay, Lightning attached) {
		super(imageKey, x, y, w, h, playerAddress, damage, true, new StatusEffect(Effect.STUNNED, 1.5, 1), dir);
		super.setActive(false);
	
//		if(dir > 45 && dir < 135 || dir > 225 && dir < 315) {
//			width = h;
//			height = w;
//			flipDraw = true;
//		}
		this.delay = delay;
		this.attached = attached;
		duration = 2;
		dead = false;
	}

	
	public boolean act(ArrayList<Avatar> avatars) {
		
		if(System.currentTimeMillis() > super.getStartTime() + delay * 1000)
			super.setActive(true);
		
		if (checkEnd()) {
			return false;
		}

		for (Avatar a : avatars) {
			if (a.getHitbox().intersects(this)) {
				AttackResult res = a.takeHit(this);
				if (res.equals(AttackResult.BLOCKED) || res.equals(AttackResult.SUCCESS)) {
					end();
				}
			}
		}

		return true;
	}
	
	protected boolean checkEnd() {
		if (System.currentTimeMillis() > super.getStartTime() + duration * 1000) {
			end();
			return true;
		} else if(attached != null && attached.isDead()) {
			end();
			return true;
		} else
			return false;
	}
	
	public void end() {
		if(attached != null)
			attached.setActive(false);
		super.end();
	}
	
	public void draw(PApplet surface) {
		if(System.currentTimeMillis() > super.getStartTime() + delay * 1000)
			super.draw(surface);
	}
	
	public boolean isDead() {
		return dead;
	}
	
}