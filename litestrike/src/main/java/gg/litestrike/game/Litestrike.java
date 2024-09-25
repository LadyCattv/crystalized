package gg.litestrike.game;

import org.bukkit.plugin.java.JavaPlugin;

public final class Litestrike extends JavaPlugin {

	// holds all the config about a map, like the spawn/border coordinates
	public MapData mapdata = new MapData();

  @Override
  public void onEnable() {

    this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

    this.getCommand("mapdata").setExecutor(mapdata);

		this.getCommand("spawnplayer").setExecutor(new Teams());
  }

  @Override
  public void onDisable() {
  }

  public static Litestrike getInstance() {
    return getPlugin(Litestrike.class);
  }
}
