package me.Bentipa.BungeeSigns;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

public class BungeeSign {

	private Location loc;
	private String conserver = "freefight";
	private HashMap<Integer, String> lines = new HashMap<Integer, String>();
	private Sign realsign;
	private BSignsMain core;
	private InetAddress serverip;
	
	public BungeeSign(BSignsMain bsm, Location loc, String server){
		this.loc = loc;
		this.conserver = server;
		this.core = bsm;
		try{
			realsign  = (Sign) loc.getWorld().getBlockAt(loc).getState();
		}catch(Exception e){
			// Unable to set Sign!
			Bukkit.getLogger().severe("Error in loading/creating BungeeSign which connects to Server '" + server+"'");
		}
	}
	
	public void setIP(InetAddress ia){
		this.serverip = ia;
	}
	
	public BungeeSign(BSignsMain bsm, Location loc){
		this(bsm, loc, "");
	}
	
	public void setSign(Sign s){
		this.realsign = s;
	}
	
	public Sign getSign(){
		return this.realsign;
	}
	
	public void setServer(String server){
		this.conserver = server;
	}
	
	@Override
	public String toString(){
		String positions = getLocation().getBlockX() + "," + getLocation().getBlockY() + "," + getLocation().getBlockZ() + "," + getLocation().getWorld().getName();
		String lines = " " + getLine(0).replace(" ", "_") + " " + getLine(1).replace(" ", "_") + " " + getLine(2).replace(" ", "_") +" " + getLine(3).replace(" ", "_");
		String server = getServer();
		return server+" "+positions+" "+lines;
	}
	
	public boolean equals(Sign s){
		Location sl = s.getLocation();
		if(sl.getBlockX() == loc.getBlockX() &&
		   sl.getBlockY() == loc.getBlockY() &&
		   sl.getBlockZ() == loc.getBlockZ() &&
		   sl.getWorld() == loc.getWorld())
			return true;
		
		return false;
	}
	
	public void setLine(int line, String con){
		if(line<5&&line>=0)
			this.lines.put(line, con);
	}
	
	public String getLine(int line){
		if(line<5&&line>=0)
			return this.lines.get(line);
		else
			return "";
	}
	
	public ArrayList<String> getRawLines(){
		return new ArrayList<String>(this.lines.values());
	}		
	
	public Location getLocation(){
		return this.loc;
	}
	
	public String getServer(){
		return this.conserver;
	}
	
	public void refresh(){

		int line = 0;
		for(String s : this.lines.values()){
			String set = getS(line, s);
//			System.out.println("line " + line  + " from " + s + " to " + set);
			this.getSign().setLine(line, set);
			line++;
		}
		this.getSign().update(true);

	}
	
	public String extractColor(String raw){
		return ChatColor.translateAlternateColorCodes('&', raw);
	}	
	
	private HashMap<Integer, ColorAnimation> cans = new HashMap<Integer, ColorAnimation>();
	
	private String getS(int line, String ph){
//		System.out.println("getS - " + line + " ->" + ph);
		if(ph.contains("%motd%"))		
			ph = ph.replace("%motd%", getMotd()== null ? "" : getMotd());
		
		if(ph.contains("%cplayers%"))		
			ph = ph.replace("%cplayers%", (getCurP() == null ? "" : getCurP()) +"");
		
		if(ph.contains("%mplayers%"))
			ph = ph.replace("%mplayers%", getMaxP() == null ? "" : getMaxP());
		
		if(ph.contains("%cswitch(")){
			String colorcontent = getCswitchContent(ph);
//			System.out.println(cans.containsKey(line));
			ColorAnimation ca = cans.get(line)==null? new ColorAnimation(colorcontent) : cans.get(line);
			if(!cans.containsKey(line)){
				cans.put(line, ca);
			}
			ph = replaceCswitch(ph, ca.getColorWithTick()+"");
		}
//		System.out.println( "Result: " + ph);
		
		return ph;
	}
	
	private String replaceCswitch(String ph, String repl){ 
		int start = 0;
		for(int i = 0; i < ph.length(); i++){
			int end = (i+8 > ph.length()) ? i+ph.length()-i : i+8;
			if(ph.substring(i, end).equals("%cswitch")){
				start = i+8;
				break;
			}
		}
		int end = 0;
		for(int i = start; i < ph.length(); i++){
			if(ph.charAt(i) == '%'){
				end = i;
			}
		}
		return ph.replace(ph.substring(start-8, end+1), repl); 		
	}
	
	private String getCswitchContent(String ph){
		int start = 0;
		for(int i = 0; i < ph.length(); i++){
			int end = (i+8 > ph.length()) ? i+ph.length()-i : i+8;
			if(ph.substring(i, end).equals("%cswitch")){
				start = i+8;
				break;
			}
		}
		int startbracket = start+1;
		int end = 0;
		for(int i = start; i < ph.length(); i++){
			if(ph.charAt(i) == '%'){
				end = i;
			}
		}
		int endbracket = end-1;
		return ph.substring(startbracket, endbracket);
	}
	
	private String getContent(String ph, String startseq, char endseq){
		int start = 0;
		for(int i = 0; i < ph.length(); i++){
			int end = (i+startseq.length() > ph.length()) ? i+ph.length()-i : i+startseq.length();
			if(ph.substring(i, end).equals(startseq)){
				start = i+startseq.length();
				break;
			}
		}
		int end = 0;
		for(int i = start; i < ph.length(); i++){
			if(ph.charAt(i) == endseq){
				end = i;
			}
		}
		return ph.substring(start, end);
	}
	
	private String getMotd(){
		core.getMotd(this.getServer());				
		try {
			Thread.sleep(520);
		} catch (InterruptedException ie) {
			// TODO Auto-generated catch block
			//ie.printStackTrace();
		}  
				
		return core.motds.get(this.getServer());	
	}
	
	private String getMaxP(){	
		if(!core.maxp.containsKey(this.getServer()))
		core.getMaxPlayers(this.getServer());				
		try {
			Thread.sleep(420);
		} catch (InterruptedException ie) {
			// TODO Auto-generated catch block
			//ie.printStackTrace();
		}  
				
		return core.maxp.get(this.getServer());	
	}
	
	private Integer getCurP(){
		core.getCurrentPlayers(this.getServer());				
		try {
			Thread.sleep(420);
		} catch (InterruptedException ie) {
			// TODO Auto-generated catch block
			//ie.printStackTrace();
		}  
					
		return core.curp.get(this.getServer());	
	}

	public static BungeeSign outOfString(BSignsMain core, String s){
//		String positions = getLocation().getBlockX() + "," + getLocation().getBlockY() + "," + getLocation().getBlockZ() + "," + getLocation().getWorld().getName();
//		String lines = "L0:" + getLine(0) + "L1:" + getLine(1) + "L2:" + getLine(2) +"L3:" + getLine(3);
//		
		String[] parts = s.split(" ");
		// parts[0] = server
		// parts[1] = positions
		// parts[2] = line1
		// parts[3] = line2
		// parts[4] = line3
		// parts[5] = line4
		Location loc ;
//		System.out.println("parts[0] " + parts[0]);
//		System.out.println("parts[1] " + parts[1]);
//		System.out.println("parts[2] " + parts[2]);
//		System.out.println("parts[3] " + parts[3]);
//		System.out.println("parts[4] " + parts[4]);
//		System.out.println("parts[5] " + parts[5]);
//		System.out.println("parts[6] " + parts[6]);
//		
		String position = parts[1];
		String[] positions = position.split(",");
		int x = Integer.valueOf(positions[0]);
		int y = Integer.valueOf(positions[1]);
		int z = Integer.valueOf(positions[2]);
		World w = Bukkit.getWorld(positions[3]);
		BungeeSign bs = new BungeeSign(core, new Location(w, x, y, z));
		bs.setServer(parts[0]);
		bs.setLine(0, parts[3].replace("_", " "));
		bs.setLine(1, parts[4].replace("_", " "));
		bs.setLine(2, parts[5].replace("_", " "));
		bs.setLine(3, parts[6].replace("_", " "));
		return bs;	
	}
	
	
	private class ColorAnimation{
		
		private ArrayList<String> c = new ArrayList<String>();
		
		private int switcher = 0;
		
		public ColorAnimation(String raw){
			String[] codes = raw.split(",");
			for(String s : codes){
				c.add(ChatColor.translateAlternateColorCodes('&', s));
			}
		}		
		
		public String getColorWithTick(){				
			if(switcher < c.size()){		
				String ret =  c.get(switcher);
				switcher++;
				return ret;
			}else{
				switcher = 0;
				String ret =  c.get(switcher);
				switcher++;
				return ret;
			}
		}
		
		public String getColor(){
			if(switcher < c.size()){
				return c.get(switcher);
			}else{
				return c.get(c.size()-1);
			}
		}
	
	}
	
	
	

}
