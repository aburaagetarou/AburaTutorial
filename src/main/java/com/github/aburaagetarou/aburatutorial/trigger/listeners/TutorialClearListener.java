package com.github.aburaagetarou.aburatutorial.trigger.listeners;

import com.github.aburaagetarou.aburatutorial.AburaTutorial;
import com.github.aburaagetarou.aburatutorial.trigger.Trigger;
import com.github.aburaagetarou.aburatutorial.tutorial.Tutorial;
import com.github.aburaagetarou.aburatutorial.tutorial.Tutorials;
import com.github.aburaagetarou.aburatutorial.util.EntityPersistentUtils;
import com.github.aburaagetarou.aburatutorial.util.PersistentKeyName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

/**
 * チュートリアルをすすめるイベントをリッスンする
 * @author AburaAgeTarou
 */
public class TutorialClearListener implements Listener {

	final List<Action> catchActions = Arrays.asList(
			Action.LEFT_CLICK_AIR,
			Action.LEFT_CLICK_BLOCK,
			Action.RIGHT_CLICK_AIR,
			Action.RIGHT_CLICK_BLOCK
	);

	// クリックトリガー
	@EventHandler
	public void tutorialClearTrigger(PlayerInteractEvent event) {

		if(!catchActions.contains(event.getAction())) return;

		// クリック時
		String tutorialNow = EntityPersistentUtils.getString(event.getPlayer(), PersistentKeyName.TUTORIAL);
		Long tutorialPhase = EntityPersistentUtils.getLong(event.getPlayer(), PersistentKeyName.TUTORIAL_PHASE);
		if(tutorialNow != null && tutorialPhase != null){
			Tutorial tutorial = Tutorials.get(tutorialNow);
			int phase = tutorialPhase.intValue();
			if(tutorial.clearTrigger(phase) != null) {
				if(tutorial.clearTrigger(phase).test(new Trigger(event, tutorial))){
					tutorial.next(event.getPlayer());
				}
			}else{
				event.getPlayer().sendMessage(
						TextComponent.ofChildren(
								AburaTutorial.MESSAGE_PREFIX,
								Component.text("クリア条件が設定されていません。").color(TextColor.color(NamedTextColor.RED))
						)
				);
			}
		}
	}

	@EventHandler
	public void tutorialClearTrigger(EntityDamageByEntityEvent event){

		// プレイヤー、発射体ではない場合は処理しない
		Player player = null;
		if(event.getDamager() instanceof Player){

			// プレイヤーにキャスト
			player = ((Player) event.getDamager());
		}else if (event.getDamager() instanceof Projectile){
			Projectile projectile = (Projectile) event.getDamager();
			if(projectile.getShooter() instanceof Player){
				player = (Player) projectile.getShooter();
			}
		}

		if(player == null) return;

		// チュートリアル
		String tutorialNow = EntityPersistentUtils.getString(player, PersistentKeyName.TUTORIAL);
		Long tutorialPhase = EntityPersistentUtils.getLong(player, PersistentKeyName.TUTORIAL_PHASE);
		if(tutorialNow != null && tutorialPhase != null){
			Tutorial tutorial = Tutorials.get(tutorialNow);
			int phase = tutorialPhase.intValue();

			if(tutorial.clearTrigger(phase) != null) {
				if(tutorial.clearTrigger(phase).test(new Trigger(event, tutorial))){
					tutorial.next(player);
				}
			}else{
				player.sendMessage(
						TextComponent.ofChildren(
								AburaTutorial.MESSAGE_PREFIX,
								Component.text("クリア条件が設定されていません。").color(TextColor.color(NamedTextColor.RED))
						)
				);
			}
		}
	}
}
