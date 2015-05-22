package me.Bentipa.BungeeSigns.animation;

import java.util.ArrayList;

public class Animation {

	public enum AnimationType{
		TEXT_LINE, COLOR_SWITCH, TEXT_REFRESH;
	}

	private AnimationType type;
	private ArrayList<AnimationStep> steps = new ArrayList<AnimationStep>();
	
	public Animation(AnimationType at){
		this.type = at;
	}
	public Animation(String at){
		switch(at){
		case "TEXT-LINE":
			this.type = AnimationType.TEXT_LINE;
			break;
		case "COLOR-SWITCH":
			this.type = AnimationType.COLOR_SWITCH;
			break;
		case "TEXT-REFRESH":
			this.type = AnimationType.TEXT_REFRESH;
			break;
		}		
	}
	
	public void addAnimationStep(AnimationStep as){
		steps.add(as);
	}
	
	public void removeAnimationStep(AnimationStep as){
		steps.remove(as);
	}
	
	
	public ArrayList<AnimationStep> getAnimationSteps(){
		return this.steps;
	}
	
	public AnimationStep getAnimationStep(int stepnumber){
		if(stepnumber < steps.size()){
			return steps.get(stepnumber);
		}else{
			return steps.get(steps.size()-1);
		}
	}
	
	public AnimationType getType(){
		return this.type;
	}
}
