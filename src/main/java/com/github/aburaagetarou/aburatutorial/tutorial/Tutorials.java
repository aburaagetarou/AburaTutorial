package com.github.aburaagetarou.aburatutorial.tutorial;

import com.github.aburaagetarou.aburatutorial.AburaTutorial;
import com.github.aburaagetarou.aburatutorial.trigger.Trigger;
import com.github.aburaagetarou.aburatutorial.trigger.TriggerType;
import com.github.aburaagetarou.aburatutorial.util.EntityPersistentUtils;
import com.github.aburaagetarou.aburatutorial.util.PersistentKeyName;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * チュートリアルのリストを管理する
 * @author AburaAgeTarou
 */
public class Tutorials {

	// チュートリアルリスト
	private static final Map<String, Tutorial> tutorials = new HashMap<>();

	public static final int SLOT_MAIN_HAND = -1;
	public static final int SLOT_OFF_HAND = -2;

	public static void load(File file){
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(config.getConfigurationSection("Tutorials") != null){

			// チュートリアルのフェーズ一覧を取得する
			config.getConfigurationSection("Tutorials").getKeys(false).forEach((key) -> {

				// チュートリアル作成
				Tutorial tutorial = new Tutorial();

				// フェーズを全ループ
				int count = 1;
				while(config.getConfigurationSection("Tutorials." + key + "." + count ) != null){

					// マテリアル取得
					Map<Integer, Material> mats = new HashMap<>();
					Map<Integer, Map<String, String>> nbtTags = new HashMap<>();

					if(config.getConfigurationSection("Tutorials." + key + "." + count + ".equipments") != null){
						int tmpCount = count;
						config.getConfigurationSection("Tutorials." + key + "." + count + ".equipments").getKeys(false).forEach((mapKey) -> {
							try{
								int slot;
								switch (mapKey.toUpperCase()) {
									case "HAND":
									case "TOOL":
										slot = SLOT_MAIN_HAND;
										break;

									case "OFFHAND":
										slot = SLOT_OFF_HAND;
										break;

									default:
										slot = Integer.parseInt(mapKey);
										break;
								}
								mats.put(
										slot,
										Material.valueOf(config.getString("Tutorials." + key + "." + tmpCount + ".equipments." + mapKey + ".material").toUpperCase())
								);
								config.getConfigurationSection("Tutorials." + key + "." + tmpCount + ".equipments." + mapKey + ".nbt").getKeys(false).forEach((nbt) -> {
									nbtTags.put(slot, new HashMap<String, String>(){{
										put(nbt, config.getString("Tutorials." + key + "." + tmpCount + ".equipments." + mapKey +  ".nbt" + nbt));
									}});
								});
							}catch (Exception ignore){
								AburaTutorial.getInstance().getLogger().info(key + ": フェーズ: " + tmpCount + ", スロット: " + mapKey + " のアイテムが正しく指定されていないようです。");
							}
						});
					}

					// クリアトリガータイプ
					Material clickMat = null;
					try {
						String str = config.getString("Tutorials." + key + "." + count + ".clickMaterial");
						if(str != null) clickMat = Material.valueOf(str);
					}catch (Exception ignore){
						AburaTutorial.getInstance().getLogger().info(key + "のクリック対象ブロックのタイプが正しく指定されていないようです。");
						AburaTutorial.getInstance().getLogger().info(Arrays.toString(TriggerType.values()) + " が使用可能です。");
					}

					// クリアトリガータイプ
					TriggerType triggerType = null;
					try {
						triggerType = TriggerType.str2Trigger(config.getString("Tutorials." + key + "." + count + ".type"));
					}catch (Exception ignore){
						AburaTutorial.getInstance().getLogger().info(key + "のトリガータイプが正しく指定されていないようです。");
						AburaTutorial.getInstance().getLogger().info(Arrays.toString(TriggerType.values()) + " が使用可能です。");
					}

					// クリアトリガー
					Predicate<Trigger> clearTrigger = (trigger) -> {

						Predicate<Player> checkNBT = (target) -> {

							Long phasePers = EntityPersistentUtils.getLong(target, PersistentKeyName.TUTORIAL_PHASE);
							if (phasePers == null) return false;
							int phase = phasePers.intValue();

							// NBTチェック
							for (int slot : trigger.getTutorial().triggerItem(phase).keySet()) {

								ItemStack item;
								switch (slot) {
									case SLOT_MAIN_HAND:
										item = target.getInventory().getItemInMainHand();
										break;

									case SLOT_OFF_HAND:
										item = target.getInventory().getItemInOffHand();
										break;

									default:
										item = target.getInventory().getItem(slot);
										break;
								}
								if (item.getType() == trigger.getTutorial().triggerItem(phase).get(slot)) {
									net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
									NBTTagCompound nbtTag = nmsItem.getTag();

									// NBTが指定されていない場合
									Map<String, String> nbts = trigger.getTutorial().itemNBT(phase).get(slot);
									if (nbts != null || nbts.size() <= 0) {
										continue;
									}

									// NBTが指定されている場合
									for (String nbtKey : nbts.keySet()) {
										if (nbtTag.getString(nbtKey) == null) return false;
										if (!nbtTag.getString(nbtKey).equals(nbts.get(nbtKey))) return false;
									}
								}else {
									return false;
								}
							}
							return true;
						};

						// クリックイベントの場合
						if (trigger.getEvent() instanceof PlayerInteractEvent) {
							PlayerInteractEvent event = (PlayerInteractEvent) trigger.getEvent();
							TriggerType tType = trigger.getTutorial().triggerType(event.getPlayer());

							Long phasePers = EntityPersistentUtils.getLong(event.getPlayer(), PersistentKeyName.TUTORIAL_PHASE);
							if (phasePers == null) return false;
							int phase = phasePers.intValue();

							// NBTチェック
							boolean chkNBT = checkNBT.test(event.getPlayer());

							TriggerType checkType = null;
							switch (event.getAction()) {
								case LEFT_CLICK_AIR:
								case LEFT_CLICK_BLOCK:
									checkType = TriggerType.LEFT_CLICK;
									break;

								case RIGHT_CLICK_AIR:
								case RIGHT_CLICK_BLOCK:
									checkType = TriggerType.RIGHT_CLICK;
									break;

								case PHYSICAL:
									checkType = TriggerType.PHYSICAL;
									break;
							}

							if(chkNBT && tType == checkType) {
								if(event.getClickedBlock() == null){
									if(tutorial.clickedType(phase) != null){
										return false;
									}
									return true;

								}else{
									if(event.getClickedBlock().getType() == tutorial.clickedType(phase)){
										return true;
									}
									return false;
								}
							}

							// ダメージイベントの場合
						} else if (trigger.getEvent() instanceof EntityDamageByEntityEvent) {
							EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) trigger.getEvent();

							// プレイヤー、発射体ではない場合は処理しない
							Player attacker = null;

							if(event.getDamager() instanceof Player){

								// プレイヤーにキャスト
								attacker = ((Player) event.getDamager());
							}else if (event.getDamager() instanceof Projectile){
								Projectile projectile = (Projectile) event.getDamager();
								if(projectile.getShooter() instanceof Player){
									attacker = (Player) projectile.getShooter();
								}
							}

							TriggerType triType = trigger.getTutorial().triggerType(attacker);
							if (triType == TriggerType.ATTACK) {
								// NBTチェック
								boolean chkNBT = checkNBT.test(attacker);
								if(chkNBT){
									if(trigger.getTutorial().attackTrigger(attacker) == null) return true;
									if(trigger.getTutorial().attackTrigger(attacker) == event.getCause()) return true;
								}
								return false;
							}
						}

						return false;
					};

					// 攻撃タイプ
					EntityDamageEvent.DamageCause damageCause = null;
					try {
						String cause = config.getString("Tutorials." + key + "." + count + ".attackType");
						if(cause != null) damageCause = EntityDamageEvent.DamageCause.valueOf(cause);
					}catch (Exception ignore){
						AburaTutorial.getInstance().getLogger().info(key + "の攻撃タイプが正しく指定されていないようです。");
						AburaTutorial.getInstance().getLogger().info(Arrays.toString(EntityDamageEvent.DamageCause.values()) + " が使用可能です。");
					}

					// チュートリアルセット
					tutorial.startMessage(count, config.getStringList("Tutorials." + key + "." + count + ".start.message"))
							.startCommand(count, config.getStringList("Tutorials." + key + "." + count + ".start.command"))
							.endMessage(count, config.getStringList("Tutorials." + key + "." + count + ".end.message"))
							.endCommand(count, config.getStringList("Tutorials." + key + "." + count + ".end.command"))
							.triggerItem(count, mats)
							.itemNBT(count, nbtTags)
							.triggerType(count, triggerType)
							.clickedType(count, clickMat)
							.clearTrigger(count, clearTrigger)
							.attackTrigger(count, damageCause);

					count++;
				}

				// 名前を得る
				String name = config.getString("Tutorials." + key + ".name");
				if(name == null) name = key;

				tutorials.put(key, tutorial.name(key)
						.displayName(name)
						.endMessage(config.getStringList("Tutorials." + key + ".endMessage"))
				);
			});
		}else{
			config.set("Tutorials", null);
			config.set("Tutorials.SampleTutorial.name", "SampleTutorial");
			config.set("Tutorials.SampleTutorial.1.type", "LCLICK");
			config.set("Tutorials.SampleTutorial.1.equipments.material", "PAPER");
			config.set("Tutorials.SampleTutorial.1.itemNBT.CustomModelData", 1);
			config.set("Tutorials.SampleTutorial.1.itemNBT.skript:item.box", 1);
			config.set("Tutorials.SampleTutorial.1.itemNBT.skript:item.id",  1);
			config.set("Tutorials.SampleTutorial.1.start.message", new String[]{"This tutorial is sample."});
			config.set("Tutorials.SampleTutorial.1.start.message", new String[]{"$give {name} paper"});
			config.set("Tutorials.SampleTutorial.1.end.message", new String[]{"Marvelous! Tutorial clear!"});
			config.set("Tutorials.SampleTutorial.1.end.message", new String[]{"$teleport {name} ~ ~ ~"});
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// チュートリアルを取得する
	public static Tutorial get(String key){
		return tutorials.get(key);
	}
	public static Set<String> getAllTutorialName(){
		return tutorials.keySet();
	}
	// チュートリアルの存在確認
	public static boolean exists(String key){
		return tutorials.containsKey(key);
	}
	public static void unload(){
		tutorials.clear();
	}

	// 指定パス下のjarファイル一覧を得る
	private static String getJarFiles(String path){
		return Arrays.stream(new File(path).listFiles()).filter(
				f -> f.getName().endsWith(".jar")
		).map(
				f -> f.getAbsoluteFile() + ";"
		).collect(
				StringBuilder::new,
				StringBuilder::append,
				StringBuilder::append
		).toString();
	}

	// プレイヤーがプレイしているチュートリアルを得る
	@Nullable
	public static Tutorial getPlayingTutorial(Player player){
		String tutorial = EntityPersistentUtils.getString(player, PersistentKeyName.TUTORIAL);
		if(exists(tutorial)) return get(tutorial);
		return null;
	}
}
