package com.github.aburaagetarou.aburatutorial.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DiverseSyncTask extends DiverseTask{

	// コンストラクタ
	public DiverseSyncTask(JavaPlugin plugin) {
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
			if(loopTask != null){
				if(times == 1) bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, 0L);
				else           bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, delay);
			}
			return;
		}

		// 終了
		if(count > times || cancel) {
			if(loopEndTask != null) Bukkit.getScheduler().runTask(plugin, loopEndTask);
			if(afterTask != null) {
				afterTask.start();
			}
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
				Bukkit.getScheduler().runTask(plugin, () -> loopTask.accept(this));
			}
			if(loopEndTask != null) Bukkit.getScheduler().runTask(plugin, loopEndTask);
			if(afterTask != null) afterTask.start();
		}else{
			Bukkit.getScheduler().runTask(plugin, this);
		}
	}
}
