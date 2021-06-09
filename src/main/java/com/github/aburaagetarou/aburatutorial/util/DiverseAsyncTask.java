package com.github.aburaagetarou.aburatutorial.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DiverseAsyncTask extends DiverseTask{

	// コンストラクタ
	public DiverseAsyncTask(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public void run() {

		// 停止
		if(stop) {
			stopLoop();
			return;
		}

		// 開始
		if(count++ == 0) {
			bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, delay);
			return;
		}

		// 終了
		if(count > times || cancel) {
			if(loopEndTask != null) Bukkit.getScheduler().runTaskAsynchronously(plugin, loopEndTask);
			if(afterTask != null) afterTask.start();
			stopLoop();
			return;
		}

		loopTask.accept(this);
	}

	@Override
	public void start() {

		// 遅延がない場合
		if(delay <= 0L){
			for(int i = 0; i < times; i++){
				Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> loopTask.accept(this));
			}
			if(loopEndTask != null) Bukkit.getScheduler().runTaskAsynchronously(plugin, loopEndTask);
			if(afterTask != null) afterTask.start();
		}else{
			Bukkit.getScheduler().runTaskAsynchronously(plugin, this);
		}
	}
}
