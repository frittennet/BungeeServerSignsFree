package me.Bentipa.BungeeSignsFree.events;

import java.util.List;

import me.Bentipa.BungeeSignsFree.ServerInfo;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bentipa(Benjamin) | codebucketdev
 * @year 2016
 **/
public class BSSPingEvent extends  Event implements Cancellable{

	
	private static final HandlerList handlers = new HandlerList();
	private List<ServerInfo> servers;
	private boolean cancelled;
	
	public BSSPingEvent(List<ServerInfo> servers)
	{
		this.servers = servers;
	}
	
	public static HandlerList getHandlerList() 
	{
        return handlers;
    }
	

	public List<ServerInfo> getServers()
	{
		return servers;
	}
	
	@Override
	public boolean isCancelled() {		
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
