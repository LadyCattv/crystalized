package gg.litestrike.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

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
	private List<PlayerData> playerDatas = new ArrayList();

	private int current_round_number = 0;
	private RoundState round_state = RoundState.PreRound;

	// the phase_timer starts counting up from the beginning of the round
	// after it reaches 15, the game is started. when the round winner is
	// determined its reset to 0 and counts until 5 for the postround time.
	// then the next round starts and it counts from 0 again
	private int phase_timer = 0;

	// after this round, the sides get switched
	private int switch_round = 4;

	// TODO private Bomb bomb = new Bomb();

	public GameController() {
		next_round();
		for(Player player : Bukkit.getOnlinePlayers()){
			PlayerData p = new PlayerData(player);
			playerDatas.add(p);
		}
		// This just calls update_game_state() once every second
		new BukkitRunnable() {
			@Override
			public void run() {
				Boolean game_over = update_game_state();
				if (game_over) {
					cancel();
				}
			}
		}.runTaskTimer(Litestrike.getInstance(), 20, 20);

	}
	
	// This is run every second
	// TODO
	private Boolean update_game_state() {
		phase_timer += 1;

		// this is like a state-machine, it will check the current state, check a condition, and
		// if the condition is met, call a method to advance to the next state
		switch (round_state) {
			case RoundState.PreRound: {
				if (phase_timer == 15) {
					start_round();
				}
			}
			break;
			case RoundState.Running: {
				if (
					// TODO check_bomb_exploded ||
					// TODO check bomb_mined ||
					// TODO all enemies dead ||
					phase_timer == 120
				) {
					finish_round();
				}
			}
			break;
			case RoundState.PostRound: {
				if (phase_timer == 5) {
					if (current_round_number == switch_round * 2) {
						start_podium();
					} else {
						next_round();
					}
				}
			}
			break;
			case RoundState.GameFinished: {
				if (phase_timer == 20) {
					finish_game();
					return true; // remove the update_game_state task
				}
			}
			break;
		}
		return false;
	}

	// this is called when we switch from PreRound to Running
	private void start_round() {
		round_state = RoundState.Running;
		phase_timer = 0;

		// play a sound and send messages to the teams
		Bukkit.getServer().playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 1));
		teams.placers_audience().sendMessage(Component.text("You are a ")
			.append(Litestrike.PLACER_TEXT)
			.append(Component.text("\nGo with your team and place the bomb at one of the designated bomb sites!!\n Or kill the enemy Team!")));
		teams.breakers_audience().sendMessage(Component.text("You are a ")
			.append(Litestrike.BREAKER_TEXT)
			.append(Component.text("\nKill the Enemy team and prevent them from placing the bomb!\n If they place the bomb, break it.")));

		// TODO remove borders
		// TODO remove shop item
	}

	// this is called when we switch from Running to PostRound
	private void finish_round() {
		round_state = RoundState.PostRound;
		phase_timer = 0;
		// TODO
	}

	// this is called when the last round is over and the podium should begin
	private void start_podium() {
		round_state = RoundState.GameFinished;
		phase_timer = 0;
		// TODO
	};

	// this is called when the next round should start and when the first round starts
	private void next_round() {
		round_state = RoundState.PreRound;
		phase_timer = 0;
		current_round_number += 1;
		// TODO
		// TODO give armor and weapons
		// TODO give shop item

	}

	// is called when the game will be finished after the podium
	private void finish_game() {
		// TODO kick everyone probably
		Litestrike.getInstance().game_controller = null;
	}

}
