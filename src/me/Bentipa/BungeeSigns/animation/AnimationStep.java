package me.Bentipa.BungeeSigns.animation;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.Bentipa.BungeeSigns.animation.Animation.AnimationType;

public class AnimationStep {
	private AnimationType at;
	
	private ArrayList<String> animationlines = new ArrayList<String>();
	private ArrayList<ChatColor> animationcolors = new ArrayList<ChatColor>();
	private ArrayList<Integer> refreshlines = new ArrayList<Integer>();
	public AnimationStep(AnimationType at){
		this.at = at;
	}
	
	public AnimationStep addLine(String s){
		animationlines.add(s);
		return this;
	}
	
	public AnimationStep addColor(ChatColor c){	
		animationcolors.add(c);
		return this;
	}
	
	public AnimationStep addRefreshLine(int line){
		refreshlines.add(line);
		return this;
	}
	
	public ArrayList<Integer> getRefreshLines(){
		return this.refreshlines;
	}
	
	public ArrayList<String> getAnimationLines(){
		return this.animationlines;
	}
	
	public String getAnimationLine(int line){
		return animationlines.get(line);
	}
	
	public ChatColor getAnimationColor(int line){	
		if(line < animationcolors.size()){
			return animationcolors.get(line);
		}else{
			return ChatColor.RESET;
		}
	}
	
	public ArrayList<ChatColor> getAnimationColors(){
		return this.animationcolors;
	}
	
}
