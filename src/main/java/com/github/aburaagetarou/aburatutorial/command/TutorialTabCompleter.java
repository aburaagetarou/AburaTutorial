package com.github.aburaagetarou.aburatutorial.command;

import com.github.aburaagetarou.aburatutorial.tutorial.Tutorials;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TutorialTabCompleter implements TabCompleter {

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		List<String> ret = new ArrayList<>();
		if(sender instanceof Player) {
			Player player = ((Player) sender);
			if (args.length <= 0) {
				ret.addAll(TutorialCommand.arguments.keySet().stream().filter(
						key -> player.hasPermission(TutorialCommand.arguments.get(key))
				).toList());
			} else {
				switch (args[0].toLowerCase()) {
					case TutorialCommand.RELOAD:
						if (!player.hasPermission("aburatutorial.reload")) return ret;
						break;

					case TutorialCommand.START:
						if (!player.hasPermission("aburatutorial.start")) return ret;
						switch (args.length) {
							case 2:
								ret.addAll(Tutorials.getAllTutorialName());
								break;

							case 3:
								Bukkit.getOnlinePlayers().forEach((online) -> {
									ret.add(online.getName());
								});
								break;
						}
						break;

					case TutorialCommand.END:
						if (!player.hasPermission("aburatutorial.end")) return ret;
						if (args.length == 2) {
							Bukkit.getOnlinePlayers().forEach((online) -> {
								ret.add(online.getName());
							});
						}
						break;

					case TutorialCommand.PHASE:
						if (!player.hasPermission("aburatutorial.phase")) return ret;
						switch (args.length) {
							case 2:
								Bukkit.getOnlinePlayers().forEach((online) -> {
									ret.add(online.getName());
								});
								break;

							case 3:
								Player target = Bukkit.getPlayer(args[1]);
								if(target == null || Tutorials.getPlayingTutorial(target) == null) break;
								ret.addAll(Tutorials.getPlayingTutorial(target).getPhases().stream().map(
										phase -> "" + phase
								).collect(Collectors.toList()));
								break;
						}
						break;

					default:
						if(args.length == 1){
							ret.addAll(TutorialCommand.arguments.keySet().stream().filter(
									key -> player.hasPermission(TutorialCommand.arguments.get(key))
							).collect(Collectors.toList()));
						}
				}
			}
		}
		return ret;
	}
}
