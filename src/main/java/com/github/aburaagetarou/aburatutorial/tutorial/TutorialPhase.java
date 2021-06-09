package com.github.aburaagetarou.aburatutorial.tutorial;

import com.github.aburaagetarou.aburatutorial.trigger.Trigger;
import com.github.aburaagetarou.aburatutorial.trigger.TriggerType;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TutorialPhase {

	protected List<String> startMsg = new ArrayList<>();
	protected List<String> startCmd = new ArrayList<>();
	protected List<String> endMsg = new ArrayList<>();
	protected List<String> endCmd = new ArrayList<>();
	protected Material clickType;

	protected Map<Integer, Map<String, String>> itemNBT = new HashMap<>();
	protected Map<Integer, Material> itemMaterial = new HashMap<>();

	protected Predicate<Trigger> trigger;

	protected TriggerType triggerType;
	protected EntityDamageEvent.DamageCause attackType;
}
