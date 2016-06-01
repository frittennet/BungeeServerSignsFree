package me.Bentipa.BungeeSignsFree.sheduler;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import me.Bentipa.BungeeSignsFree.BungeeSign;
import me.Bentipa.BungeeSignsFree.ServerInfo;
import me.Bentipa.BungeeSignsFree.pinghelp.ServerPing;
import me.Bentipa.BungeeSignsFree.events.BSSBackSendEvent;
import me.Bentipa.BungeeSignsFree.events.BSSPingEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * @author Bentipa(Benjamin)
 * @year 2016
 **/
public class BungeeSignsPinger implements Runnable, Listener{

	
	private final me.Bentipa.BungeeSignsFree.Core plugin;
	
	public BungeeSignsPinger(me.Bentipa.BungeeSignsFree.Core bSignsMain)
	{
		this.plugin = bSignsMain;
		bSignsMain.getServer().getPluginManager().registerEvents(this, bSignsMain);
	}
	
	@Override
	public void run() {
		
		final List<ServerInfo> servers = plugin.servers;
		BSSPingEvent event = new BSSPingEvent(servers);
		Bukkit.getPluginManager().callEvent(event);
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, (plugin.getConfig().getInt("sign-refresh")/1000)*20);
	}
	
	
	@EventHandler
	public void onEvent(BSSPingEvent e)
	{
		if(!e.isCancelled())
		{
			for(ServerInfo server : e.getServers())
			{
//				System.out.println("Checking Server " + server.getName());
				if(server.isLocal()){
//					System.out.println("Local!");
					final String status = server.getMotd();
					ServerListPingEvent ping = new ServerListPingEvent(new InetSocketAddress(Bukkit.getIp(), Bukkit.getPort()).getAddress(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
					Bukkit.getPluginManager().callEvent(ping);
					server.setMotd(ping.getMotd());
					server.setPlayerCount(ping.getNumPlayers());
					server.setMaxPlayers(ping.getMaxPlayers());
					server.setPingStart(System.currentTimeMillis());
					server.setPingEnd(System.currentTimeMillis());
                                           
			        
			        
				}else{
//					System.out.println("Ext.!");
					pingAsync(server);
				}
					
			}
		}
	}
	
	@EventHandler
	public void onEvent(BSSBackSendEvent e)
	{
		if(!e.isCancelled())
		{
			ServerInfo si = e.getServerInfo();
			for(BungeeSign bs : getSigns(si.getName())){			
				bs.setServerInfo(si);			
			}
		}
	}
	
	private ArrayList<BungeeSign> getSigns(String servername){
		ArrayList<BungeeSign> signs = new ArrayList<BungeeSign>();
		for(BungeeSign bs: plugin.getSigns()){
			if(bs.getServer().equals(servername)){
				signs.add(bs);
			}
		}
		return signs;
	}

	private void pingAsync(final ServerInfo server)
	{
		final ServerPing ping = server.getPing();
//		System.out.println("Pinging!");
		if(!ping.isFetching())
		{
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
			{
				@Override
				public void run() 
				{
					long pingStartTime = System.currentTimeMillis();
					ping.setAddress(server.getAddress());
					ping.setTimeout(server.getTimeout());
					ping.setFetching(true);
					
//					System.out.println("Starting!");
					
					try 
					{
						final String status = server.getMotd();
                                                ServerPing.SResponse response = ping.fetchData();				
						server.setProtocol(response.getProtocol());
						server.setMotd(response.getDescription());
						server.setPlayerCount(response.getPlayers());
						server.setMaxPlayers(response.getSlots());
						server.setPingStart(pingStartTime);
						
//						System.out.println("Fetched Data!");
//						System.out.println("Motd: " + response.getDescription());
//						System.out.println("PlayerCount: " + response.getPlayers());
//						System.out.println("PlayersMax: " + response.getSlots());
//						
						BSSBackSendEvent backsend = new BSSBackSendEvent(server, ping);
						plugin.callSyncEvent(backsend);
                                                server.setFailedConnections(0);  
                                                                                               
					} 
					catch(Exception e)
					{
						server.setFailedConnections(server.getFailedConnections()+1);
//                                                System.out.println("Ping failed!");
//                                                e.printStackTrace();
					}
					finally
					{
//					System.out.println("Finished!");
						ping.setFetching(false);
						server.setPingEnd(System.currentTimeMillis());
					}
				}
			});
		}
	}
	
}
