package me.malaguti.tChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class TChat extends JavaPlugin {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        getLogger().info("\\u001B[32mTchat enabled!\\u001B[0m");

        createConfig();

        // Registrando o comando /g
        Objects.requireNonNull(getCommand("g")).setExecutor(this);
        Objects.requireNonNull(getCommand("tell")).setExecutor(this);

        // Registrando o listener de chat local
        getServer().getPluginManager().registerEvents(new LocalChatListener(this), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("\u001B[31mTChat disabled!\u001B[0m");
    }

    private void createConfig() {
        // Cria a pasta se não existir
        File configFolder = new File(getDataFolder(), "messages");
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        // Cria o arquivo de configuração se não existir
        File configFile = new File(configFolder, "pt-BR.yml");
        if (!configFile.exists()) {
            saveResource("messages/pt-BR.yml", false); // Salva o arquivo padrão no jar
        }

        // Carrega o arquivo de configuração
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfigMessages() {
        return config;
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

                    String globalMessage = Objects.requireNonNull(getConfigMessages().getString("global_chat"))
                            .replace("%player%", player.getName());
                    globalMessage = ChatColor.translateAlternateColorCodes('&', globalMessage);
                    Bukkit.broadcastMessage(globalMessage + message);
                    // Bukkit.broadcastMessage("§7[G] §f" + player.getName() + "§7: " + message);
                    return true;
                } else {
                    String errorMessage = Objects.requireNonNull(getConfigMessages().getString("error_message_global"));
                    String formattedMessage = ChatColor.translateAlternateColorCodes('&', errorMessage);
                    player.sendMessage(formattedMessage);
                    // player.sendMessage("§cUso correto: /g <mensagem>");
                    return true;
                }
            } else {
                sender.sendMessage("§cOnly players can use this command.");
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
                            String message = Objects.requireNonNull(getConfigMessages().getString("error_private_message_sender"));
                            String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
                            player.sendMessage(formattedMessage);
                            // player.sendMessage("§cVocê não pode enviar uma mensagem privada para si mesmo.");
                            return true;
                        }

                        // Concatena o resto dos argumentos para formar a mensagem
                        String message = String.join(" ", args).substring(args[0].length()).trim();
                        // Verifica se o jogador tem a permissão
                        if(player.hasPermission("tchat.colors")) {
                            message = ChatColor.translateAlternateColorCodes('&', message);
                        }

                        // Quem está enviando a mensagem
                        String privateMessageSender = Objects.requireNonNull(getConfigMessages().getString("private_message_sender"))
                                        .replace("%player%", target.getName());
                        privateMessageSender = ChatColor.translateAlternateColorCodes('&', privateMessageSender);
                        player.sendMessage(privateMessageSender, message);

                        // Quem está recebendo a mensagem
                        String privateMessageReceiver = Objects.requireNonNull(getConfigMessages().getString("private_message_receiver"))
                                .replace("%player%", player.getName());
                        privateMessageReceiver = ChatColor.translateAlternateColorCodes('&', privateMessageReceiver);
                        target.sendMessage(privateMessageReceiver, message);
                        // target.sendMessage("§2[Privado] §7De §8" + player.getName() + "§7: " + message);
                        // player.sendMessage("§2[Privado] §7Para §8" + target.getName() + "§7: " + message);
                        return true;
                    } else {
                        String errorMessage = Objects.requireNonNull(getConfigMessages().getString("player_not_found"));
                        String formattedMessage = ChatColor.translateAlternateColorCodes('&', errorMessage);
                        player.sendMessage(formattedMessage);
                        // player.sendMessage("§cJogador não encontrado ou offline.");
                        return true;
                    }
                } else {
                    String errorMessage = Objects.requireNonNull(getConfigMessages().getString("error_message"));
                    String formattedMessage = ChatColor.translateAlternateColorCodes('&', errorMessage);
                    player.sendMessage(formattedMessage);
                    // player.sendMessage("§cUso correto: /tell <player> <mensagem>");
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
