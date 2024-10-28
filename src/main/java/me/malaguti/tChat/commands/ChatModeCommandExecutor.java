package me.malaguti.tChat.commands;

import me.malaguti.tChat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ChatModeCommandExecutor implements CommandExecutor {
    private final TChat plugin;

    public ChatModeCommandExecutor(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("chat")) {
            if(sender instanceof Player player) {
                if(args.length > 0) {
                    String mode = args[0].toLowerCase();
                    if(mode.equals("global") || mode.equals("local")) {
                        plugin.setPlayerChatMode(player, mode);
                        String message = Objects.requireNonNull(plugin.getConfigMessages().getString("chat_mode_set"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message) + mode);
                    } else {
                        String message = Objects.requireNonNull(plugin.getConfigMessages().getString("warn_mode_set"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                } else {
                    String message = Objects.requireNonNull(plugin.getConfigMessages().getString("warn_mode_set"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            } else {
                sender.sendMessage("§cOnly players can use this command.");
            }
            return true;
        }
        return false;
    }
}
