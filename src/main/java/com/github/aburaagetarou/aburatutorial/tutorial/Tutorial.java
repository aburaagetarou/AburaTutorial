package com.github.aburaagetarou.aburatutorial.tutorial;

import com.github.aburaagetarou.aburatutorial.AburaTutorial;
import com.github.aburaagetarou.aburatutorial.trigger.Trigger;
import com.github.aburaagetarou.aburatutorial.trigger.TriggerType;
import com.github.aburaagetarou.aburatutorial.util.DiverseSyncTask;
import com.github.aburaagetarou.aburatutorial.util.EntityPersistentUtils;
import com.github.aburaagetarou.aburatutorial.util.PersistentKeyName;
import com.github.aburaagetarou.aburatutorial.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;


/**
 * チュートリアルを管理するクラス
 * @author AburaAgeTarou
 */
public class Tutorial {

	// このチュートリアルのフェイズと動きを保存するマップ
	public Map<Integer, TutorialPhase> phases = new HashMap<>();

	String name;				// 名称
	String displayName;			// 表示名
	List<String> endMessage; 			// 終了メッセージ

	// フェイズがマップに存在しない場合、作成する
	private void makePhaseIfExists(int phase){
		if(!phases.containsKey(phase)) phases.put(phase, new TutorialPhase());
	}

	// 名称セット
	public Tutorial name(String name){
		this.name = name;
		return this;
	}

	// 表示名セット
	public Tutorial displayName(String name){
		this.displayName = name;
		return this;
	}

	// 開始メッセージ
	public Tutorial startMessage(int phase, List<String> messages){
		makePhaseIfExists(phase);
		phases.get(phase).startMsg = messages;
		return this;
	}
	@Nullable
	public List<String> startMessage(int phase){
		return phases.containsKey(phase) ? phases.get(phase).startMsg : null;
	}

	// 開始と同時に実行するコマンド
	public Tutorial startCommand(int phase, List<String> commands){
		makePhaseIfExists(phase);
		phases.get(phase).startCmd = commands;
		return this;
	}
	@Nullable
	public List<String> startCommand(int phase){
		return phases.containsKey(phase) ? phases.get(phase).startCmd : null;
	}

	// クリアに必要なアイテム
	public Tutorial triggerItem(int phase, Map<Integer, Material> mat){
		makePhaseIfExists(phase);
		phases.get(phase).itemMaterial = mat;
		return this;
	}
	@Nullable
	public Map<Integer, Material> triggerItem(int phase){
		return phases.containsKey(phase) ? phases.get(phase).itemMaterial : null;
	}

	// クリアに必要なアイテム
	public Tutorial itemNBT(int phase, Map<Integer, Map<String, String>> nbt){
		makePhaseIfExists(phase);
		phases.get(phase).itemNBT = nbt;
		return this;
	}
	@Nullable
	public Map<Integer, Map<String, String>> itemNBT(int phase){
		return phases.containsKey(phase) ? phases.get(phase).itemNBT : null;
	}

	// 終了メッセージ
	public Tutorial endMessage(int phase, List<String> messages){
		makePhaseIfExists(phase);
		phases.get(phase).endMsg = messages;
		return this;
	}
	@Nullable
	public List<String> endMessage(int phase){
		return phases.containsKey(phase) ? phases.get(phase).endMsg : null;
	}

	// 終了と同時に実行するコマンド
	public Tutorial endCommand(int phase, List<String> commands){
		makePhaseIfExists(phase);
		phases.get(phase).endCmd = commands;
		return this;
	}
	@Nullable
	public List<String> endCommand(int phase){
		return phases.containsKey(phase) ? phases.get(phase).endCmd : null;
	}

	// クリア判定部
	public Tutorial clearTrigger(int phase, Predicate<Trigger> trigger){
		makePhaseIfExists(phase);
		phases.get(phase).trigger = trigger;
		return this;
	}
	@Nullable
	public Predicate<Trigger> clearTrigger(int phase){
		return phases.containsKey(phase) ? phases.get(phase).trigger : null;
	}

	// クリア判定トリガー
	public Tutorial triggerType(int phase, TriggerType type){
		makePhaseIfExists(phase);
		phases.get(phase).triggerType = type;
		return this;
	}
	@Nullable
	public TriggerType triggerType(int phase){
		return phases.containsKey(phase) ? phases.get(phase).triggerType : null;
	}
	@Nullable
	public TriggerType triggerType(Player player){
		Long phasePers = EntityPersistentUtils.getLong(player, PersistentKeyName.TUTORIAL_PHASE);
		if(phasePers == null) return null;
		int phase = phasePers.intValue();
		return phases.containsKey(phase) ? phases.get(phase).triggerType : null;
	}

	// クリア判定トリガー
	public Tutorial attackTrigger(int phase, EntityDamageEvent.DamageCause type){
		makePhaseIfExists(phase);
		phases.get(phase).attackType = type;
		return this;
	}
	@Nullable
	public EntityDamageEvent.DamageCause attackTrigger(int phase){
		return phases.containsKey(phase) ? phases.get(phase).attackType : null;
	}
	@Nullable
	public EntityDamageEvent.DamageCause attackTrigger(Player player){
		Long phasePers = EntityPersistentUtils.getLong(player, PersistentKeyName.TUTORIAL_PHASE);
		if(phasePers == null) return null;
		int phase = phasePers.intValue();
		return phases.containsKey(phase) ? phases.get(phase).attackType : null;
	}

	// クリック対象ブロックのタイプ
	public Tutorial clickedType(int phase, Material type){
		makePhaseIfExists(phase);
		phases.get(phase).clickType = type;
		return this;
	}
	@Nullable
	public Material clickedType(int phase){
		return phases.containsKey(phase) ? phases.get(phase).clickType : null;
	}

	// チュートリアル開始
	public void start(Player player){

		// 次のフェーズへ
		EntityPersistentUtils.setString(player, PersistentKeyName.TUTORIAL, name);
		EntityPersistentUtils.setLong(player, PersistentKeyName.TUTORIAL_PHASE, 1L);

		// メッセージ送信、コマンド実行
		Utils.sendTutorialMessage(player, startMessage(1)).root().start();
	}

	// 次のフェーズに進む
	// 全行程終了の場合、終了する
	public void next(Player player) {
		String tutorialNow = EntityPersistentUtils.getString(player, PersistentKeyName.TUTORIAL);
		Long tutorialPhase = EntityPersistentUtils.getLong(player, PersistentKeyName.TUTORIAL_PHASE);
		if (tutorialNow != null && tutorialPhase != null) {

			// チュートリアルの名前が異なる場合は処理しない
			if(!tutorialNow.equals(this.name)) return;

			// フェーズ取得
			int phase = tutorialPhase.intValue();

			// 次のフェーズへ
			EntityPersistentUtils.setLong(player, PersistentKeyName.TUTORIAL_PHASE, ++phase);

			// メッセージ送信、コマンド実行
			Utils.sendTutorialMessage(player, endMessage(phase - 1))
					.after(new DiverseSyncTask(AburaTutorial.getInstance()).waitTick(60L))
					.after(Utils.sendTutorialMessage(player, startMessage(phase)).root()).root().start();

			// 次のフェーズが存在しない場合は完了
			if(!phases.containsKey(phase)){
				end(player);
			}
		}
	}

	// 終了時メッセージ
	public Tutorial endMessage(List<String> messages){
		endMessage = messages;
		return this;
	}

	// 終了
	public void end(Player player) {
		EntityPersistentUtils.remove(player, PersistentKeyName.TUTORIAL);
		EntityPersistentUtils.remove(player, PersistentKeyName.TUTORIAL_PHASE);
		player.sendTitle("§lチュートリアルを完了しました！", displayName, 10, 60, 10);
		Utils.sendTutorialMessage(player, endMessage).root().start();
	}

	// フェーズセット
	public void setPhase(Player player, int phase) {

		EntityPersistentUtils.setLong(player, PersistentKeyName.TUTORIAL_PHASE, phase);

		// メッセージ送信、コマンド実行
		Utils.sendTutorialMessage(player, startMessage(phase)).root().start();
	}

	public Set<Integer> getPhases() {
		return phases.keySet();
	}

	// 前のフェーズに戻る
	// 戻り値がfalse: フェーズ移動失敗（最初のフェーズなど）
	public boolean prev(Player player){
		String tutorialNow = EntityPersistentUtils.getString(player, PersistentKeyName.TUTORIAL);
		Long tutorialPhase = EntityPersistentUtils.getLong(player, PersistentKeyName.TUTORIAL_PHASE);
		if (tutorialNow != null && tutorialPhase != null) {

			// チュートリアルの名前が異なる場合は処理しない
			if(!tutorialNow.equals(this.name)) return true;

			// 前のフェーズが存在しない場合は完了
			int phase = tutorialPhase.intValue();
			if(!phases.containsKey(--phase)) return false;

			// 前のフェーズへ
			EntityPersistentUtils.setLong(player, PersistentKeyName.TUTORIAL_PHASE, phase);
			return true;
		}
		return false;
	}
}
