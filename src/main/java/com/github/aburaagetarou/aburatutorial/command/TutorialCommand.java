package com.github.aburaagetarou.aburatutorial.command;

import com.github.aburaagetarou.aburatutorial.AburaTutorial;
import com.github.aburaagetarou.aburatutorial.tutorial.Tutorials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


/**
 * チュートリアル用コマンド
 * @author AburaAgeTarou
 */
public class TutorialCommand implements CommandExecutor {

	protected final static String RELOAD = "reload";
	protected final static String START = "start";
	protected final static String END = "end";
	protected final static String PHASE = "phase";

	protected final static Map<String, String> arguments = new HashMap<String, String>(){{
		put(RELOAD, "aburatutorial." + RELOAD);
		put(START,  "aburatutorial." + START);
		put(END,    "aburatutorial." + END);
		put(PHASE,  "aburatutorial." + PHASE);
	}};
	private final Map<String, TextComponent> details = new HashMap<String, TextComponent>(){{
		put(RELOAD, Component.text("チュートリアル設定ファイルのリロードを行います。").color(TextColor.color(NamedTextColor.GREEN)));
		put(START, TextComponent.ofChildren(
				Component.text("<チュートリアル名> <プレイヤー名>").color(TextColor.color(NamedTextColor.AQUA)),
				Component.text(" 次の引数で指定したチュートリアルを1から開始します。").color(TextColor.color(NamedTextColor.GREEN))
		));
		put(END, TextComponent.ofChildren(
				Component.text("<プレイヤー名>").color(TextColor.color(NamedTextColor.AQUA)),
				Component.text(" 指定プレイヤーがプレイ中のチュートリアルを終わらせます。").color(TextColor.color(NamedTextColor.GREEN))
		));
		put(PHASE, TextComponent.ofChildren(
				Component.text("<フェーズ番号> <プレイヤー名>").color(TextColor.color(NamedTextColor.AQUA)),
				Component.text(" 指定プレイヤーがプレイ中のチュートリアルフェーズを変更します。").color(TextColor.color(NamedTextColor.GREEN))
		));
	}};

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player){
			Player player = ((Player) sender);
			if(args.length <= 0){
				if(!player.hasPermission("aburatutorial.help")) {
					player.sendMessage(TextComponent.ofChildren(
							AburaTutorial.MESSAGE_PREFIX,
							Component.text("コマンドを使用する権限がありません。").color(TextColor.color(NamedTextColor.RED)))
					);
					return false;
				}
				sendHelp(player);
			}else{
				switch(args[0].toLowerCase()){
					case RELOAD:
						if(!player.hasPermission("aburatutorial.reload")) {
							player.sendMessage(TextComponent.ofChildren(
									AburaTutorial.MESSAGE_PREFIX,
									Component.text("コマンドを使用する権限がありません。").color(TextColor.color(NamedTextColor.RED)))
							);
							return false;
						}
						Tutorials.unload();
						Tutorials.load(AburaTutorial.getTutorialFile());
						AburaTutorial.getPluginConfig().load();
						player.sendMessage(TextComponent.ofChildren(
								AburaTutorial.MESSAGE_PREFIX,
								Component.text("チュートリアルをリロードしました！").color(TextColor.color(NamedTextColor.GREEN)))
						);
						break;

					case START:
						if(!player.hasPermission("aburatutorial.start")) {
							player.sendMessage(TextComponent.ofChildren(
									AburaTutorial.MESSAGE_PREFIX,
									Component.text("コマンドを使用する権限がありません。").color(TextColor.color(NamedTextColor.RED)))
							);
							return false;
						}
						if(args.length >= 3) {
							if(Tutorials.exists(args[1])){
								Player target = Bukkit.getPlayer(args[2]);
								if(target == null) {
									player.sendMessage(TextComponent.ofChildren(
											AburaTutorial.MESSAGE_PREFIX,
											Component.text("指定したプレイヤーはオンラインではありません。").color(TextColor.color(NamedTextColor.RED)))
									);
									return false;
								}
								Tutorials.get(args[1]).start(target);
							}else{
								player.sendMessage(TextComponent.ofChildren(
										AburaTutorial.MESSAGE_PREFIX,
										Component.text("指定したチュートリアルは存在しません。").color(TextColor.color(NamedTextColor.RED)))
								);
								return false;
							}
						}else{
							player.sendMessage(TextComponent.ofChildren(
									AburaTutorial.MESSAGE_PREFIX,
									Component.text("チュートリアル名を指定してください。").color(TextColor.color(NamedTextColor.RED)))
							);
							return false;
						}
						break;

					case END:
						if(!player.hasPermission("aburatutorial.end")) {
							player.sendMessage(TextComponent.ofChildren(
									AburaTutorial.MESSAGE_PREFIX,
									Component.text("コマンドを使用する権限がありません。").color(TextColor.color(NamedTextColor.RED)))
							);
							return false;
						}
						if(args.length >= 2) {
							if(Tutorials.exists(args[1])){
								Player target = Bukkit.getPlayer(args[2]);
								if(target == null) {
									player.sendMessage(TextComponent.ofChildren(
											AburaTutorial.MESSAGE_PREFIX,
											Component.text("指定したプレイヤーはオンラインではありません。").color(TextColor.color(NamedTextColor.RED)))
									);
									return false;
								}
								if(Tutorials.getPlayingTutorial(target) == null) {
									player.sendMessage(TextComponent.ofChildren(
											AburaTutorial.MESSAGE_PREFIX,
											Component.text("対象はチュートリアルをプレイしていません。").color(TextColor.color(NamedTextColor.RED)))
									);
									return false;
								}
								Tutorials.getPlayingTutorial(target).end(target);
							}else{
								player.sendMessage(TextComponent.ofChildren(
										AburaTutorial.MESSAGE_PREFIX,
										Component.text("指定したチュートリアルは存在しません。").color(TextColor.color(NamedTextColor.RED)))
								);
							}
						}else{
							player.sendMessage(TextComponent.ofChildren(
									AburaTutorial.MESSAGE_PREFIX,
									Component.text("チュートリアル名を指定してください。").color(TextColor.color(NamedTextColor.RED)))
							);
						}
						break;

					case PHASE:
						if(!player.hasPermission("aburatutorial.phase")) {
							player.sendMessage(TextComponent.ofChildren(
									AburaTutorial.MESSAGE_PREFIX,
									Component.text("コマンドを使用する権限がありません。").color(TextColor.color(NamedTextColor.RED)))
							);
							return false;
						}
						if(args.length >= 3) {
							Player target = Bukkit.getPlayer(args[1]);
							if(target == null) {
								player.sendMessage(TextComponent.ofChildren(
										AburaTutorial.MESSAGE_PREFIX,
										Component.text("指定したプレイヤーはオンラインではありません。").color(TextColor.color(NamedTextColor.RED)))
								);
								return false;
							}
							if(Tutorials.getPlayingTutorial(target) == null) {
								player.sendMessage(TextComponent.ofChildren(
										AburaTutorial.MESSAGE_PREFIX,
										Component.text("対象はチュートリアルをプレイしていません。").color(TextColor.color(NamedTextColor.RED)))
								);
								return false;
							}
							try {
								Tutorials.getPlayingTutorial(target).setPhase(target, Integer.parseInt(args[2]));
							}catch (Exception e){
								player.sendMessage(TextComponent.ofChildren(
										AburaTutorial.MESSAGE_PREFIX,
										Component.text("フェーズ指定が間違っています。").color(TextColor.color(NamedTextColor.RED)))
								);
								return false;
							}
						}else{
							player.sendMessage(TextComponent.ofChildren(
									AburaTutorial.MESSAGE_PREFIX,
									Component.text("引数が足りません。").color(TextColor.color(NamedTextColor.RED)))
							);
						}
						break;

					default:
						if(!player.hasPermission("aburatutorial.help")) {
							player.sendMessage(TextComponent.ofChildren(
									AburaTutorial.MESSAGE_PREFIX,
									Component.text("コマンドを使用する権限がありません。").color(TextColor.color(NamedTextColor.RED)))
							);
							return false;
						}
						sendHelp(player);
						break;
				}
			}
		}
		return true;
	}

	// ヘルプを表示する
	private void sendHelp(Player player){
		arguments.forEach((arg, value) -> {
			if(!player.hasPermission(value)) return;
			player.sendMessage(TextComponent.ofChildren(
					AburaTutorial.MESSAGE_PREFIX,
					Component.text(arg + " ").color(TextColor.color(NamedTextColor.YELLOW)),
					details.get(arg)
			));
		});
	}
}
