package me.Bentipa.BungeeSigns;

public class BungeeSignsRefresher{

	private BSignsMain core;
	private boolean active;
	int taskid = 0;
	public BungeeSignsRefresher(BSignsMain main) {
		this.core = main;		
	}
	
	public void start(){
		if(taskid!=0)
			core.getServer().getScheduler().cancelTask(taskid);
			
		taskid = core.getServer().getScheduler().scheduleSyncRepeatingTask(core, new Runnable() {
			
			@Override
			public void run() {
				refreshAll();
			}
		}, 0L, 20L);
	}
	
	public void stop(){
		if(taskid!=0)
			core.getServer().getScheduler().cancelTask(taskid);
	}
	
	private void refreshAll(){
		for(BungeeSign bs : core.getSigns()){
			bs.refresh();
		}
	}
}
