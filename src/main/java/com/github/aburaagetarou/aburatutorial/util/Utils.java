package com.github.aburaagetarou.aburatutorial.util;

import com.github.aburaagetarou.aburatutorial.AburaTutorial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * メッセージ、コマンド送信等
 * @author AburaAgeTarou
 */
public class Utils {

	// メッセージ送信
	public static DiverseTask sendTutorialMessage(Player player, List<String> messages){

		DiverseTask task = new DiverseSyncTask(AburaTutorial.getInstance()).waitTick(0);

		if(messages == null) return task;

		for(String msg : messages){
			if(msg == null) continue;
			if(msg.startsWith("wait ")) {
				try {
					long ticks = Integer.parseInt(msg.replace("wait ", ""));
					task = task.after(new DiverseSyncTask(AburaTutorial.getInstance()).waitTick(ticks));
				} catch (NumberFormatException ignore) {}
			}else if(msg.startsWith("/")) {
				task = task.after(new DiverseSyncTask(AburaTutorial.getInstance()).single(0, (loop) -> {
					Bukkit.dispatchCommand(player, msg.replace("{name}", player.getName()).substring(1));
				}));
			}else if(msg.startsWith("$")) {
				task = task.after(new DiverseSyncTask(AburaTutorial.getInstance()).single(0, (loop) -> {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg.replace("{name}", player.getName()).substring(1));
				}));
			}else {
				task = task.after(new DiverseSyncTask(AburaTutorial.getInstance()).single(0, (loop) -> {
					player.sendMessage(TextComponent.ofChildren(
						AburaTutorial.MESSAGE_PREFIX,
						Component.text(msg.replace("{name}", player.getName()))
					));
				}));
			}
		}

		return task;
	}
}
