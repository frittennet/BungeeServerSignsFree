
package me.Bentipa.BungeeSigns;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.Bentipa.updater.SpigotPluginUpdater;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BSignsMain extends JavaPlugin implements PluginMessageListener {

	 public static boolean checkEvents;
	 
	 protected static ArrayList<Player> inCreation = new ArrayList<Player>();
	 protected static ArrayList<Player> inRemove = new ArrayList<Player>();
	 protected static HashMap<Player, Step> creationStep = new HashMap<Player, Step>(); 
	 protected static HashMap<Player, BungeeSign> creations = new HashMap<Player, BungeeSign>();
	 
	 private static ArrayList<BungeeSign> signs = new ArrayList<BungeeSign>();
	 private SpigotPluginUpdater spu;
	 private BungeeSignsRefresher bsr;
	 public boolean sign_alive_check = true;
	 public void onEnable(){
	          
	        loadConfig();
	        sign_alive_check = getConfig().getBoolean("server-alive");
	        this.getLogger().info("[Info] Config loaded ");
	        getServer().getPluginManager().registerEvents(new BSignsListener(this), this);
	        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		    spu = new SpigotPluginUpdater(this, "http://bentipa.bplaced.net/stealth-coders-plugins/bungeesigns.html");
		    if(spu.needsUpdate()){
		    this.getServer().broadcastMessage("To update the plugin, type /bsupdate");	
		    }
		    
		    loadSigns();
		    this.getLogger().info("[Info] Signs loaded!");
		    try{
		    bsr = new BungeeSignsRefresher(this);
		    bsr.start();
		    }catch(Exception e){
		    	
		    }
		    this.getLogger().info("[Info] Refresh-Task started!");
		    this.getLogger().info("[Info] Plugin enabled ");   
	 	}
	 
	    public void onDisable(){
	    	bsr.stop();
	        this.getLogger().info("[Info] Refresh-Task stoped!");
	        this.getLogger().info("[Info] Plugin disabled!");
	    }
	
		public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
	    	Player player = (Player) sender;
	    	String message ="";
	
	        if(cmd.getName().equalsIgnoreCase("bsinfo")){              
	        		if(player.hasPermission("BungeeSigns.info")){
	        			player.sendMessage(MSG_PREFIX() + ChatColor.GREEN + "[Info] Created at 29.04.2015 by Bentipa");
	        		
	        			return true;
	        		}else{
	        			player.sendMessage(ChatColor.RED +"You don't have the permission to use this command!");
	        			return false;
	        		}
	        }
	        if(cmd.getName().equalsIgnoreCase("bsupdate")){              
        		if(player.hasPermission("BungeeSigns.update")){
        			spu.update();
        			player.sendMessage(MSG_PREFIX() + ChatColor.GREEN + "Plugin updated! To make changes work, you have to reload your server!");
        			
        			return true;
        		}else{
        			player.sendMessage(ChatColor.RED +"You don't have the permission to use this command!");
        			return false;
        		}
        }
	        if(cmd.getName().equalsIgnoreCase("createBSSign")){              
        		if(player.hasPermission("BungeeSigns.create")){
        			player.sendMessage(MSG_PREFIX() + SS(Step.SELECT) + ChatColor.GREEN + "Click on the Sign you want to be a Bungee-Sign.");
        			inCreation.add(player);
        			creationStep.put(player, Step.SELECT);
        			return true;
        		}else{
        			player.sendMessage(ChatColor.RED +"You don't have the permission to use this command!");
        			return false;
        		}
	        }
	        if(cmd.getName().equalsIgnoreCase("removeBSSign")){              
        		if(player.hasPermission("BungeeSigns.remove")){
        			player.sendMessage(MSG_PREFIX() + ChatColor.GREEN + "Click on the Sign you want to not be a Bungee-Sign.");
        			inRemove.add(player);
        			return true;
        		}else{
        			player.sendMessage(ChatColor.RED +"You don't have the permission to use this command!");
        			return false;
        		}
	        }
	        if(cmd.getName().equalsIgnoreCase("reload")){              
        		if(player.hasPermission("BungeeSigns.reload")){
        			try {
						getConfig().load(new File("plugins/BungeeSignsPremium/config.yml"));
					} catch (IOException | InvalidConfigurationException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						getLogger().severe("Couldnt find config.yml!");
					}
        			player.sendMessage(MSG_PREFIX() + ChatColor.GREEN + "Reloaded the config.");        			
        			return true;
        		}else{
        			player.sendMessage(ChatColor.RED +"You don't have the permission to use this command!");
        			return false;
        		}
	        }
	        
	        
	        return false;
	    }
		
		protected String MSG_PREFIX(){
			return ChatColor.GRAY + "[" + ChatColor.BLUE + "Bungee-Signs" + ChatColor.GRAY + "] ";
		}
		
		protected enum Step{
			SELECT,
			SERVER_NAME,
			SIGN_CONTENT,
			END;
		}
		
		protected String SS(Step s){
			switch(s){
			case SELECT:
				return ChatColor.GRAY + "[" + ChatColor.AQUA + "Step: " + ChatColor.RED+ "1" + ChatColor.GRAY + "] ";
			case SERVER_NAME:
				return ChatColor.GRAY + "[" + ChatColor.AQUA + "Step: " + ChatColor.YELLOW+ "2" + ChatColor.GRAY + "] ";		
			case SIGN_CONTENT:
				return ChatColor.GRAY + "[" + ChatColor.AQUA + "Step: " + ChatColor.BLUE+ "3" + ChatColor.GRAY + "] ";		
			
			}
			return "";
		}
		
		public boolean isSaved(Sign s){
			for(BungeeSign ffs : signs){
				if(ffs!=null)
				if(ffs.equals(s)){
					return true;
				}
			}
			return false;
		}
		
		
		public Sign getSign(BungeeSign ffs){
//			System.out.println(" ?= " + ffs);
			for(BungeeSign s : signs){
//				System.out.println(s + " ?= " + ffs);
				if(ffs.equals(s)){
					if(s.getLocation().getWorld().getBlockAt(s.getLocation()).getState() instanceof Sign)
					return (Sign) s.getLocation().getWorld().getBlockAt(s.getLocation()).getState();
				}
			}
			for(BungeeSign s : creations.values()){
//				System.out.println(s + " ?= " + ffs);
				if(ffs.equals(s)){
					if(s.getLocation().getWorld().getBlockAt(s.getLocation()).getState() instanceof Sign)
					return (Sign) s.getLocation().getWorld().getBlockAt(s.getLocation()).getState();
				}
			}
			return null;
		}
		
		public BungeeSign getBungeeSignsSign(Sign s){
			if(!isSaved(s))
				return null;
			
			for(BungeeSign ffs : signs){
				if(ffs.equals(s)){
					return ffs;
				}
			}
			
			return null;
		}
		
		public void saveSign(BungeeSign ffs){
			signs.add(ffs);
			saveSigns();
		}
		
		public ArrayList<BungeeSign> getSigns(){
			return this.signs;
		}
		
		public void removeSign(BungeeSign ffs){
			signs.remove(ffs);
			saveSigns();
		}

		private void saveSigns(){			
				ArrayList<String> tostring = new ArrayList<String>();
				for(BungeeSign ffs : signs){
					if(ffs!=null)
					tostring.add(ffs.toString());
				}
				getConfig().set("signs", tostring);		
				saveConfig();
		}
		
		private void loadSigns(){
			List<String> tostring = getConfig().getStringList("signs");
			for(String s: tostring){
				BungeeSign si = BungeeSign.outOfString(this, s);	
				getIP(si.getServer());
				signs.add(si);
			}
		}
		
		public void loadConfig(){
			getConfig().options().copyDefaults(true);
	    	saveConfig();
	    }

		  @Override
		  public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		    if (!channel.equals("BungeeCord")) {
		    //System.out.println("Message withouth BungeeCord!");
		      return;
		      
		    }
		    try{
		    ByteArrayDataInput in = ByteStreams.newDataInput(message);
		    String subchannel = in.readUTF();
//			System.out.println("Message in! subchannel: " + subchannel);
		    if (subchannel.equals("ServerIP")) {
			  	String serverName = in.readUTF();
			  	String ip = in.readUTF();
			  	short port = in.readShort();		  				  				  
			  	this.adrss.put(serverName, new InetSocketAddress(ip, port));
			  	
			  			ServerListPing17 slp = new ServerListPing17();
			  			slp.setAddress(new InetSocketAddress(ip, port));
			  			if(servercmd.get(serverName).equalsIgnoreCase("motd")){
			  				try {
			  					motds.put(serverName, slp.fetchData().getDescription());
			  				} catch (IOException e) {
			  					// TODO Auto-generated catch block
							
			  				}
			  			}else
			  				if(servercmd.get(serverName).equalsIgnoreCase("maxp")){
			  					
				  				try {				  				
				  					maxp.put(serverName, slp.fetchData().getPlayers().getMax()+"");
				  				} catch (IOException e) {
				  					// TODO Auto-generated catch block
								
				  				}
				  			}
		  					
		    }       
		   
            if (subchannel.equals("PlayerCount")) {
            	
                        String server = in.readUTF();
                        int playerCount = in.readInt();
//                        System.out.println("PlayerCount: " + server + " -> " + playerCount);
                        curp.put(server, playerCount);                          
            }
		    }catch(Exception e){
//		    	e.printStackTrace();
		    	getLogger().severe("Error in PluginMessageChannel, Reasons: Invalid Servername, Error in saving,.." );
		    }
            
		  }	 
		  
		
	 public HashMap<String, String> servercmd = new HashMap<String, String>();	  
		  
	 public HashMap<String, String> motds = new HashMap<String, String>();
	 public HashMap<String, String> maxp  = new HashMap<String, String>();
	 public HashMap<String, Integer> curp = new HashMap<String, Integer>();
	 public HashMap<String, InetSocketAddress> adrss = new HashMap<String, InetSocketAddress>();
	 
	 
	 public void getMotd(String server){
		 servercmd.put(server, "motd");
		  final ByteArrayOutputStream b = new ByteArrayOutputStream();
          final DataOutputStream out = new DataOutputStream(b);            		    
		    try {
		    	out.writeUTF("ServerIP");
				out.writeUTF(server);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		    getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}
	 
	public void getCurrentPlayers(String server){
		 servercmd.put(server, "curp");
//	 		System.out.println("New request: " + server);
	 		  final ByteArrayOutputStream b = new ByteArrayOutputStream();
	          final DataOutputStream out = new DataOutputStream(b);            		    
			    try {
			    	out.writeUTF("PlayerCount");
			    	out.writeUTF(server);		
			    } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	 		getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
	 		
 	}
	
	public void getMaxPlayers(String server){
		servercmd.put(server, "maxp");
		  final ByteArrayOutputStream b = new ByteArrayOutputStream();
          final DataOutputStream out = new DataOutputStream(b);            		    
		    try {
		    	out.writeUTF("ServerIP");
				out.writeUTF(server);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		    getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}
	
	public void getIP(String server){
		 servercmd.put(server, "ip");
		  final ByteArrayOutputStream b = new ByteArrayOutputStream();
         final DataOutputStream out = new DataOutputStream(b);            		    
		    try {
		    	out.writeUTF("ServerIP");
				out.writeUTF(server);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		    getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}
	
	public boolean online(InetSocketAddress adr){
		Socket s = new Socket();		
		try {
			s.setSoTimeout(400);
			s.connect(adr, 400);
			if(s.isConnected()){
				s.close();
				return true;		
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return false;	
	}
		
}
