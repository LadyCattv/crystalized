package gg.litestrike.game;

import org.bukkit.scheduler.BukkitRunnable;

enum RoundState {
	PreRound,
	Running,
	PostRound,
	GameFinished,
}

// This will be created by something else, whenever there are 6+ people online and no game is currently going
public class GameController {
	// TODO initialize this from ladycats teams
	private Teams teams;
	// private PlayerData[] player_datas;

	private int current_round_number = 1;
	private RoundState round_state = RoundState.PreRound;

	// the round_timer starts counting up from the beginning of the round
	// after it reaches 15, the game is started. when the round winner is
	// determined its reset to 0 and counts until 5 for the postround time.
	// then the next round starts and it counts from 0 again
	private int round_timer = 0;

	// after this round, the sides get switched
	private int switch_round = 4;

	public GameController() {

		// This just calls update_game_state() once every second
		new BukkitRunnable() {
			@Override
			public void run() {
				update_game_state();
			}
		}.runTaskTimer(Litestrike.getInstance(), 20, 20);

	}
	
	// This is run every second
	private void update_game_state() {
		round_timer += 1;

		// this is like a state-machine, it will check the current state, check a condition, and
		// if the condition is met, call a method to advance to the next state
		switch (round_state) {
			case RoundState.PreRound: {
				if (round_timer == 15) {
					start_round();
				}
			}
			case RoundState.Running: {
				if (
					// TODO check_bomb_exploded ||
					// TODO all enemies dead ||
					round_timer == 135   // 2 minutes round time + 15 sec preround
				) {
					finish_round();
				}
			}
			case RoundState.PostRound: {
				if (round_timer == 5) {
					if (current_round_number == switch_round * 2) {
						finish_game();
					} else {
						next_round();
					}
				}
			}
			case RoundState.GameFinished: {
				if (round_timer == 10) {
					// TODO kick everyone probably
					Litestrike.getInstance().game_controller = new GameController(); // check if this works??
				}
			}
		}
	}

	// this is called when we switch from PreRound to Running
	private void start_round() {
		round_state = RoundState.Running;
		// TODO
	}

	// this is called when we switch from Running to PostRound
	private void finish_round() {
		round_state = RoundState.PostRound;
		round_timer = 0;
		// TODO
	}

	// this is called when the last round is over and the podium should begin
	private void finish_game() {
		round_timer = 0;
		// TODO
	};

	// this is called when the next round should start
	private void next_round() {
		round_timer = 0;
		current_round_number += 1;
		// TODO

	}

}
