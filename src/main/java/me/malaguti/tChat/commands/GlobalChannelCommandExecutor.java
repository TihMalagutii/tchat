package me.malaguti.tChat.commands;

import me.malaguti.tChat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class GlobalChannelCommandExecutor implements CommandExecutor {
    private final TChat plugin;
    private final HashMap<UUID, Long> chatCooldown = new HashMap<>();
    private  int globalCooldownTime;

    public GlobalChannelCommandExecutor(TChat plugin) {
        this.plugin = plugin;
        globalCooldownTime = plugin.getConfig().getInt("global_chat_cooldown", 2);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("g")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(!player.hasPermission("tchat.bypass.cooldown")) {
                    if(chatCooldown.containsKey(player.getUniqueId())) {
                        long lastUsageTime = chatCooldown.get(player.getUniqueId());
                        long timeSinceLastUse = (System.currentTimeMillis() - lastUsageTime) / 1000;

                        if(timeSinceLastUse < globalCooldownTime) {
                            int timeLeft = globalCooldownTime - (int) timeSinceLastUse;
                            String message = Objects.requireNonNull(plugin.getConfigMessages().getString("global_cooldown_message"))
                                    .replace("%globalCooldown%", String.valueOf(timeLeft));
                            message = ChatColor.translateAlternateColorCodes('&', message);
                            player.sendMessage(message);
                            return true;
                        }
                    }
                    // Atualiza o tempo do ultimo uso
                    chatCooldown.put(player.getUniqueId(), System.currentTimeMillis());
                }

                if(args.length > 0) {
                    String message = String.join(" ", args);
                    plugin.sendGlobalMessage(player, message);
                    return true;
                } else {
                    String errorMessage = Objects.requireNonNull(plugin.getConfigMessages().getString("error_message_global"));
                    String formattedMessage = ChatColor.translateAlternateColorCodes('&', errorMessage);
                    player.sendMessage(formattedMessage);
                    return true;
                }
            } else {
                sender.sendMessage("§cOnly players can use this command.");
                return false;
            }
        }
        return false;
    }
}
