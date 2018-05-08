package gameplay;

import java.util.ArrayList;

import clientside.ControlType;
import gameplay.attacks.Attack;
import gameplay.avatars.Avatar;
import gameplay.avatars.Brute;
import gameplay.avatars.Avatar.AttackType;
import networking.frontend.NetworkDataObject;
import networking.frontend.NetworkListener;
import networking.frontend.NetworkMessenger;

/**
 * 
 * Runs the game mechanics from the server side, Listens to the network from the
 * server
 * 
 * @author shaylandias
 *
 */
public class GameManager implements NetworkListener {

	private boolean runningGame;
	private GameState state;
	private ArrayList<NetworkDataObject> commands;
	private ArrayList<NetworkDataObject> connections;
	private NetworkMessenger nm;

	public GameManager() {
		state = new GameState();
		runningGame = true;
		commands = new ArrayList<NetworkDataObject>();
		connections = new ArrayList<NetworkDataObject>();
	}

	/**
	 * 
	 * Returns the state of the game to be drawn
	 * 
	 * @return The Game State
	 */
	public GameState getState() {
		return state;
	}

	// public void addPlayer() {
	// state.addAvatar(new Brute()); // This is a placeholder for testing
	// }

	public void addCommand(NetworkDataObject ndo) {
		synchronized (commands) {
			commands.add(ndo);
		}
	}

	/**
	 * Runs one step of the game mechanics
	 */
	public void run() {

		synchronized (commands) {
			for (NetworkDataObject ndo : commands) {
				if (ndo.message[0] instanceof ControlType) {
					ControlType action = (ControlType) ndo.message[0];
					Avatar avatar = null;
					String playerNum = ndo.getSourceIP();
					for (Avatar x : state.getAvatars()) {
						if (x.getPlayer().equals(playerNum)) {
							avatar = x;
							break;
						}
					}
					if (avatar != null) {
						if (action == ControlType.MOVEMENT) {

							char dir = (char) ndo.message[2];
							boolean dir1 = (boolean) ndo.message[3];
							if (dir == 'w')
								avatar.setUp(dir1);
							else if (dir == 'a')
								avatar.setLeft(dir1);
							else if (dir == 's')
								avatar.setDown(dir1);
							else if (dir == 'd')
								avatar.setRight(dir1);

						} else if (action == ControlType.ATTACK) {
							if (ndo.message[2] == AttackType.BASIC) {
								state.addAttack(avatar.basicAttack(playerNum, (double) ndo.message[3]));
							}

						}
						else if(action == ControlType.DASH) {
							avatar.dash((double)ndo.message[2]);
							
						}

					}
				}
			}
			commands.clear();
		}

		synchronized (state) {
			for (int i = 0; i < state.getAttacks().size(); i++) {
				Attack p = state.getAttacks().get(i);
				if (!p.act(state.getAvatars())) {
					state.getAttacks().remove(i);
					i--;
				}

			}
			for (Avatar x : state.getAvatars()) {
				x.act();
			}

		}

	}

	public ArrayList<NetworkDataObject> getConnections() {
		return connections;
	}

	@Override
	public void connectedToServer(NetworkMessenger nm) {
		this.nm = nm;
	}

	/**
	 * Adds a NetworkDataObject to the list of commands to be run if it contains a
	 * ControlType
	 */
	@Override
	public void networkMessageReceived(NetworkDataObject ndo) {

		if (ndo.message.length > 0) {
			if (ndo.message[0] instanceof ControlType) {
				addCommand(ndo);
			}
		}
		if (ndo.message[0] instanceof String) {
			if (((String) ndo.message[0]).equals("INTIALIZATION")) {
				state.addAvatar((Avatar) ndo.message[1]);
			}
		}
	}

}
