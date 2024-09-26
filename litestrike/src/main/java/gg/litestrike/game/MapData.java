package gg.litestrike.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

// this will read a file, map_config.json, in the current world directory,
// from this file it will read, for example: the spawn points and map_name.
// this is a singleton
public class MapData implements CommandExecutor {
	public double[] placer_spawn;
	public double[] breaker_spawn;
	public double[] que_spawn;

	public String map_name;

	// toggelable map-specific features
	public Boolean jump_pads;
	public Boolean openable_doors;

	// border gets placed 1 block above this block type
	public Material border_specifier;

	// This handles the /mapdata command
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
      @NotNull String[] args) {
    Player cmd_sender = (Player) commandSender;

		cmd_sender.sendMessage(this.toString());


		return true;
	}

	public MapData() {
		try {
			String file_content = Files.readString(Paths.get("./world/map_config.json"));
			JsonObject json = JsonParser.parseString(file_content).getAsJsonObject();


			// TODO add pitch and yaw to the config file
			JsonArray p_spawn = json.get("placer_spawn").getAsJsonArray();
			this.placer_spawn = new double[]{p_spawn.get(0).getAsDouble(), p_spawn.get(1).getAsDouble(), p_spawn.get(2).getAsDouble()};

			JsonArray b_spawn = json.get("breaker_spawn").getAsJsonArray();
			this.breaker_spawn = new double[]{b_spawn.get(0).getAsDouble(), b_spawn.get(1).getAsDouble(), b_spawn.get(2).getAsDouble()};

			JsonArray q_spawn = json.get("que_spawn").getAsJsonArray();
			this.que_spawn =  new double[]{q_spawn.get(0).getAsDouble(), q_spawn.get(1).getAsDouble(), q_spawn.get(2).getAsDouble()};

			this.map_name = json.get("map_name").getAsString();

			String b_spec = json.get("border_specifier").getAsString();
			this.border_specifier = Material.matchMaterial(b_spec);

			this.jump_pads = json.get("enable_jump_pads") != null;
			this.openable_doors = json.get("enable_openable_doors") != null;

		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not load the maps configuration file!\n" + e);
			Bukkit.getLogger().log(Level.SEVERE, "The Plugin will be disabled!");
			// disable plugin when failure
			Bukkit.getPluginManager().disablePlugin(Litestrike.getInstance());
		}

	}

	public String toString() {
		return "placer_spawn: " + this.placer_spawn +
		"\nbreaker_spawn: " + this.breaker_spawn +
		"\nque_spawn: " + this.que_spawn +
		"\nmap_name: " + this.map_name +
		"\nborder_specifier: " + this.border_specifier +
		"\nenable_jump_pads: " + this.jump_pads +
		"\nenable_openable_doors: " + this.openable_doors;
	}
}
