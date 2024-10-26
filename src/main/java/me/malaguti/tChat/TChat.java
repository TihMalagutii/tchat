package me.malaguti.tChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class TChat extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("\\u001B[32mTchat plugin habilitado!\\u001B[0m");

        // Registrando o comando /g
        getCommand("g").setExecutor(this);
        getCommand("tell").setExecutor(this);

        // Registrando o listener de chat local
        getServer().getPluginManager().registerEvents(new LocalChatListener(), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("\u001B[31mTChat plugin desabilitado!\u001B[0m");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("g")) {
            // Comando /g - Chat Global
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(args.length > 0) {
                    String message = String.join(" ", args);
                    // Verifica se o jogador tem a permissão
                    if(player.hasPermission("tchat.colors")) {
                        message = ChatColor.translateAlternateColorCodes('&', message); // Adiciona cores à mensage
                    }
                    Bukkit.broadcastMessage("§7[G] §f" + player.getName() + "§7: " + message);
                    return true;
                } else {
                    player.sendMessage("§cUso correto: /g <mensagem>");
                    return true;
                }
            } else {
                sender.sendMessage("Apenas jogadores podem usar este comando.");
                return false;
            }
        } else if(command.getName().equalsIgnoreCase("tell")) {
            // Comando /tell - Mensagem privada &2[Privado] &7Para &8Tihghnari&2: &7oi
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(args.length >= 2) {
                    // Obtendo o nome do jogador alvo e a mensagem
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target != null && target.isOnline()) {
                        // Verifica se o jogador não está enviando uma mensagem para si mesmo
                        if(target.getName().equals(player.getName())) {
                            player.sendMessage("§cVocê não pode enviar uma mensagem privada para si mesmo.");
                            return true;
                        }

                        // Concatena o resto dos argumentos para formar a mensagem
                        String message = String.join(" ", args).substring(args[0].length()).trim();
                        // Verifica se o jogador tem a permissão
                        if(player.hasPermission("tchat.colors")) {
                            message = ChatColor.translateAlternateColorCodes('&', message);
                        }
                        target.sendMessage("§2[Privado] §7De §8" + player.getName() + "§7: " + message);
                        player.sendMessage("§2[Privado] §7Para §8" + target.getName() + "§7: " + message);
                        return true;
                    } else {
                        player.sendMessage("§cJogador não encontrado ou offline.");
                        return true;
                    }
                } else {
                    player.sendMessage("§cUso correto: /tell <player> <mensagem>");
                    return true;
                }
            } else {
                sender.sendMessage("Apenas jogadores podem usar este comando.");
                return false;
            }
        }
        return false;
    }
}
