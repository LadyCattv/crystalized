package gg.litestrike.game;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class ShopListener implements Listener {

	@EventHandler
	public void openShop(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Shop s = Shop.getShop(p);
		if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
			if (p.getInventory().getItemInMainHand().getType() == Material.EMERALD) {
				p.openInventory(s.currentView);
				Shop.setItems(p, s.shopItems);
				Shop.setDefuser(p);
			}
		}
	}

	@EventHandler
	public void buyItem(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		Shop s = Shop.getShop(p);
		if (event.getCurrentItem() == null) {
			return;
		}
		if (event.getInventory() != s.currentView) {
			return;
		}
		event.setCancelled(true);

		if(event.isRightClick()){
			undoBuy(event.getCurrentItem(), (Player) event.getWhoClicked(), event.getSlot());
			return;
		}

		PlayerData pd = Litestrike.getInstance().game_controller.getPlayerData(p);

		for (LSItem lsitem : s.shopItems) {

			if(lsitem.item.getType() != Material.IRON_PICKAXE) {
				if (lsitem.slot == null || lsitem.slot != event.getSlot()) {
					continue;
				}
			}

			// if the item is not ammuntion and also not a consumable and we already have
			// it, then we cant buy it
			if (lsitem.categ != LSItem.ItemCategory.Ammunition && lsitem.categ != LSItem.ItemCategory.Consumable
					&& Shop.alreadyHasThis(p, lsitem.item)) {
				p.sendMessage(Component.text("You already have this item").color(RED));
				p.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1, 1));
				return;
			}
			// check that we have enough money
			if (!pd.removeMoney(lsitem.price)) {
				p.sendMessage(Component.text("Cant afford this item").color(RED));
				p.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1, 1));
				return;
			}

			if (lsitem.categ == LSItem.ItemCategory.Armor) {
				p.getInventory().setChestplate(lsitem.item);
			} else if ((Shop.findInvIndex(p, lsitem) == -1 || lsitem.categ == LSItem.ItemCategory.Consumable || lsitem.categ == LSItem.ItemCategory.Ammunition) && lsitem.item.getType() == Material.IRON_PICKAXE) {
				p.getInventory().addItem(lsitem.item);
			} else {
				p.getInventory().setItem(Shop.findInvIndex(p, lsitem), lsitem.item);
			}
			p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 5));
			s.updateTitle(p, lsitem);
			s.buyHistory.add(lsitem);
			return;
		}
	}

	public void undoBuy(ItemStack item, Player p, int slot) {

		Shop s = Shop.getShop(p);

		for(LSItem lsitem : s.shopItems) {
			//find corresponding LSItem to the item clicked by slot
			if (lsitem.item.getType() != Material.IRON_PICKAXE && lsitem.slot != null){
				if (lsitem.slot != slot) {
					continue;
				}
			}

			//go through the players inv and find the item we want to sell
			for(int i = 0; i <= 40; i++){
				ItemStack ite = p.getInventory().getItem(i);

				Component lsitemcom = lsitem.item.displayName();
				Component itecom = ite.displayName();
				PlainTextComponentSerializer ptcs = PlainTextComponentSerializer.plainText();
				String lsitemName = ptcs.serialize(lsitemcom);
				String iteName = ptcs.serialize(lsitemcom);

				if(lsitemName.equals(iteName)){
					//check what category the item is in order to sell properly
					if(lsitem.categ == LSItem.ItemCategory.Melee || lsitem.categ == LSItem.ItemCategory.Range || lsitem.categ == LSItem.ItemCategory.Defuser){
						//go through the buyHistory and find and LSItem that has the same category but isn't the same item
						for(int j = s.buyHistory.size(); j > 1; j--){
							LSItem hisitem = s.buyHistory.get(j);
							if(hisitem.categ == lsitem.categ && hisitem.item != lsitem.item){
								p.getInventory().setItem(i, hisitem.item);
								PlayerData.addMoney(lsitem.price, p);
								s.updateTitle(p, lsitem);
								p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 3));
								return;
							}
						}
						//if we don't find any buys in the history we give the player the basic kit
						if(lsitem.categ == LSItem.ItemCategory.Melee){
							p.getInventory().setItem(i, new ItemStack(Material.STONE_SWORD));
							PlayerData.addMoney(lsitem.price, p);
							s.updateTitle(p, lsitem);
							p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 3));
							return;
						}else if(lsitem.categ == LSItem.ItemCategory.Range){
							p.getInventory().setItem(i, new ItemStack(Material.BOW));
							PlayerData.addMoney(lsitem.price, p);
							s.updateTitle(p, lsitem);
							p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 3));
							return;
						}else{
							p.getInventory().setItem(i, new ItemStack(Material.STONE_PICKAXE));
							PlayerData.addMoney(lsitem.price, p);
							s.updateTitle(p, lsitem);
							p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 3));
							return;
						}
					}else if(lsitem.categ == LSItem.ItemCategory.Armor){
						for(int j = s.buyHistory.size(); j > 1; j--){
							LSItem hisitem = s.buyHistory.get(j);
							if(hisitem.categ == lsitem.categ && hisitem.item != lsitem.item){
								p.getInventory().setChestplate(hisitem.item);
								PlayerData.addMoney(lsitem.price, p);
								s.updateTitle(p, lsitem);
								p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 3));
								return;
							}
						}
						if(Teams.get_team(p.getName()) == Team.Placer){
							p.getInventory().setChestplate(Shop.colorArmor(Color.fromRGB(0xe31724), new ItemStack(Material.LEATHER_CHESTPLATE)));
							PlayerData.addMoney(lsitem.price, p);
							s.updateTitle(p, lsitem);
							p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 3));
							return;
						}else{
							p.getInventory().setChestplate(Shop.colorArmor(Color.fromRGB(0x0f9415), new ItemStack(Material.LEATHER_CHESTPLATE)));
							PlayerData.addMoney(lsitem.price, p);
							s.updateTitle(p, lsitem);
							p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 3));
							return;
						}
					}else if(lsitem.categ == LSItem.ItemCategory.Consumable || lsitem.categ == LSItem.ItemCategory.Ammunition){
						if(lsitem.slot == 50 && ite.getAmount() == 6){
							return;
						}
						for(int j = s.buyHistory.size(); j > 1; j--){
							LSItem hisitem = s.buyHistory.get(j);
							if(hisitem.categ == lsitem.categ && hisitem.item != lsitem.item){
								int amt = lsitem.item.getAmount();
								int amount = ite.getAmount();
								p.getInventory().setItem(i, new ItemStack(lsitem.item.getType(), amount-amt));
								PlayerData.addMoney(lsitem.price, p);
								s.updateTitle(p, lsitem);
								return;
							}
						}
					}
				}
			}
		}
	}
}
