package me.malaguti.tChat.commands;

import me.malaguti.tChat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PrivateChannelCommandExecutor implements CommandExecutor {
    private final TChat plugin;

    public PrivateChannelCommandExecutor(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("tell")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(args.length >= 2) {
                    // Obtendo o nome do jogador alvo e a mensagem
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target != null && target.isOnline()) {
                        // Verifica se o jogador não está enviando uma mensagem para si mesmo
                        if(target.getName().equals(player.getName())) {
                            String message = Objects.requireNonNull(plugin.getConfigMessages().getString("error_private_message_sender"));
                            message = ChatColor.translateAlternateColorCodes('&', message);
                            player.sendMessage(message);
                            return true;
                        }

                        // Concatena o resto dos argumentos para formar a mensagem
                        String message = String.join(" ", args).substring(args[0].length()).trim();

                        if(player.hasPermission("tchat.colors")) {
                            message = ChatColor.translateAlternateColorCodes('&', message);
                        }

                        // Quem está enviando a mensagem
                        String privateMessageSender = Objects.requireNonNull(plugin.getConfigMessages().getString("private_message_sender"))
                                .replace("%player%", target.getName());
                        privateMessageSender = ChatColor.translateAlternateColorCodes('&', privateMessageSender);
                        player.sendMessage(privateMessageSender, message);

                        // Quem está recebendo a mensagem
                        String privateMessageReceiver = Objects.requireNonNull(plugin.getConfigMessages().getString("private_message_receiver"))
                                .replace("%player%", player.getName());
                        privateMessageReceiver = ChatColor.translateAlternateColorCodes('&', privateMessageReceiver);
                        target.sendMessage(privateMessageReceiver, message);
                        return true;
                    } else {
                        String errorMessage = Objects.requireNonNull(plugin.getConfigMessages().getString("player_not_found"));
                        errorMessage = ChatColor.translateAlternateColorCodes('&', errorMessage);
                        player.sendMessage(errorMessage);
                        return true;
                    }
                } else {
                    String errorMessage = Objects.requireNonNull(plugin.getConfigMessages().getString("error_message"));
                    errorMessage = ChatColor.translateAlternateColorCodes('&', errorMessage);
                    player.sendMessage(errorMessage);
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
