package com.github.aburaagetarou.aburatutorial.trigger.listeners;

import com.github.aburaagetarou.aburatutorial.AburaTutorial;
import com.github.aburaagetarou.aburatutorial.util.EntityPersistentUtils;
import com.github.aburaagetarou.aburatutorial.util.PersistentKeyName;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

	// 参加時のリセット処理
	@EventHandler
	public void tutorialClearTrigger(PlayerJoinEvent event) {

		if(AburaTutorial.getPluginConfig().resetOnLogin) {
			EntityPersistentUtils.remove(event.getPlayer(), PersistentKeyName.TUTORIAL);
			EntityPersistentUtils.remove(event.getPlayer(), PersistentKeyName.TUTORIAL_PHASE);
		}
	}
}
