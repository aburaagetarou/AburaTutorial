package com.github.aburaagetarou.aburatutorial.trigger;

import com.github.aburaagetarou.aburatutorial.AburaTutorial;

import java.util.Arrays;
import java.util.Optional;

/**
 * トリガーのタイプ
 * @author AburaAgeTarou
 */
public enum TriggerType {

	LEFT_CLICK("LCLICK", 1),
	RIGHT_CLICK("RCLICK", 2),
	ATTACK("ATK", 3),
	PHYSICAL("PHYSICAL", 4);

	private final String altName;	// 短縮名
	private final int id;			// ID

	// コンストラクタ
	TriggerType(String altName, int id) {
		this.altName = altName;
		this.id = id;
	}

	// 短縮名を得る
	public String getAltName() {
		return altName;
	}

	// 文字列からトリガータイプを得る
	public static TriggerType str2Trigger(String value){
		try {
			return valueOf(value);
		}catch (Exception e){

			// AltNameの中から探す
			Optional<TriggerType> type = Arrays.stream(TriggerType.values()).filter(
					(triggerType -> triggerType.getAltName().equals(value))
			).findFirst();

			if(type.isPresent()) return type.get();

			// 見つからなかった場合
			e.printStackTrace();
			AburaTutorial.getInstance().getLogger().warning("トリガータイプが正しく指定されていません。");
			AburaTutorial.getInstance().getLogger().warning("指定可能なのは、LEFT_CLICK(LCLICK), RIGHT_CLICK(RCLICK), ATTACK(ATK)");
		}
		return null;
	}

	// IDを得る
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return name() + "," + altName;
	}
}
