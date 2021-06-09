package com.github.aburaagetarou.aburatutorial.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

	private final Plugin plugin;
	private FileConfiguration config = null;

	public boolean resetOnLogin = false;

	public Config(Plugin plugin) {
		this.plugin = plugin;
	}

	public void load(){
		plugin.saveDefaultConfig();
		if(config != null) {
			plugin.reloadConfig();
		}
		config = plugin.getConfig();

		resetOnLogin = getConfig("resetOnLogin", Boolean.class, false);
	}

	public <T> T getConfig(String path, Class<T> clazz, T defVal){

		// コンフィグがない場合
		if(config == null) return defVal;

		// コンフィグに値が存在しない場合
		if(!config.contains(path)) {
			config.set(path, defVal);
			return defVal;
		};

		// 取得
		try{
			return config.getObject(path, clazz);
		}catch (Exception e){
			return defVal;
		}
	}
}
