package com.github.aburaagetarou.aburatutorial.trigger;

import com.github.aburaagetarou.aburatutorial.tutorial.Tutorial;
import org.bukkit.event.Event;

/**
 * トリガーの条件確認に渡されるトリガー
 * @author AburaAgeTarou
 */
public class Trigger {

	Event event;
	Tutorial tutorial;

	public Trigger(Event event, Tutorial tutorial){
		this.event = event;
		this.tutorial = tutorial;
	}

	public Event getEvent() {
		return event;
	}

	public Tutorial getTutorial() {
		return tutorial;
	}
}
