package me.malaguti.tChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;

public class LocalChatListener implements Listener {

    private final TChat plugin; // Variável para armazenar a instância do plugin

    // Construtor que aceita uma instância do plugin
    public LocalChatListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        // Verifica o chat padrão do jogador
        String chatMode = plugin.getPlayerChatMode(sender);

        if(chatMode.equals("global")) {
            event.setCancelled(true); // Cancela o evento para que não seja enviado no chat local
            if(sender.hasPermission("tchat.colors")) {
                // Permite que os jogadores usem cores nas mensagem
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            String prefix = plugin.getPrefix(sender);
            String globalMessage = Objects.requireNonNull(plugin.getConfigMessages().getString("global_chat"))
                    .replace("%player%", sender.getName())
                    .replace("%prefix%", prefix);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', globalMessage) + message);
        } else {
            // Chat Local (padrão)
            if(sender.hasPermission("tchat.colors")) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            boolean hasPlayers = false;
            // Limita a mensagem aos jogadores dentro do raio especificado
            event.getRecipients().clear(); // Limpa todos os destinatarios padrão
            for(Player player : sender.getWorld().getPlayers()) {
                // Limite de distancia em blocos
                if(player != sender && player.getLocation().distance(sender.getLocation()) <= plugin.getChatRange()) {
                    event.getRecipients().add(player); // Adiciona somente os jogadores proximos
                    event.getRecipients().add(sender);
                    hasPlayers = true;
                }
            }

            String prefix = plugin.getPrefix(sender);
            if(hasPlayers) {
                // Formata a mensagem no chat local

                String localMessage = Objects.requireNonNull(plugin.getConfigMessages().getString("local_chat"))
                        .replace("%player%", sender.getName())
                        .replace("%prefix%", prefix);
                localMessage = ChatColor.translateAlternateColorCodes('&', localMessage);
                event.setFormat(localMessage + message);
                // event.setFormat("§e[L] §f" + sender.getName() + "§e: " + message);
            } else {
                String localMessage = Objects.requireNonNull(plugin.getConfigMessages().getString("local_chat"))
                        .replace("%player%", sender.getName())
                        .replace("%prefix%", prefix);
                localMessage = ChatColor.translateAlternateColorCodes('&', localMessage);
                sender.sendMessage(localMessage + message);
                // sender.sendMessage("§e[L] §f" + sender.getName() + "§e: " + message);

                String errorMessage = Objects.requireNonNull(plugin.getConfigMessages().getString("no_players_near"));
                String formattedMessage = ChatColor.translateAlternateColorCodes('&', errorMessage);
                sender.sendMessage(formattedMessage);
                // sender.sendMessage("§cNão tem ninguém perto para ouvir sua mensagem");
            }
        }

    }

}
