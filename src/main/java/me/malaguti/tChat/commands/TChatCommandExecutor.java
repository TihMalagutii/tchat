package me.malaguti.tChat.commands;

import me.malaguti.tChat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class TChatCommandExecutor implements CommandExecutor {
    private final TChat plugin;

    public TChatCommandExecutor(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("tchat")) {
            if(sender.hasPermission("tchat.admin")) {
                if(args.length == 0) {
                    String message = Objects.requireNonNull(plugin.getConfigMessages().getString("unknown_command"));
                    sender.sendMessage("§6[TChat] " + ChatColor.translateAlternateColorCodes('&', message));
                    return true;
                }

                if("reload".equalsIgnoreCase(args[0])) {
                    plugin.reloadConfig();
                    plugin.createMainConfig();
                    plugin.loadMessagesConfig();
                    String message = Objects.requireNonNull(plugin.getConfigMessages().getString("reload_msg"));
                    sender.sendMessage("§6[TChat] " + ChatColor.translateAlternateColorCodes('&', message));
                } else if("help".equalsIgnoreCase(args[0])) {
                    sender.sendMessage("§6[TChat] §7Available commands:\n §f- §2/g <message> §f: Global chat\n §f- §2/tell <player> <message> §f: Private message\n §f- §2/chat <global | local> §f: Toggle chat mode\n §f- §2/tchat reload §f: Reload plugin config.");
                } else {
                    String message = Objects.requireNonNull(plugin.getConfigMessages().getString("unknown_command"));
                    sender.sendMessage("§6[TChat] " + ChatColor.translateAlternateColorCodes('&', message));
                }
                return true;
            } else {
                String message = Objects.requireNonNull(plugin.getConfigMessages().getString("no_permission_msg"));
                sender.sendMessage("§6[TChat] " + ChatColor.translateAlternateColorCodes('&', message));
                return true;
            }
        }
        return true;
    }
}
