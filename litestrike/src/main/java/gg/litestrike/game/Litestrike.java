package gg.litestrike.game;

import org.bukkit.plugin.java.JavaPlugin;

// TODO add a sanity checker class

public final class Litestrike extends JavaPlugin {

	// holds all the config about a map, like the spawn/border coordinates
	public MapData mapdata = new MapData();

	public GameController game_controller;

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getCommand("mapdata").setExecutor(mapdata);
		this.getCommand("spawnplayer").setExecutor(new Teams());

		// bukkit runnable taht creates game controller
	}

	@Override
	public void onDisable() {
	}

	public static Litestrike getInstance() {
		return getPlugin(Litestrike.class);
	}
}
