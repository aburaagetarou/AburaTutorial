package com.github.aburaagetarou.aburatutorial;

import com.github.aburaagetarou.aburatutorial.command.TutorialCommand;
import com.github.aburaagetarou.aburatutorial.command.TutorialTabCompleter;
import com.github.aburaagetarou.aburatutorial.trigger.listeners.PlayerJoinListener;
import com.github.aburaagetarou.aburatutorial.trigger.listeners.TutorialClearListener;
import com.github.aburaagetarou.aburatutorial.tutorial.Tutorials;
import com.github.aburaagetarou.aburatutorial.util.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class AburaTutorial extends JavaPlugin {

	private static AburaTutorial instance;
	private static File tutorialFile;
	public final static String TRIGGER_PATH = "trigger" + System.getProperty("file.separator");
	public static String fileName = "tutorial.yml";

	private static Config config;

	public static Component MESSAGE_PREFIX = TextComponent.ofChildren(
			Component.text("[").color(TextColor.color(NamedTextColor.GRAY)),
			Component.text("Tutorial").color(TextColor.color(NamedTextColor.AQUA)),
			Component.text("] ").color(TextColor.color(NamedTextColor.GRAY))
	);

	@Override
	public void onEnable() {

		instance = this;

		// コンフィグファイルを生成する
		tutorialFile = new File(getDataFolder(), fileName);
		boolean exists = tutorialFile.exists();
		if(!exists){
			try {
				getLogger().info("チュートリアルファイルを作成します。パス：" + tutorialFile.getPath());
				if(getDataFolder().exists()) exists = getDataFolder().mkdirs();
				if(exists) exists = tutorialFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				getLogger().warning("チュートリアルのロードに失敗しました...");
			}
		}
		if(exists) Tutorials.load(tutorialFile);

		// Javaトリガー用ファイルを生成する
		File trigger = new File(getDataFolder(), TRIGGER_PATH);
		if(!trigger.exists()){
			getLogger().info("トリガーディレクトリを生成します。パス：" + tutorialFile.getPath());
			trigger.mkdirs();
		}

		// 登録
		Bukkit.getPluginManager().registerEvents(new TutorialClearListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getCommand("tutorial").setExecutor(new TutorialCommand());
		getCommand("tutorial").setTabCompleter(new TutorialTabCompleter());

		// コンフィグ読み込み
		config = new Config(this);
		config.load();
	}

	@Override
	public void onDisable() {

		// チュートリアルをアンロード
		Tutorials.unload();
	}

	// プラグイン インスタンスを取得する
	public static AburaTutorial getInstance() {
		return instance;
	}

	public static File getTutorialFile() {
		return tutorialFile;
	}

	public static Config getPluginConfig() {
		return config;
	}
}
