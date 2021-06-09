package com.github.aburaagetarou.aburatutorial.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public abstract class DiverseTask implements Runnable {

	// スケジュールの実行に必要なプラグインインスタンス
	protected JavaPlugin plugin;

	// スケジュールに割り振られたタスクID
	protected BukkitTask bukkitTask;

	// 回数
	protected long times = 0;

	// 遅延
	protected long delay = 0L;

	// 残り時間
	protected int count = 0;

	// キャンセルフラグ
	protected boolean cancel = false;

	// 停止フラグ
	protected boolean stop = false;

	// 処理
	protected Consumer<DiverseTask> loopTask;
	protected Runnable loopEndTask;
	protected DiverseTask afterTask;
	protected DiverseTask root;

	public DiverseTask(JavaPlugin plugin){
		this.plugin = plugin;
	}

	@Override
	public abstract void run();

	public abstract void start();

	public void cancel(){
		this.cancel = true;
	}

	public void stop(){
		this.stop = true;
	}

	public DiverseTask loop(long times, long delay, Consumer<DiverseTask> loopTask){
		this.times = times;
		this.delay = delay;
		this.loopTask = loopTask;
		return this;
	}

	public DiverseTask whileTask(long delay, Consumer<DiverseTask> loopTask){
		this.times = Long.MAX_VALUE;
		this.delay = delay;
		this.loopTask = loopTask;
		return this;
	}

	public DiverseTask waitTick(long delay){
		this.times = 1L;
		this.delay = delay;
		this.loopTask = (task) -> {};
		return this;
	}

	public DiverseTask loopEnd(Runnable loopEndTask){
		this.loopEndTask = loopEndTask;
		return this;
	}

	public DiverseTask single(long delay, Consumer<DiverseTask> loopTask){
		this.times = 1L;
		this.delay = delay;
		this.loopTask = loopTask;
		return this;
	}

	public DiverseTask after(DiverseTask afterTask){
		this.afterTask = afterTask;
		if(this.root == null) this.root = this;
		this.afterTask.root = this.root;
		return this.afterTask;
	}

	public DiverseTask root(){
		if(root == null) return this;
		return root;
	}

	public void stopLoop(){
		if(bukkitTask != null) bukkitTask.cancel();
	}

	public int getCount(){ return count; }
}
