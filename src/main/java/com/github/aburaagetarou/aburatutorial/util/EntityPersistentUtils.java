package com.github.aburaagetarou.aburatutorial.util;

import com.github.aburaagetarou.aburatutorial.AburaTutorial;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;

/**
 * Persistentを取得、セット、除去する
 * @author AburaAgeTarou
 */
public class EntityPersistentUtils {

	// 整数の値を返す
	@Nullable
	public static Long getLong(Entity entity, String keyName){
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		return entity.getPersistentDataContainer().get(key, PersistentDataType.LONG);
	}

	// 整数の値をセット
	public static void setLong(Entity entity, String keyName, long value){
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		entity.getPersistentDataContainer().set(key, PersistentDataType.LONG, value);
	}

	// 文字列の値を返す
	@Nullable
	public static String getString(Entity entity, String keyName){
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		return entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
	}

	// 文字列の値をセット
	public static void setString(Entity entity, String keyName, String value){
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
	}

	// 浮動小数点数の値を返す
	@Nullable
	public static Double getDouble(Entity entity, String keyName){
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		return entity.getPersistentDataContainer().get(key, PersistentDataType.DOUBLE);
	}

	// 浮動小数点数の値をセット
	public static void setDouble(Entity entity, String keyName, double value){
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		entity.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, value);
	}

	// 真偽値の値を返す
	public static Boolean getBoolean(Entity entity, String keyName){
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		return (entity.getPersistentDataContainer().get(key, PersistentDataType.SHORT) != null);
	}

	// 真偽値の値をセット
	public static void setBoolean(Entity entity, String keyName, boolean value){
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		entity.getPersistentDataContainer().set(key, PersistentDataType.SHORT, (short)(value ? 1 : 0));
	}

	// 除去
	public static void remove(Entity entity, String keyName) {
		NamespacedKey key = new NamespacedKey(AburaTutorial.getInstance(), keyName);
		entity.getPersistentDataContainer().remove(key);
	}
}
