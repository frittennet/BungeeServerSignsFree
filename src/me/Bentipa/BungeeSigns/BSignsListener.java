package me.Bentipa.BungeeSigns;


import java.util.HashMap;
import me.Bentipa.BungeeSigns.BSignsMain.Step;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;




/**
 *
 * @author Benjamin
 */

class BSignsListener implements Listener{

	
	static BSignsMain configGetter;
    public BSignsListener(BSignsMain plugin) {    
      configGetter = plugin;
    }
     
    
  
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player p = event.getPlayer();
    	Block block = event.getClickedBlock(); 	
    	if(block != null)
    	if(block.getState() instanceof Sign){    
    		Sign s = (Sign) block.getState();
    		if(BSignsMain.inCreation.contains(p) && BSignsMain.creationStep.containsKey(p)){
   
    			if(BSignsMain.creationStep.get(p) == BSignsMain.Step.SELECT){
   
    				BSignsMain.creations.put(p, new BungeeSign(configGetter, block.getLocation()));
    				p.sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SELECT) + ChatColor.GREEN + "Succesfully set BungeeSigns-Sign ("+block.getX() + "|" + block.getY() + "|" + block.getZ() +")!");
    				BSignsMain.creationStep.put(p, Step.SERVER_NAME);    		
    				p.sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SERVER_NAME) + ChatColor.GREEN + "Now type in the name of to Server to connect to!");
    			}
    		}else 
    		if(BSignsMain.inRemove.contains(p)){   
    			if(configGetter.isSaved(s)){
    			configGetter.removeSign(configGetter.getBungeeSignsSign((Sign) block.getState()));
    			p.sendMessage(configGetter.MSG_PREFIX() + ChatColor.GREEN +"Sign succesfully removed!");
    			}else{
    			p.sendMessage(configGetter.MSG_PREFIX() + ChatColor.RED + "This Sign is not a BungeeSign-Sign!");
    			}
    			BSignsMain.inRemove.remove(p);
    		}
    		if(configGetter.getSigns().contains(configGetter.getBungeeSignsSign((Sign) block.getState()))){
    			if(configGetter.isSaved((Sign) block.getState())){
    				 ByteArrayDataOutput out = ByteStreams.newDataOutput();
    				 out.writeUTF("Connect");
    				 out.writeUTF(configGetter.getBungeeSignsSign((Sign) block.getState()).getServer());
    				 p.sendPluginMessage(configGetter, "BungeeCord", out.toByteArray());
    				
    			}
    		}
    	}
    	
    }
    private HashMap<Player, Integer> line = new HashMap<Player, Integer>();
    private HashMap<Player, Sign> ce = new HashMap<Player, Sign>();
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
    	if(BSignsMain.inCreation.contains(e.getPlayer())){
			if(BSignsMain.creationStep.get(e.getPlayer()).equals(BSignsMain.Step.SERVER_NAME)){
				String message = e.getMessage();
				if(!message.contains("!")&&
					!message.contains(" ")&&
					!message.contains(".")&&
					!message.contains(",")&&
					!message.contains("_")&&
					!message.contains(";")
						){
					if(serverExists(message) || !configGetter.sign_alive_check){
					BSignsMain.creations.get(e.getPlayer()).setServer(e.getMessage());
					e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SERVER_NAME) + ChatColor.GREEN + "Succesfully set Server to '" + ChatColor.GOLD + e.getMessage() + ChatColor.GREEN + "' !");
			
					BSignsMain.creationStep.put(e.getPlayer(), Step.SIGN_CONTENT);
					e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SIGN_CONTENT) + ChatColor.GREEN  + "Now type in the lines of the Sign:");
					line.put(e.getPlayer(), 1);
					e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SIGN_CONTENT) + ChatColor.GREEN  + "Line "+ChatColor.RED + line.get(e.getPlayer()) + ChatColor.GREEN + ":");
					ce.put(e.getPlayer(), configGetter.getSign(BSignsMain.creations.get(e.getPlayer())));
					e.setCancelled(true);
					}else{
						e.getPlayer().sendMessage(ChatColor.RED+ "Server does not exist !");
						e.setCancelled(true);
					}
				}else{
					e.getPlayer().sendMessage(ChatColor.RED + "Invalid characters!: (do not use '"+ChatColor.BLUE + "!"+ChatColor.RED+ "', '"+ChatColor.BLUE + " "+ChatColor.RED+ "', '"
				    +ChatColor.BLUE + "."+ChatColor.RED+ "', '"+ChatColor.BLUE + ","+ ChatColor.RESET + ChatColor.RED + "', '"+ChatColor.BLUE + ";"+ChatColor.RED+ "', '"+ChatColor.BLUE + "_"+ChatColor.RED+ "')");
					e.setCancelled(true);
				}
					
			}else
			if(BSignsMain.creationStep.get(e.getPlayer()).equals(BSignsMain.Step.SIGN_CONTENT)){
				if(line.get(e.getPlayer())!=5){
				String msg = e.getMessage();		
//				e.getPlayer().sendMessage((line.get(e.getPlayer())-1) + " to " + msg);
//				e.getPlayer().sendMessage("ce: " + ce.get(e.getPlayer()).getLine(line.get(e.getPlayer())-1));
				msg = msg.trim();
			
				BungeeSign bs = BSignsMain.creations.get(e.getPlayer());
				if(msg.contains("%cswitch")){
					e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SIGN_CONTENT) + ChatColor.GREEN  +"You added a Color-Switch Animation!");		
				}else{
					 msg = ChatColor.translateAlternateColorCodes('&', e.getMessage());	
				}
				if(msg.contains("%cplayers%")){				
					e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SIGN_CONTENT) + ChatColor.GREEN  +"You added a Players-Display!");		
				}
				if(msg.contains("%mplayers%")){				
					e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SIGN_CONTENT) + ChatColor.GREEN  +"You added a Players-Display!");
				}
				if(msg.contains("%motd%")){
					e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SIGN_CONTENT) + ChatColor.GREEN  +"You added a Motd-Display!");
				}
				if(msg.equalsIgnoreCase("[SPACE]")){
					msg = "";
				}
				bs.setLine((line.get(e.getPlayer())-1), msg);
				ce.get(e.getPlayer()).setLine((line.get(e.getPlayer())-1), msg);				
				e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SIGN_CONTENT) + ChatColor.GREEN  + "Line " +ChatColor.RED + line.get(e.getPlayer()) + ChatColor.GREEN + " set to: '" + ChatColor.RESET + msg + ChatColor.GREEN + "'");
				line.put(e.getPlayer(), line.get(e.getPlayer())+1);		
				if(line.get(e.getPlayer())==5){			
				configGetter.saveSign(BSignsMain.creations.get(e.getPlayer()));		

				Sign realsign = configGetter.getSign(BSignsMain.creations.get(e.getPlayer()));
				realsign.setLine(0, ce.get(e.getPlayer()).getLine(0));
				realsign.setLine(1, ce.get(e.getPlayer()).getLine(1));
				realsign.setLine(2, ce.get(e.getPlayer()).getLine(2));
				realsign.setLine(3, ce.get(e.getPlayer()).getLine(3));
				realsign.update(true);
				
				e.getPlayer().sendMessage(configGetter.MSG_PREFIX() +  ChatColor.GREEN + "Succesfully created BungeeSigns-Sign!");
				BSignsMain.inCreation.remove(e.getPlayer());
				}else{
					e.getPlayer().sendMessage(configGetter.MSG_PREFIX() + configGetter.SS(Step.SIGN_CONTENT) + ChatColor.GREEN  + "Line " +ChatColor.BLUE + line.get(e.getPlayer()) + ChatColor.GREEN + ":");
				}
				e.setCancelled(true);
			}
			}
		}
    }
    
    public void setSignStrings(Sign s){
    	s.setLine(0, ChatColor.GRAY + "[" + ChatColor.GREEN + "BungeeSigns" + ChatColor.GRAY + "]");
    	
    	
    	
    	if(configGetter.motds.get(configGetter.getBungeeSignsSign(s).getServer()) == null){
    	configGetter.getMotd(configGetter.getBungeeSignsSign(s).getServer());    
    	try {
			Thread.sleep(120);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
   
    
    	//int curp = configGetter.curp.get(configGetter.getBungeeSignsSign(s).getServer());
    	int maxp = getMaxPlayers(configGetter.motds.get(configGetter.getBungeeSignsSign(s).getServer()));
//    	if(curp < maxp){
//    		s.setLine(1, ChatColor.GRAY + "["+ChatColor.GREEN+ "JOIN" + ChatColor.GRAY + "]");
//    	}else{
//    		s.setLine(1, ChatColor.GRAY + "["+ChatColor.RED+ "FULL" + ChatColor.GRAY + "]");
//    	}
    	
//    	s.setLine(2, getFormattedMapString(configGetter.motds.get(configGetter.getBungeeSignsSign(s).getServer())));
//    	s.setLine(3, getFormattedPlayersString(configGetter.motds.get(configGetter.getBungeeSignsSign(s).getServer())));
//    	s.update(true);
    }
    
    public boolean serverExists(String servername){
    	configGetter.getCurrentPlayers(servername);
    	try {
			Thread.sleep(120);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	if(configGetter.curp.get(servername)!=null){    		
    	return true;	
    	}
    	return false;
    }
    
    public boolean canJoin(Sign s){
    	configGetter.getMotd(configGetter.getBungeeSignsSign(s).getServer());    
    	try {
			Thread.sleep(120);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	configGetter.getCurrentPlayers(configGetter.getBungeeSignsSign(s).getServer());
    	try {
			Thread.sleep(120);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	int curp = configGetter.curp.get(configGetter.getBungeeSignsSign(s).getServer());
    	int maxp = getMaxPlayers(configGetter.motds.get(configGetter.getBungeeSignsSign(s).getServer()));
    	if(curp < maxp){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public String getFormattedMapString(String raw){
    	if(raw!=null){
    	raw = raw.replace("\"", "");
    	String[] parts = raw.split(",");
		String map = parts[0];
		String[] mapparts = map.split(":");
		String end = "";
		end+=ChatColor.AQUA + mapparts[0] + ": " + ChatColor.GRAY + mapparts[1]; 
		return end;
    	}
 		return "";
    }

 	public String getFormattedPlayersString(String raw){
 		if(raw!=null){
        raw = raw.replace("\"", "");
    	String[] parts = raw.split(",");
		String plays = parts[1];
		String[] playerparts = plays.split(":");
		String end = "";
		String[] players = playerparts[1].split("x");
		end+=ChatColor.BLUE + players[0] +  ChatColor.GRAY+ "x" +ChatColor.BLUE + players[1]; 
		return end;
 		}
 		return "";
    }
 	
 	public int getMaxPlayers(String raw){
 		
 		if(raw!=null){
 	   	raw = raw.replace("\"", "");
    	String[] parts = raw.split(",");
		String plays = parts[1];
		String[] playerparts = plays.split(":");
		String end = "";
		String[] players = playerparts[1].split("x");
		int t = Integer.valueOf(players[0]);
		int ppt = Integer.valueOf(players[1]);
		return t*ppt;
 		}
 		return 0;
 	}
 	

}   

   