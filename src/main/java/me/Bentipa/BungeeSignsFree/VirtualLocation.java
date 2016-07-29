/*
 * stealth-coders (c) 2016 
 * Copyright by stealth-coders:
 * You are NOT allowed to share, upload or decompile this plugin at any time.
 * You are NOT allowed to share, upload or use code parts/snippets of this plugin without our consent.
 * You are allowed to use this software only for yourself and/or your server/servers.
 * The respective Owner of this Software is stealth-coders.
 */
package me.Bentipa.BungeeSignsFree;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author Benjamin
 */
public class VirtualLocation {
    
    private double x;
    private double y;
    private double z;
    private String worldname;
    
    public double getX(){
    	return x; 
    }
    
    public double getY(){
    	return y; 
    }
    
    public double getZ(){
    	return z; 
    }
    
    public String getWorldname(){
    	return worldname; 
    }
    
    public VirtualLocation(Location realloc){
        this.x = realloc.getBlockX();
        this.y = realloc.getBlockY();
        this.z = realloc.getBlockZ();
        this.worldname = realloc.getWorld().getName();
    }
    
    public VirtualLocation(double x, double y, double z, String world){
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldname = world;
    }
    
    public Location toRealLocation(){        
        return new Location(Bukkit.getWorld(worldname), x, y, z);
    }
}
