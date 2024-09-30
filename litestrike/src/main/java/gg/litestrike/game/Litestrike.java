package gg.litestrike.game;

import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

// TODO add a sanity checker class

public final class Litestrike extends JavaPlugin {

	public static final Component PLACER_TEXT = Component.text("Placer").color(TextColor.color(0xe31724)).decoration(TextDecoration.BOLD, true);
	public static final Component BREAKER_TEXT = Component.text("Breaker").color(TextColor.color(0x0f9415)).decoration(TextDecoration.BOLD, true);

	// holds all the config about a map, like the spawn/border coordinates
	public final MapData mapdata = new MapData();

	public GameController game_controller;

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getPluginManager().registerEvents(new MapData(), this);
		this.getCommand("mapdata").setExecutor(mapdata);

		// TODO bukkit runnable taht creates game controller
	}

	@Override
	public void onDisable() {
	}

	public static Litestrike getInstance() {
		return getPlugin(Litestrike.class);
	}
}
