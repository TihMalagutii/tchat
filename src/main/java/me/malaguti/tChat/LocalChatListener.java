package me.malaguti.tChat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LocalChatListener implements Listener {

    private final int chatRange = 100; // Limite de distancia em blocos

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        // Verifica se o jogador tem a permissão
        if(sender.hasPermission("tchat.colors")) {
            // Permite que os jogadores usem cores nas mensagem
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        boolean hasPlayers = false;

        // Limita a mensagem aos jogadores dentro do raio especificado
        event.getRecipients().clear(); // Limpa todos os destinatarios padrão
        for(Player player : sender.getWorld().getPlayers()) {
            if(player != sender && player.getLocation().distance(sender.getLocation()) <= chatRange) {
                event.getRecipients().add(player); // Adiciona somente os jogadores proximos
                event.getRecipients().add(sender);
                hasPlayers = true;
            }
        }

        if(hasPlayers) {
            // Formata a mensagem no chat local
            event.setFormat("§e[L] §f" + sender.getName() + "§e: " + message);
        } else {
            sender.sendMessage("§e[L] §f" + sender.getName() + "§e: " + message);
            sender.sendMessage("§cNão tem ninguém perto para ouvir sua mensagem");
        }


    }

}
