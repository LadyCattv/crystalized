package gg.windcore.treasure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static org.bukkit.entity.EntityType.PLAYER;

public class teams implements CommandExecutor {
    private List<Player> list = new ArrayList<Player>();
    private List<Player> team1 = new ArrayList<Player>();
    private List<Player> team2 = new ArrayList<Player>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;
            World w = Bukkit.getServer().getWorld(player.getLocation().getWorld().getName());
            w.spawnEntity(player.getLocation(), PLAYER);
            w.spawnEntity(player.getLocation(), PLAYER);
            w.spawnEntity(player.getLocation(), PLAYER);
            this.shuffle();
            player.sendMessage("Team 1: "+ team1 +"; Team 2: "+ team2);
        }
        return true;
    }

    private void shuffle(){
        list = (List<Player>)Bukkit.getOnlinePlayers();
        Collections.shuffle((List<Player>) list);
        int size = (int) Math.ceil(list.size()/2);
        Player[] listarr = list.toArray(new Player[] {});
        int i = 0;

        while(i < size/2+1){
            i++;
            Player mate = listarr[i];
            team1.add(mate);
        }

        while(i > size/2 && i <= size){
            i++;
            Player mate = listarr[i];
            team2.add(mate);
        }

    }



}
