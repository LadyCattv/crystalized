package gg.litestrike.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

// this will read a file, map_config.json, in the current world directory,
// from this file it will read, for example: the spawn points and map_name.
// this is a singleton
public class MapData {
	public Location placer_spawn;
	public Location breaker_spawn;
	public Location que_spawn;

	public String map_name;

	// toggelable map-specific features
	public Boolean jump_pads;
	public Boolean openable_doors;

	// border gets placed 1 block above this block type
	public BlockType border_specifier;

	public MapData() {

		try {
			String file_content = Files.readString(Paths.get("./world/map_config.json"));
			JSONObject json = new JSONObject(file_content);

		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "could not read the maps configuration file! no spawnpoints for the teams will be set!");
		}
	}

}
