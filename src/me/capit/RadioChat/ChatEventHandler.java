package me.capit.RadioChat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatEventHandler implements Listener {
	RadioChat plugin;
	
	public ChatEventHandler(RadioChat plugin){
		this.plugin = plugin;
	}
	
	public enum ObfChatLevel{
		NONE, LIGHT, HEAVY
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e){
		e.setJoinMessage(null);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerChatted(AsyncPlayerChatEvent e){
		Player plyr = e.getPlayer();
		plugin.getLogger().info(plyr.getName()+": "+e.getMessage());
		Player[] allPlayers = plugin.getServer().getOnlinePlayers();
		//plugin.getLogger().info("Trying to send to "+allPlayers.length+" players...");
		String tag = "["+plyr.getName()+"]: ";
		String msg = e.getMessage();
		//plugin.getLogger().info("Composed "+tag+msg);
		for (int i=0; i<allPlayers.length; i++){
			Player p = allPlayers[i];
			if (!p.getName().equalsIgnoreCase(plyr.getName())){
				//plugin.getLogger().info("Sent to "+p.getName());
				Location pLoc = plyr.getLocation();
				Location loc = p.getLocation();
				double diff = Math.abs(pLoc.getX()-loc.getX()) + Math.abs(pLoc.getZ()-loc.getZ());
				//plugin.getLogger().info("Diff: "+diff);
				if (diff<=plugin.getConfig().getInt("config.LIGHT_RANGE")){
					p.sendMessage(tag+msg);
				} else if (diff<=plugin.getConfig().getInt("config.HEAVY_RANGE")){
					p.sendMessage(obfusicateString(tag, ObfChatLevel.LIGHT)+obfusicateString(msg, ObfChatLevel.LIGHT));
				} else if (diff<=plugin.getConfig().getInt("config.SILENT_RANGE")){
					p.sendMessage(obfusicateString(tag, ObfChatLevel.HEAVY)+obfusicateString(msg, ObfChatLevel.HEAVY));
				}
			} else {
				//plugin.getLogger().info("Tried to send to self. No obfusication.");
				p.sendMessage(tag+msg);
			}
		}
		e.setCancelled(true);
	}
	
	public String obfusicateString(String string, ObfChatLevel level){
		int charRand = 0;
		//plugin.getLogger().info("Called "+level.toString()+".");
		switch (level){
			case LIGHT: charRand=20; break;
			case HEAVY: charRand=40; break;
			case NONE: charRand=0; break;
		}
		String newstr = "";
		for (int i=0; i<string.length(); i++){
			double randBreak = Math.ceil(Math.random()*100);
			// plugin.getLogger().info(randBreak+"<=?"+charRand);
			if (randBreak<=charRand){
				newstr += ChatColor.MAGIC+"*"+ChatColor.RESET;
			} else {
				newstr += string.charAt(i);
			}
		}
		return newstr;
	}
	
}
