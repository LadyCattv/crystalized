package gg.litestrike.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

// this will read a file, map_config.json, in the current world directory,
// from this file it will read, for example: the spawn points and map_name.
// this is a singleton
public class MapData implements TabExecutor {
	public Location placer_spawn;
	public Location breaker_spawn;
	public Location que_spawn;

	public String map_name;

	// toggelable map-specific features
	public Boolean jump_pads;
	public Boolean openable_doors;

	// border gets placed 1 block above this block type
	public Material border_specifier;

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
      @NotNull String[] args) {
    Player cmd_sender = (Player) commandSender;

		cmd_sender.sendMessage(this.toString());

		return true;
	}

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

		return null;
	}

	public MapData() {

		try {
			String file_content = Files.readString(Paths.get("./world/map_config.json"));
			JsonObject json = JsonParser.parseString(file_content).getAsJsonObject();

			// TODO FIXME idk how to get the correct world
			World world = Bukkit.getServer().getWorld("world");


			JsonArray p_spawn = json.get("placer_spawn").getAsJsonArray();
			this.placer_spawn = new Location(world, p_spawn.get(0).getAsInt(), p_spawn.get(1).getAsInt(), p_spawn.get(2).getAsInt());

			JsonArray b_spawn = json.get("breaker_spawn").getAsJsonArray();
			this.breaker_spawn = new Location(world, b_spawn.get(0).getAsInt(), b_spawn.get(1).getAsInt(), b_spawn.get(2).getAsInt());

			JsonArray q_spawn = json.get("que_spawn").getAsJsonArray();
			this.que_spawn = new Location(world, q_spawn.get(0).getAsInt(), q_spawn.get(1).getAsInt(), q_spawn.get(2).getAsInt());

			// FIXME border specifier is null???
			this.map_name = json.get("map_name").getAsString();
			this.border_specifier = Material.getMaterial(json.get("border_specifier").getAsString());

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
