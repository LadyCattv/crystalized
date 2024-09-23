package gg.litestrike.game;

import org.bukkit.plugin.java.JavaPlugin;

public final class Litestrike extends JavaPlugin {

	// holds all the config about a map, like the spawn/border coordinates
	private MapData mapdata = new MapData();

  @Override
  public void onEnable() {

    getServer().getPluginManager().registerEvents(new PlayerListener(), this);

  }

  @Override
  public void onDisable() {
  }
}
