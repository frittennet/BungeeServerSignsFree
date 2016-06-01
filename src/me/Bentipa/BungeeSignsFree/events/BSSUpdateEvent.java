package me.Bentipa.BungeeSignsFree.events;

import java.util.List;

import me.Bentipa.BungeeSignsFree.BungeeSign;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bentipa(Benjamin) | codebucketdev
 * @year 2016
 **/
public class BSSUpdateEvent  extends Event implements Cancellable{

		private static final HandlerList handlers = new HandlerList();
		private List<BungeeSign> signs;
		private boolean cancelled;
		
		public BSSUpdateEvent(List<BungeeSign> signs)
		{
			this.signs = signs;
		}
		
		public HandlerList getHandlers() 
		{
	        return handlers;
	    }
		
		public static HandlerList getHandlerList() 
		{
	        return handlers;
	    }
		
		public List<BungeeSign> getSigns()
		{
			return signs;
		}
		
		public void setCancelled(boolean cancel)
		{
			this.cancelled = cancel;
		}

		public boolean isCancelled() 
		{
			return cancelled;
		}
}
